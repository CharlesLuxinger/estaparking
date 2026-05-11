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
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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
                    vehicle = Vehicle("ABC1234"),
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
                    vehicle = Vehicle(plate),
                    eventType = EventType.ENTRY,
                    timestamp = java.time.LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
            )

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    vehicle = Vehicle(plate),
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
                    vehicle = Vehicle(plate),
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
                        it.vehicle.plate == plate &&
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
                    vehicle = Vehicle(plate),
                    eventType = EventType.ENTRY,
                    timestamp = LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
                StoredParkingEvent(
                    parkingId = parkingId,
                    vehicle = Vehicle(plate),
                    eventType = EventType.PARKED,
                    timestamp = LocalDateTime.of(2025, 1, 1, 13, 0),
                ),
                StoredParkingEvent(
                    parkingId = parkingId,
                    vehicle = Vehicle(plate),
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
                    vehicle = Vehicle(plate),
                    eventType = EventType.ENTRY,
                    timestamp = java.time.LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
            )

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    vehicle = Vehicle(plate),
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
                    vehicle = Vehicle("ABC1234"),
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

    @Test
    fun `ENTRY event when parking is full returns rejected transition with full occupancy`() {
        val parkingId = "parking-entry-full"
        val vehicle = Vehicle("JKL1234")
        val ports = createMockPorts()
        val useCase = createUseCase(ports)
        val fullParking =
            Parking(
                id = parkingId,
                name = "Full Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.5500000"), BigDecimal("-46.6300000")),
                            status = SpotStatus.ENTRY_REGISTERED,
                            occupiedBy = Vehicle("MNO5678"),
                        ),
                    ),
            )

        every { ports.parkingSessionRepositoryPort.findById(parkingId) } returns fullParking
        every { ports.parkingEventRepositoryPort.findByParkingId(parkingId) } returns emptyList()
        every { ports.entryCommandPort.execute(fullParking, vehicle) } returns
            DomainResult.Error(ParkingDomainError.FullOccupancyEntryDenied)

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    vehicle = vehicle,
                    eventType = EventType.ENTRY,
                    occurredAt = LocalDateTime.of(2025, 1, 1, 10, 0),
                ),
            )

        assertTrue(result is WebhookEventOutcome.RejectedTransition)
        val rejectedResult = result as WebhookEventOutcome.RejectedTransition
        assertTrue(rejectedResult.error is ParkingDomainError.FullOccupancyEntryDenied)
        verify(exactly = 0) { ports.parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { ports.parkingEventRepositoryPort.save(any()) }
    }

    @Test
    fun `PARKED event with invalid ordering returns rejected transition with invalid parked ordering`() {
        val parkingId = "parking-invalid-parked-ordering"
        val ports = createMockPorts()
        val useCase = createUseCase(ports)
        val vehicle = Vehicle("PQR9012")

        every { ports.parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithAvailableSpot(parkingId)
        every { ports.parkingEventRepositoryPort.findByParkingId(parkingId) } returns emptyList()
        every { ports.parkedCommandPort.execute(any(), vehicle) } returns
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
                    vehicle = vehicle,
                    eventType = EventType.PARKED,
                    occurredAt = LocalDateTime.of(2025, 1, 1, 11, 0),
                ),
            )

        assertTrue(result is WebhookEventOutcome.RejectedTransition)
        val rejectedResult = result as WebhookEventOutcome.RejectedTransition
        assertTrue(rejectedResult.error is ParkingDomainError.InvalidParkedOrdering)
        verify(exactly = 0) { ports.parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { ports.parkingEventRepositoryPort.save(any()) }
    }

    @Test
    fun `EXIT event with wrong vehicle returns rejected transition with wrong vehicle error`() {
        val parkingId = "parking-exit-wrong-vehicle"
        val currentVehicle = Vehicle("STU3456")
        val attemptedVehicle = Vehicle("VWX7890")
        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = mockk(),
                parkingEventRepositoryPort = mockk(),
                entryCommandPort = mockk(),
                parkedCommandPort = mockk(),
                billingTransactionRepositoryPort = mockk(),
                billingRepositoryPort = mockk(),
            )
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()

        val useCaseWithMocks =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
                entryCommandPort = mockk(),
                parkedCommandPort = mockk(),
                billingTransactionRepositoryPort = mockk(),
                billingRepositoryPort = mockk(),
            )
        val parking =
            Parking(
                id = parkingId,
                name = "Exit Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.5500000"), BigDecimal("-46.6300000")),
                            status = SpotStatus.PARKED,
                            occupiedBy = currentVehicle,
                        ),
                    ),
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parking
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns emptyList()

        val result =
            useCaseWithMocks.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    vehicle = attemptedVehicle,
                    eventType = EventType.EXIT,
                    occurredAt = LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
            )

        assertTrue(result is WebhookEventOutcome.RejectedTransition)
        val rejectedResult = result as WebhookEventOutcome.RejectedTransition
        assertTrue(rejectedResult.error is ParkingDomainError.WrongVehicleTransitionAttempt)
        verify(exactly = 0) { parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { parkingEventRepositoryPort.save(any()) }
    }

    @Test
    fun `when entry succeeds save parking session and parking event are called`() {
        val parkingId = "parking-entry-success-persistence"
        val plate = "YZA1122"
        val vehicle = Vehicle(plate)
        val ports = createMockPorts()
        val useCase = createUseCase(ports)
        val currentParking = parkingWithAvailableSpot(parkingId)
        val updatedParking =
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
                            occupiedBy = vehicle,
                        ),
                    ),
            )

        every { ports.parkingSessionRepositoryPort.findById(parkingId) } returns currentParking
        every { ports.parkingEventRepositoryPort.findByParkingId(parkingId) } returns emptyList()
        every { ports.entryCommandPort.execute(currentParking, vehicle) } returns DomainResult.Success(updatedParking)
        every { ports.parkingSessionRepositoryPort.save(updatedParking) } returns updatedParking
        every { ports.parkingEventRepositoryPort.save(any()) } answers { firstArg() }

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    vehicle = vehicle,
                    eventType = EventType.ENTRY,
                    occurredAt = LocalDateTime.of(2025, 1, 1, 13, 0),
                ),
            )

        assertEquals(WebhookEventOutcome.Processed, result)
        verify(exactly = 1) { ports.parkingSessionRepositoryPort.save(updatedParking) }
        verify(exactly = 1) {
            ports.parkingEventRepositoryPort.save(
                match {
                    it.parkingId == parkingId &&
                        it.vehicle == vehicle &&
                        it.eventType == EventType.ENTRY
                },
            )
        }
    }

    @Test
    fun `when entry fails with full occupancy no persistence calls are made`() {
        val parkingId = "parking-entry-fail-no-persistence"
        val vehicle = Vehicle("BCD3344")
        val ports = createMockPorts()
        val useCase = createUseCase(ports)
        val currentParking = parkingWithAvailableSpot(parkingId)

        every { ports.parkingSessionRepositoryPort.findById(parkingId) } returns currentParking
        every { ports.parkingEventRepositoryPort.findByParkingId(parkingId) } returns emptyList()
        every { ports.entryCommandPort.execute(currentParking, vehicle) } returns
            DomainResult.Error(ParkingDomainError.FullOccupancyEntryDenied)

        val result =
            useCase.handle(
                WebhookEventCommand(
                    parkingId = parkingId,
                    vehicle = vehicle,
                    eventType = EventType.ENTRY,
                    occurredAt = LocalDateTime.of(2025, 1, 1, 14, 0),
                ),
            )

        assertTrue(result is WebhookEventOutcome.RejectedTransition)
        val rejectedResult = result as WebhookEventOutcome.RejectedTransition
        assertTrue(rejectedResult.error is ParkingDomainError.FullOccupancyEntryDenied)
        verify(exactly = 0) { ports.parkingSessionRepositoryPort.save(any()) }
        verify(exactly = 0) { ports.parkingEventRepositoryPort.save(any()) }
    }
}
