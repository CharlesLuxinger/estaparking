package com.charlesluxinger.estaparking.domain.vehicle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
}
