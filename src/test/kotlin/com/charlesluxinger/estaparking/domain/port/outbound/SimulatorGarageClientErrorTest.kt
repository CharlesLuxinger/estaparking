package com.charlesluxinger.estaparking.domain.port.outbound

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class SimulatorGarageClientErrorTest {
    @Test
    fun `transport failure should keep message`() {
        val error = SimulatorGarageClientError.TransportFailure(message = "timeout")

        assertEquals("timeout", error.message)
    }

    @Test
    fun `payload mapping failure should keep message`() {
        val error = SimulatorGarageClientError.PayloadMappingFailure(message = "json parse error")

        assertEquals("json parse error", error.message)
    }

    @Test
    fun `unexpected status should keep status code and body`() {
        val error = SimulatorGarageClientError.UnexpectedStatus(statusCode = 503, responseBody = "service unavailable")

        assertEquals(503, error.statusCode)
        assertEquals("service unavailable", error.responseBody)
    }

    @Test
    fun `unexpected status data class generated methods are exercised`() {
        val original = SimulatorGarageClientError.UnexpectedStatus(statusCode = 500, responseBody = "error")
        val sameValues = SimulatorGarageClientError.UnexpectedStatus(statusCode = 500, responseBody = "error")
        val different = original.copy(statusCode = 502)

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }
}
