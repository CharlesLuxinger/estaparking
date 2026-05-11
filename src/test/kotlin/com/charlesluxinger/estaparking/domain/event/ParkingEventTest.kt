package com.charlesluxinger.estaparking.domain.event

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParkingEventTest {
    @Test
    fun `should have vehicle property on ParkingEvent interface`() {
        val vehicle = Vehicle("ABC1234")
        val timestamp = LocalDateTime.now()

        val event: ParkingEvent =
            StoredParkingEvent(
                parkingId = "parking-123",
                vehicle = vehicle,
                eventType = EventType.ENTRY,
                timestamp = timestamp,
            )

        assertEquals(vehicle, event.vehicle)
    }

    @Test
    fun `should create StoredParkingEvent with Vehicle`() {
        val vehicle = Vehicle("ABC1234")
        val timestamp = LocalDateTime.now()

        val event =
            StoredParkingEvent(
                parkingId = "parking-123",
                vehicle = vehicle,
                eventType = EventType.ENTRY,
                timestamp = timestamp,
            )

        assertEquals("parking-123", event.parkingId)
        assertEquals(vehicle, event.vehicle)
        assertEquals(EventType.ENTRY, event.eventType)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `should fail to create StoredParkingEvent when Vehicle validation fails`() {
        assertThrows<IllegalArgumentException> {
            StoredParkingEvent(
                parkingId = "parking-123",
                vehicle = Vehicle("invalid"),
                eventType = EventType.ENTRY,
                timestamp = LocalDateTime.now(),
            )
        }
    }
}
