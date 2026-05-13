package com.charlesluxinger.estaparking.domain.port.inbound.webhook.model

import com.charlesluxinger.estaparking.domain.common.Coordinates
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class WebhookEventCommandTest {
    @Test
    fun `parked event should require a valid latitude range`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                WebhookEventCommand(
                    parkingId = "parking-1",
                    vehicle = Vehicle("ABC1234"),
                    eventType = EventType.PARKED,
                    coordinates = Coordinates(BigDecimal("-90.01"), BigDecimal("-46.6300000")),
                )
            }

        assertEquals("Latitude must be between -90 and 90", exception.message)
    }

    @Test
    fun `parked event should require a valid longitude range`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                WebhookEventCommand(
                    parkingId = "parking-1",
                    vehicle = Vehicle("ABC1234"),
                    eventType = EventType.PARKED,
                    coordinates = Coordinates(BigDecimal("-48.01"), BigDecimal("-180.6300000")),
                )
            }

        assertEquals("Longitude must be between -180 and 180", exception.message)
    }

    @Test
    fun `parked event should accept coordinates in valid range`() {
        val command =
            WebhookEventCommand(
                parkingId = "parking-1",
                vehicle = Vehicle("ABC1234"),
                eventType = EventType.PARKED,
                coordinates = Coordinates(BigDecimal("-23.5505"), BigDecimal("-46.6333")),
            )

        assertEquals(BigDecimal("-23.5505"), command.coordinates.latitude)
        assertEquals(BigDecimal("-46.6333"), command.coordinates.longitude)
    }
}
