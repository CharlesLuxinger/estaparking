package com.charlesluxinger.estaparking.domain.port.inbound.revenue.model

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class RevenueQueryRequestTest {
    @Test
    fun `should create request with date and sector`() {
        val date = LocalDate.of(2026, 5, 11)

        val request = RevenueQueryRequest(date = date, sector = "A")

        assertEquals(date, request.date)
        assertEquals("A", request.sector)
    }

    @Test
    fun `data class generated methods are exercised`() {
        val original = RevenueQueryRequest(date = LocalDate.of(2026, 5, 11), sector = "A")
        val sameValues = RevenueQueryRequest(date = LocalDate.of(2026, 5, 11), sector = "A")
        val different = original.copy(sector = "B")

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }
}
