package com.charlesluxinger.estaparking.domain.port.inbound.webhook.model

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class WebhookEventOutcomeTest {
    @Test
    fun `rejected transition should keep domain error`() {
        val error = ParkingDomainError.VehicleNotFoundForTransition(EventType.ENTRY, "ABC1234")

        val outcome = WebhookEventOutcome.RejectedTransition(error = error)

        assertEquals(error, outcome.error)
    }

    @Test
    fun `rejected transition data class generated methods are exercised`() {
        val firstError = ParkingDomainError.VehicleNotFoundForTransition(EventType.ENTRY, "ABC1234")
        val secondError = ParkingDomainError.VehicleNotFoundForTransition(EventType.EXIT, "XYZ9876")

        val original = WebhookEventOutcome.RejectedTransition(error = firstError)
        val sameValues = WebhookEventOutcome.RejectedTransition(error = firstError)
        val different = original.copy(error = secondError)

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }
}
