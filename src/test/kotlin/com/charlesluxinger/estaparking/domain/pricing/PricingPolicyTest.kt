package com.charlesluxinger.estaparking.domain.pricing

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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

    @Test
    fun `occupancyMultiplier applies 90 percent below 25`() {
        assertEquals(BigDecimal("0.90"), PricingPolicy.occupancyMultiplier(BigDecimal("24")))
    }

    @Test
    fun `occupancyMultiplier applies 125 percent above 75`() {
        assertEquals(BigDecimal("1.25"), PricingPolicy.occupancyMultiplier(BigDecimal("76")))
    }

    @Test
    fun `calculateAmount throws for zero base price`() {
        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException::class.java) {
                PricingPolicy.calculateAmount(BigDecimal.ZERO, 60, BigDecimal("50"))
            }
        assertEquals("Base price must be greater than zero", exception.message)
    }

    @Test
    fun `calculateAmount throws for negative parked minutes`() {
        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException::class.java) {
                PricingPolicy.calculateAmount(BigDecimal("10.00"), -1, BigDecimal("50"))
            }
        assertEquals("Parked minutes must be non-negative", exception.message)
    }

    @Test
    fun `occupancyMultiplier throws for negative occupancy`() {
        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException::class.java) {
                PricingPolicy.occupancyMultiplier(BigDecimal("-1"))
            }
        assertEquals("Occupancy percentage must be non-negative", exception.message)
    }

    @Test
    fun `occupancyMultiplier throws for occupancy above 100`() {
        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException::class.java) {
                PricingPolicy.occupancyMultiplier(BigDecimal("101"))
            }
        assertEquals("Occupancy percentage must be at most 100", exception.message)
    }
}
