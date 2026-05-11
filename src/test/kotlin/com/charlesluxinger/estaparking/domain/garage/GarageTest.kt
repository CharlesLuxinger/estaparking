package com.charlesluxinger.estaparking.domain.garage

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GarageTest {
    @Test
    fun `constructor with valid sector basePrice and maxCapacity succeeds`() {
        val garage = Garage(sector = "A", basePrice = BigDecimal("10.0"), maxCapacity = 100)

        assertEquals("A", garage.sector)
        assertEquals(BigDecimal("10.0"), garage.basePrice)
        assertEquals(100, garage.maxCapacity)
    }

    @Test
    fun `constructor with blank sector throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Garage(sector = "   ", basePrice = BigDecimal("10.0"), maxCapacity = 100)
            }

        assertEquals("Garage sector must not be blank", exception.message)
    }

    @Test
    fun `constructor with non-positive basePrice throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Garage(sector = "A", basePrice = BigDecimal.ZERO, maxCapacity = 100)
            }

        assertEquals("Garage base price must be greater than zero", exception.message)
    }

    @Test
    fun `constructor with non-positive maxCapacity throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Garage(sector = "A", basePrice = BigDecimal("10.0"), maxCapacity = 0)
            }

        assertEquals("Garage max capacity must be greater than zero", exception.message)
    }
}
