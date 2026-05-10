package com.charlesluxinger.estaparking.domain.pricing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PricingPolicyTest {
    @Test
    fun `calculateAmount returns zero for 30 minutes`() {
        val amount = PricingPolicy.calculateAmount(BigDecimal("10.00"), 30, BigDecimal("50"))

        assertEquals(BigDecimal("0.00"), amount)
    }

    @Test
    fun `calculateAmount bills one hour for 31 minutes`() {
        val amount = PricingPolicy.calculateAmount(BigDecimal("10.00"), 31, BigDecimal("50"))

        assertEquals(BigDecimal("10.00"), amount)
    }

    @Test
    fun `calculateAmount bills one hour for 60 minutes`() {
        val amount = PricingPolicy.calculateAmount(BigDecimal("10.00"), 60, BigDecimal("50"))

        assertEquals(BigDecimal("10.00"), amount)
    }

    @Test
    fun `calculateAmount bills two hours for 61 minutes`() {
        val amount = PricingPolicy.calculateAmount(BigDecimal("10.00"), 61, BigDecimal("50"))

        assertEquals(BigDecimal("20.00"), amount)
    }

    @Test
    fun `occupancyMultiplier applies minus ten percent up to 25`() {
        assertEquals(BigDecimal("0.90"), PricingPolicy.occupancyMultiplier(BigDecimal("25")))
    }

    @Test
    fun `occupancyMultiplier applies zero percent up to 50`() {
        assertEquals(BigDecimal("1.00"), PricingPolicy.occupancyMultiplier(BigDecimal("50")))
    }

    @Test
    fun `occupancyMultiplier applies plus ten percent up to 75`() {
        assertEquals(BigDecimal("1.10"), PricingPolicy.occupancyMultiplier(BigDecimal("75")))
    }

    @Test
    fun `occupancyMultiplier applies plus twenty five percent up to 100`() {
        assertEquals(BigDecimal("1.25"), PricingPolicy.occupancyMultiplier(BigDecimal("100")))
    }
}
