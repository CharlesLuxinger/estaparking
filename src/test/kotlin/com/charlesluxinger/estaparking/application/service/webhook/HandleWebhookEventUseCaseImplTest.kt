package com.charlesluxinger.estaparking.application.service.webhook

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.garage.Garage
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.port.inbound.entry.EntryCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.parked.ParkedCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRecordRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.LifecycleEventPublisherPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.PricingSnapshotRepositoryPort
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.result.DomainResult.Success
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HandleWebhookEventUseCaseImplTest {
    private fun createUseCase(
        parkingSessionRepositoryPort: ParkingSessionRepositoryPort,
        parkingEventRepositoryPort: ParkingEventRepositoryPort,
        entryCommandPort: EntryCommandPort,
        parkedCommandPort: ParkedCommandPort,
        billingRepositoryPort: BillingRepositoryPort,
        pricingSnapshotRepositoryPort: PricingSnapshotRepositoryPort,
        billingRecordRepositoryPort: BillingRecordRepositoryPort,
        lifecycleEventPublisherPort: LifecycleEventPublisherPort,
    ): HandleWebhookEventUseCaseImpl =
        HandleWebhookEventUseCaseImpl(
            parkingSessionRepositoryPort = parkingSessionRepositoryPort,
            parkingEventRepositoryPort = parkingEventRepositoryPort,
            entryCommandPort = entryCommandPort,
            parkedCommandPort = parkedCommandPort,
            billingRepositoryPort = billingRepositoryPort,
            pricingSnapshotRepositoryPort = pricingSnapshotRepositoryPort,
            billingRecordRepositoryPort = billingRecordRepositoryPort,
            lifecycleEventPublisherPort = lifecycleEventPublisherPort,
        )

    private fun mockBillingPorts(): List<Any> =
        listOf(
            mockk<BillingRepositoryPort>(relaxed = true),
            mockk<PricingSnapshotRepositoryPort>(relaxed = true),
            mockk<BillingRecordRepositoryPort>(relaxed = true),
            mockk<LifecycleEventPublisherPort>(relaxed = true),
        )

    @Test
    fun `returns not found when parking does not exist`() {
        val parkingId = "missing-parking"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()
        val entryCommandPort = mockk<EntryCommandPort>()
        val parkedCommandPort = mockk<ParkedCommandPort>()
        val billingPorts = mockBillingPorts()
        val billingRepo = billingPorts[0] as BillingRepositoryPort
        val pricingSnapRepo = billingPorts[1] as PricingSnapshotRepositoryPort
        val billingRecRepo = billingPorts[2] as BillingRecordRepositoryPort
        val lifecyclePub = billingPorts[3] as LifecycleEventPublisherPort

        val useCase =
            createUseCase(
                parkingSessionRepositoryPort,
                parkingEventRepositoryPort,
                entryCommandPort,
                parkedCommandPort,
                billingRepo,
                pricingSnapRepo,
                billingRecRepo,
                lifecyclePub,
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns null

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    licensePlate = "ABC1234",
                    eventType = EventType.ENTRY,
                ),
            )

        assertEquals(WebhookEventOutcome.NotFound, result)
        verify(exactly = 0) { parkingEventRepositoryPort.findByParkingId(any()) }
        verify(exactly = 0) { parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { parkingEventRepositoryPort.save(any()) }
    }

    @Test
    fun `duplicate ENTRY in same active session is ignored`() {
        val parkingId = "parking-dup-entry"
        val plate = "ABC1234"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()
        val entryCommandPort = mockk<EntryCommandPort>()
        val parkedCommandPort = mockk<ParkedCommandPort>()
        val billingPorts = mockBillingPorts()
        val billingRepo = billingPorts[0] as BillingRepositoryPort
        val pricingSnapRepo = billingPorts[1] as PricingSnapshotRepositoryPort
        val billingRecRepo = billingPorts[2] as BillingRecordRepositoryPort
        val lifecyclePub = billingPorts[3] as LifecycleEventPublisherPort

        val useCase =
            createUseCase(
                parkingSessionRepositoryPort,
                parkingEventRepositoryPort,
                entryCommandPort,
                parkedCommandPort,
                billingRepo,
                pricingSnapRepo,
                billingRecRepo,
                lifecyclePub,
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithEntryRegistered(plate, parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns
            listOf(StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.ENTRY))

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                ),
            )

        assertEquals(WebhookEventOutcome.IgnoredDuplicate, result)

        verify(exactly = 0) { parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { parkingEventRepositoryPort.save(any()) }
    }

    @Test
    fun `ENTRY after EXIT guard reset`() {
        val parkingId = "parking-reset-after-exit"
        val plate = "ABC1234"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()
        val entryCommandPort = mockk<EntryCommandPort>()
        val parkedCommandPort = mockk<ParkedCommandPort>()
        val billingPorts = mockBillingPorts()
        val billingRepo = billingPorts[0] as BillingRepositoryPort
        val pricingSnapRepo = billingPorts[1] as PricingSnapshotRepositoryPort
        val billingRecRepo = billingPorts[2] as BillingRecordRepositoryPort
        val lifecyclePub = billingPorts[3] as LifecycleEventPublisherPort

        val useCase =
            createUseCase(
                parkingSessionRepositoryPort,
                parkingEventRepositoryPort,
                entryCommandPort,
                parkedCommandPort,
                billingRepo,
                pricingSnapRepo,
                billingRecRepo,
                lifecyclePub,
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithAvailableSpot(parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns
            listOf(
                StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.ENTRY),
                StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.PARKED),
                StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.EXIT),
            )
        every { entryCommandPort.execute(any(), any()) } returns
            Success(Parking(parkingId, "Test", listOf()))
        every { parkingSessionRepositoryPort.save(any()) } answers { firstArg() }
        every { parkingEventRepositoryPort.save(any()) } answers { firstArg() }
        every { billingRepo.findGarageBySector("A") } returns Garage("A", BigDecimal("10.00"), 100)

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                ),
            )

        assertEquals(WebhookEventOutcome.Processed, result)

        verify(exactly = 1) { parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 1) { entryCommandPort.execute(any(), Vehicle(plate)) }
        verify(exactly = 1) {
            parkingEventRepositoryPort.save(
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                ),
            )
        }
    }

    @Test
    fun `duplicate webhook command does not mutate session state`() {
        val parkingId = "parking-idempotency-stability"
        val plate = "ABC1234"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()
        val entryCommandPort = mockk<EntryCommandPort>()
        val parkedCommandPort = mockk<ParkedCommandPort>()
        val billingPorts = mockBillingPorts()
        val billingRepo = billingPorts[0] as BillingRepositoryPort
        val pricingSnapRepo = billingPorts[1] as PricingSnapshotRepositoryPort
        val billingRecRepo = billingPorts[2] as BillingRecordRepositoryPort
        val lifecyclePub = billingPorts[3] as LifecycleEventPublisherPort

        val useCase =
            createUseCase(
                parkingSessionRepositoryPort,
                parkingEventRepositoryPort,
                entryCommandPort,
                parkedCommandPort,
                billingRepo,
                pricingSnapRepo,
                billingRecRepo,
                lifecyclePub,
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithEntryRegistered(plate, parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns
            listOf(StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.ENTRY))

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                ),
            )

        assertEquals(WebhookEventOutcome.IgnoredDuplicate, result)

        verify(exactly = 1) { parkingSessionRepositoryPort.findById(parkingId) }
        verify(exactly = 1) { parkingEventRepositoryPort.findByParkingId(parkingId) }
        verify(exactly = 0) { parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { parkingEventRepositoryPort.save(any()) }
    }

    @Test
    fun `rejected transition returns explicit outcome`() {
        val parkingId = "parking-rejected-transition"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()
        val entryCommandPort = mockk<EntryCommandPort>()
        val parkedCommandPort = mockk<ParkedCommandPort>()
        val billingPorts = mockBillingPorts()
        val billingRepo = billingPorts[0] as BillingRepositoryPort
        val pricingSnapRepo = billingPorts[1] as PricingSnapshotRepositoryPort
        val billingRecRepo = billingPorts[2] as BillingRecordRepositoryPort
        val lifecyclePub = billingPorts[3] as LifecycleEventPublisherPort

        val useCase =
            createUseCase(
                parkingSessionRepositoryPort,
                parkingEventRepositoryPort,
                entryCommandPort,
                parkedCommandPort,
                billingRepo,
                pricingSnapRepo,
                billingRecRepo,
                lifecyclePub,
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithAvailableSpot(parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns emptyList()
        every { parkedCommandPort.execute(any(), Vehicle("ABC1234")) } returns
            DomainResult.Error(
                ParkingDomainError.InvalidParkedOrdering(
                    spotId = 1L,
                    currentStatus = SpotStatus.AVAILABLE,
                ),
            )

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    licensePlate = "ABC1234",
                    eventType = EventType.PARKED,
                ),
            )

        assertTrue(result is WebhookEventOutcome.RejectedTransition)
        val rejectedResult = result as WebhookEventOutcome.RejectedTransition
        assertTrue(rejectedResult.error is ParkingDomainError.InvalidParkedOrdering)
        verify(exactly = 1) { parkedCommandPort.execute(any(), Vehicle("ABC1234")) }
        verify(exactly = 0) { parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { parkingEventRepositoryPort.save(any()) }
    }

    private fun parkingWithAvailableSpot(parkingId: String): Parking =
        Parking(
            id = parkingId,
            name = "Webhook Parking",
            spots =
                listOf(
                    Spot(
                        id = 1L,
                        sector = "A",
                        coordinates = Coordinates(BigDecimal("-23.5500000"), BigDecimal("-46.6300000")),
                    ),
                ),
        )

    private fun parkingWithEntryRegistered(
        plate: String,
        parkingId: String,
    ): Parking =
        Parking(
            id = parkingId,
            name = "Webhook Parking",
            spots =
                listOf(
                    Spot(
                        id = 1L,
                        sector = "A",
                        coordinates = Coordinates(BigDecimal("-23.5500000"), BigDecimal("-46.6300000")),
                        status = SpotStatus.ENTRY_REGISTERED,
                        occupiedBy = Vehicle(plate),
                    ),
                ),
        )
}
