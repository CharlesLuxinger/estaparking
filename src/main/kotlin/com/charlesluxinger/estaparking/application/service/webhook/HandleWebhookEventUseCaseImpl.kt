package com.charlesluxinger.estaparking.application.service.webhook

import com.charlesluxinger.estaparking.domain.billing.BillingRecord
import com.charlesluxinger.estaparking.domain.billing.PricingSnapshot
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.PublishedLifecycleEvent
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.port.inbound.entry.EntryCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.parked.ParkedCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRecordRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.LifecycleEventPublisherPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.PricingSnapshotRepositoryPort
import com.charlesluxinger.estaparking.domain.pricing.PricingPolicy
import com.charlesluxinger.estaparking.domain.result.fold
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.Duration
import java.time.Instant

class HandleWebhookEventUseCaseImpl(
    private val parkingSessionRepositoryPort: ParkingSessionRepositoryPort,
    private val parkingEventRepositoryPort: ParkingEventRepositoryPort,
    private val entryCommandPort: EntryCommandPort,
    private val parkedCommandPort: ParkedCommandPort,
    private val billingRepositoryPort: BillingRepositoryPort,
    private val pricingSnapshotRepositoryPort: PricingSnapshotRepositoryPort,
    private val billingRecordRepositoryPort: BillingRecordRepositoryPort,
    private val lifecycleEventPublisherPort: LifecycleEventPublisherPort,
    private val clock: Clock = Clock.systemUTC(),
) : WebhookEventCommandPort {
    companion object {
        private const val FREE_PARKING_MINUTES = 30
        private const val MINUTES_PER_HOUR = 60
    }

    override fun handle(command: WebhookEventCommand): WebhookEventOutcome {
        val currentParking =
            parkingSessionRepositoryPort.findById(command.parkingId)
                ?: return WebhookEventOutcome.NotFound

        return when {
            isDuplicateForActiveSession(command) -> WebhookEventOutcome.IgnoredDuplicate
            else -> processEventTransition(currentParking, command)
        }
    }

    private fun processEventTransition(
        currentParking: Parking,
        command: WebhookEventCommand,
    ): WebhookEventOutcome {
        val vehicle = Vehicle(command.licensePlate)
        val transitionResult =
            when (command.eventType) {
                EventType.ENTRY -> entryCommandPort.execute(currentParking, vehicle)
                EventType.PARKED -> parkedCommandPort.execute(currentParking, vehicle)
                EventType.EXIT -> currentParking.apply(eventType = EventType.EXIT, vehicle = vehicle)
            }

        return transitionResult.fold(
            onSuccess = { updatedParking -> processSuccessfulTransition(updatedParking, command) },
            onFailure = { error -> WebhookEventOutcome.RejectedTransition(error) },
        )
    }

    private fun processSuccessfulTransition(
        updatedParking: Parking,
        command: WebhookEventCommand,
    ): WebhookEventOutcome {
        val occurredAt = Instant.now(clock)

        parkingSessionRepositoryPort.save(updatedParking)
        parkingEventRepositoryPort.save(
            StoredParkingEvent(
                parkingId = command.parkingId,
                licensePlate = command.licensePlate,
                eventType = command.eventType,
            ),
        )

        when (command.eventType) {
            EventType.ENTRY -> storePricingSnapshot(updatedParking, command, occurredAt)
            EventType.EXIT -> createBillingRecord(command, occurredAt)
            EventType.PARKED -> Unit
        }

        lifecycleEventPublisherPort.publish(
            PublishedLifecycleEvent(
                parkingId = command.parkingId,
                licensePlate = command.licensePlate,
                eventType = command.eventType,
                occurredAt = occurredAt,
            ),
        )

        return WebhookEventOutcome.Processed
    }

    private fun storePricingSnapshot(
        updatedParking: Parking,
        command: WebhookEventCommand,
        occurredAt: Instant,
    ) {
        val assignedSpot = updatedParking.spots.firstOrNull { it.occupiedBy?.plate == command.licensePlate } ?: return
        val garage = billingRepositoryPort.findGarageBySector(assignedSpot.sector) ?: return
        val occupancy = updatedParking.occupancyPercentage()

        pricingSnapshotRepositoryPort.save(
            PricingSnapshot(
                parkingId = command.parkingId,
                licensePlate = command.licensePlate,
                sector = assignedSpot.sector,
                basePrice = garage.basePrice,
                occupancyPercentageAtEntry = occupancy,
                multiplierAtEntry = PricingPolicy.occupancyMultiplier(occupancy),
                entryAt = occurredAt,
            ),
        )
    }

    private fun createBillingRecord(
        command: WebhookEventCommand,
        occurredAt: Instant,
    ) {
        val snapshot =
            pricingSnapshotRepositoryPort.findLatestByParkingIdAndLicensePlate(
                parkingId = command.parkingId,
                licensePlate = command.licensePlate,
            ) ?: return

        val parkedMinutes = Duration.between(snapshot.entryAt, occurredAt).toMinutes().coerceAtLeast(0)
        val billedHours =
            if (parkedMinutes <= FREE_PARKING_MINUTES) {
                BigDecimal.ZERO
            } else {
                BigDecimal
                    .valueOf(parkedMinutes)
                    .divide(BigDecimal(MINUTES_PER_HOUR), 0, RoundingMode.CEILING)
            }
        val amount =
            snapshot.basePrice
                .multiply(billedHours)
                .multiply(snapshot.multiplierAtEntry)
                .setScale(2, RoundingMode.HALF_UP)

        billingRecordRepositoryPort.save(
            BillingRecord(
                parkingId = command.parkingId,
                licensePlate = command.licensePlate,
                sector = snapshot.sector,
                amount = amount,
                parkedMinutes = parkedMinutes,
                billedAt = occurredAt,
            ),
        )
    }

    private fun isDuplicateForActiveSession(command: WebhookEventCommand): Boolean {
        val eventsByVehicle =
            parkingEventRepositoryPort
                .findByParkingId(command.parkingId)
                .filter { it.licensePlate == command.licensePlate }

        if (eventsByVehicle.isEmpty()) {
            return false
        }

        val lastExitIndex = eventsByVehicle.indexOfLast { it.eventType == EventType.EXIT }
        val eventsInActiveSession =
            if (lastExitIndex < 0) {
                eventsByVehicle
            } else {
                eventsByVehicle.drop(lastExitIndex + 1)
            }

        return eventsInActiveSession.any { it.eventType == command.eventType }
    }
}
