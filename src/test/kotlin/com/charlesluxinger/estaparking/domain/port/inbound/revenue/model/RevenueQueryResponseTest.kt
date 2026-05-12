package com.charlesluxinger.estaparking.domain.port.inbound.revenue.model

import com.charlesluxinger.estaparking.domain.common.Currency
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class RevenueQueryResponseTest {
    @Test
    fun `should create RevenueQueryResponse with default currency BRL`() {
        val amount = BigDecimal("100.50")
        val timestamp = LocalDateTime.now()

        val response =
            RevenueQueryResponse(
                amount = amount,
                timestamp = timestamp,
            )

        assertEquals(Currency.BRL, response.currency)
        assertEquals(amount, response.amount)
        assertEquals(timestamp, response.timestamp)
    }

    @Test
    fun `Currency enum should have BRL value`() {
        val currency = Currency.valueOf("BRL")
        assertEquals(Currency.BRL, currency)
    }

    @Test
    fun `data class generated methods are exercised`() {
        val timestamp = LocalDateTime.of(2026, 5, 11, 12, 0)
        val original = RevenueQueryResponse(amount = BigDecimal("100.50"), currency = Currency.BRL, timestamp = timestamp)
        val sameValues = RevenueQueryResponse(amount = BigDecimal("100.50"), currency = Currency.BRL, timestamp = timestamp)
        val different = original.copy(amount = BigDecimal("200.00"))

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }
}
