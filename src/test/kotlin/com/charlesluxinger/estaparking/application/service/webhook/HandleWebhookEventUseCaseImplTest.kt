package com.charlesluxinger.estaparking.application.service.webhook

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
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

class HandleWebhookEventUseCaseImplTest {
    @Test
    fun `returns not found when parking does not exist`() {
        val parkingId = "missing-parking"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
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

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
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
    fun `ENTRY is processed again after EXIT resets guard`() {
        val parkingId = "parking-reset-after-exit"
        val plate = "ABC1234"
        val parkingSessionRepositoryPort = mockk<ParkingSessionRepositoryPort>()
        val parkingEventRepositoryPort = mockk<ParkingEventRepositoryPort>()

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithAvailableSpot(parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns
            listOf(
                StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.ENTRY),
                StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.PARKED),
                StoredParkingEvent(parkingId = parkingId, licensePlate = plate, eventType = EventType.EXIT),
            )
        every { parkingSessionRepositoryPort.save(any()) } answers { firstArg() }
        every { parkingEventRepositoryPort.save(any()) } answers { firstArg() }

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

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
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

        val useCase =
            HandleWebhookEventUseCaseImpl(
                parkingSessionRepositoryPort = parkingSessionRepositoryPort,
                parkingEventRepositoryPort = parkingEventRepositoryPort,
            )

        every { parkingSessionRepositoryPort.findById(parkingId) } returns parkingWithAvailableSpot(parkingId)
        every { parkingEventRepositoryPort.findByParkingId(parkingId) } returns emptyList()

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
                        id = "A-01",
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
                        id = "A-01",
                        sector = "A",
                        coordinates = Coordinates(BigDecimal("-23.5500000"), BigDecimal("-46.6300000")),
                        status = SpotStatus.ENTRY_REGISTERED,
                        occupiedBy = Vehicle(plate),
                    ),
                ),
        )
}
