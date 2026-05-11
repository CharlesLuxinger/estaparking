package com.charlesluxinger.estaparking.application.service.webhook

import com.charlesluxinger.estaparking.domain.billing.BillingTransaction
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.port.inbound.entry.EntryCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.parked.ParkedCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingTransactionRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import com.charlesluxinger.estaparking.domain.pricing.PricingPolicy
import com.charlesluxinger.estaparking.domain.result.DomainResult
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class HandleWebhookEventUseCaseImpl(
    private val parkingSessionRepositoryPort: ParkingSessionRepositoryPort,
    private val parkingEventRepositoryPort: ParkingEventRepositoryPort,
    private val entryCommandPort: EntryCommandPort,
    private val parkedCommandPort: ParkedCommandPort,
    private val billingTransactionRepositoryPort: BillingTransactionRepositoryPort,
    private val billingRepositoryPort: BillingRepositoryPort,
) : WebhookEventCommandPort {
    override fun handle(command: WebhookEventCommand): WebhookEventOutcome {
        val currentParking =
            parkingSessionRepositoryPort.findById(command.parkingId)
                ?: return WebhookEventOutcome.NotFound

        val outcome =
            if (isDuplicateForActiveSession(command)) {
                WebhookEventOutcome.IgnoredDuplicate
            } else {
                val vehicle = command.vehicle
                val transitionResult =
                    when (command.eventType) {
                        EventType.ENTRY -> entryCommandPort.execute(currentParking, vehicle)
                        EventType.PARKED -> parkedCommandPort.execute(currentParking, vehicle)
                        EventType.EXIT -> currentParking.apply(eventType = EventType.EXIT, vehicle = vehicle)
                    }

                when (transitionResult) {
                    is DomainResult.Success -> {
                        val timestamp = command.occurredAt ?: LocalDateTime.now()

                        parkingSessionRepositoryPort.save(transitionResult.value)
                        parkingEventRepositoryPort.save(
                            StoredParkingEvent(
                                parkingId = command.parkingId,
                                vehicle = command.vehicle,
                                eventType = command.eventType,
                                timestamp = timestamp,
                            ),
                        )

                        if (command.eventType == EventType.EXIT && command.occurredAt != null) {
                            saveBillingTransaction(command, currentParking, timestamp)
                        }

                        WebhookEventOutcome.Processed
                    }

                    is DomainResult.Error -> WebhookEventOutcome.RejectedTransition(transitionResult.error)
                }
            }

        return outcome
    }

    private fun saveBillingTransaction(
        command: WebhookEventCommand,
        currentParking: com.charlesluxinger.estaparking.domain.parking.Parking,
        exitTime: LocalDateTime,
    ) {
        val sector = findSectorForVehicle(currentParking, command.vehicle.plate)
        val entryTime = findLastEntryTime(command.parkingId, command.vehicle.plate)
        val garage = sector?.let { billingRepositoryPort.findGarageBySector(it) }

        if (sector == null || entryTime == null || garage == null) {
            return
        }

        val parkedMinutes = Duration.between(entryTime, exitTime).toMinutes()
        val occupancyPercentage = calculateOccupancy(currentParking)

        val amount =
            PricingPolicy.calculateAmount(
                basePrice = garage.basePrice,
                parkedMinutes = parkedMinutes,
                occupancyPercentage = occupancyPercentage,
            )

        val billingTransaction =
            BillingTransaction(
                id = UUID.randomUUID().toString(),
                vehicle = command.vehicle,
                sector = sector,
                amount = amount,
                exitTime = exitTime,
                createdAt = LocalDateTime.now(),
            )

        billingTransactionRepositoryPort.save(billingTransaction)
    }

    private fun findSectorForVehicle(
        parking: com.charlesluxinger.estaparking.domain.parking.Parking,
        licensePlate: String,
    ): String? = parking.spots.find { it.occupiedBy?.plate == licensePlate }?.sector

    private fun findLastEntryTime(
        parkingId: String,
        licensePlate: String,
    ): LocalDateTime? {
        val events =
            parkingEventRepositoryPort
                .findByParkingId(parkingId)
                .filter { it.vehicle.plate == licensePlate && it.eventType == EventType.ENTRY }
                .sortedByDescending { it.timestamp }
        return events.firstOrNull()?.timestamp
    }

    private fun calculateOccupancy(parking: com.charlesluxinger.estaparking.domain.parking.Parking): BigDecimal {
        val totalSpots = parking.spots.size
        if (totalSpots == 0) return BigDecimal.ZERO

        val occupiedSpots = parking.spots.count { it.occupiedBy != null }
        return (occupiedSpots.toBigDecimal() * BigDecimal("100")) / totalSpots.toBigDecimal()
    }

    private fun isDuplicateForActiveSession(command: WebhookEventCommand): Boolean {
        val eventsByVehicle =
            parkingEventRepositoryPort
                .findByParkingId(command.parkingId)
                .filter { it.vehicle.plate == command.vehicle.plate }

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
