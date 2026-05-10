package com.charlesluxinger.estaparking.domain.vehicle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VehicleTest {
    @Test
    fun `constructor with valid plate succeeds`() {
        val vehicle = Vehicle(plate = "ABC1234")

        assertEquals("ABC1234", vehicle.plate)
    }

    @Test
    fun `constructor with lowercase plate throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Vehicle(plate = "abc1234")
            }

        assertEquals("Plate must be uppercase", exception.message)
    }

    @Test
    fun `constructor with surrounding spaces throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Vehicle(plate = " ABC1234 ")
            }

        assertEquals("Plate must not contain surrounding spaces", exception.message)
    }

    @Test
    fun `constructor with invalid pattern throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Vehicle(plate = "AB12345")
            }

        assertEquals("Plate must match format AAA9999", exception.message)
    }

    @Test
    fun `constructor with blank plate throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Vehicle(plate = "   ")
            }

        assertEquals("Plate must not be blank", exception.message)
    }

    @Test
    fun `constructor with internal space fails regex branch`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Vehicle(plate = "ABC 1234")
            }

        assertEquals("Plate must match format AAA9999", exception.message)
    }

    @Test
    fun `constructor with digits in letter section fails regex branch`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Vehicle(plate = "A1C1234")
            }

        assertEquals("Plate must match format AAA9999", exception.message)
    }

    @Test
    fun `data class generated methods are exercised`() {
        val original = Vehicle(plate = "ABC1234")
        val sameValues = Vehicle(plate = "ABC1234")
        val different = original.copy(plate = "XYZ9876")
        val (plate) = original

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
        assertEquals("ABC1234", plate)
        assertTrue(original.toString().contains("Vehicle(plate=ABC1234)"))
    }
}
