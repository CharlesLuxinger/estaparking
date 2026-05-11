package com.charlesluxinger.estaparking.application.service.webhook

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.port.inbound.entry.EntryCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.parked.ParkedCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class HandleWebhookEventUseCaseImplTest {
    @Test
    fun `returns not found when parking does not exist`() {
        val parkingId = "missing-parking"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()
        val entryCommandPort = mockk<EntryCommandPort>()
        val parkedCommandPort = mockk<ParkedCommandPort>()

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
                entryCommandPort = entryCommandPort,
                parkedCommandPort = parkedCommandPort,
                billingTransactionRepositoryPort = mockk(),
                billingRepositoryPort = mockk(),
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

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
                entryCommandPort = entryCommandPort,
                parkedCommandPort = parkedCommandPort,
                billingTransactionRepositoryPort = mockk(),
                billingRepositoryPort = mockk(),
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithEntryRegistered(plate, parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns
            listOf(
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                    timestamp = java.time.LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
            )

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
    fun `ENTRY is processed again after EXIT resets guard`() {
        val parkingId = "parking-reset-after-exit"
        val plate = "ABC1234"
        val ports = createMockPorts()
        val useCase = createUseCase(ports)

        setupMocksForReentry(ports, parkingId, plate)

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                ),
            )

        assertEquals(WebhookEventOutcome.Processed, result)

        verify(exactly = 1) { ports.parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 1) { ports.entryCommandPort.execute(any(), Vehicle(plate)) }
        verify(exactly = 1) {
            ports.parkingEventRepositoryPort.save(
                match {
                    it.parkingId == parkingId &&
                        it.licensePlate == plate &&
                        it.eventType == EventType.ENTRY
                },
            )
        }
    }

    private fun setupMocksForReentry(
        ports: MockPorts,
        parkingId: String,
        plate: String,
    ) {
        every { ports.parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithAvailableSpot(parkingId)
        every { ports.parkingEventRepositoryPort.findByParkingId(parkingId) } returns
            listOf(
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                    timestamp = LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.PARKED,
                    timestamp = LocalDateTime.of(2025, 1, 1, 13, 0),
                ),
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.EXIT,
                    timestamp = LocalDateTime.of(2025, 1, 1, 14, 0),
                ),
            )
        every { ports.entryCommandPort.execute(any(), Vehicle(plate)) } answers {
            firstArg<Parking>().apply(EventType.ENTRY, Vehicle(plate))
        }
        every { ports.parkingSessionRepositoryPort.save(any()) } answers { firstArg() }
        every { ports.parkingEventRepositoryPort.save(any()) } answers { firstArg() }
    }

    @Test
    fun `duplicate webhook command does not mutate session state`() {
        val parkingId = "parking-idempotency-stability"
        val plate = "ABC1234"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()
        val entryCommandPort = mockk<EntryCommandPort>()
        val parkedCommandPort = mockk<ParkedCommandPort>()

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
                entryCommandPort = entryCommandPort,
                parkedCommandPort = parkedCommandPort,
                billingTransactionRepositoryPort = mockk(),
                billingRepositoryPort = mockk(),
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithEntryRegistered(plate, parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns
            listOf(
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = plate,
                    eventType = EventType.ENTRY,
                    timestamp = java.time.LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
            )

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

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
                entryCommandPort = entryCommandPort,
                parkedCommandPort = parkedCommandPort,
                billingTransactionRepositoryPort = mockk(),
                billingRepositoryPort = mockk(),
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

    private fun createMockPorts() =
        MockPorts(
            parkingSessionRepositoryPort = mockk(),
            parkingEventRepositoryPort = mockk(),
            entryCommandPort = mockk(),
            parkedCommandPort = mockk(),
        )

    private data class MockPorts(
        val parkingSessionRepositoryPort: ParkingSessionRepositoryPort,
        val parkingEventRepositoryPort: ParkingEventRepositoryPort,
        val entryCommandPort: EntryCommandPort,
        val parkedCommandPort: ParkedCommandPort,
    )

    private fun createUseCase(ports: MockPorts): HandleWebhookEventUseCaseImpl =
        HandleWebhookEventUseCaseImpl(
            parkingSessionRepositoryPort = ports.parkingSessionRepositoryPort,
            parkingEventRepositoryPort = ports.parkingEventRepositoryPort,
            entryCommandPort = ports.entryCommandPort,
            parkedCommandPort = ports.parkedCommandPort,
            billingTransactionRepositoryPort = mockk(),
            billingRepositoryPort = mockk(),
        )
}
