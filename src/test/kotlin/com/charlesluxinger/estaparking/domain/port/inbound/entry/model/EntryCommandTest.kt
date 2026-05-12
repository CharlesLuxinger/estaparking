package com.charlesluxinger.estaparking.domain.port.inbound.entry.model

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class EntryCommandTest {
    @Test
    fun `should create entry command with vehicle`() {
        val vehicle = Vehicle(plate = "ABC1234")

        val command = EntryCommand(vehicle = vehicle)

        assertEquals(vehicle, command.vehicle)
    }

    @Test
    fun `data class generated methods are exercised`() {
        val original = EntryCommand(vehicle = Vehicle(plate = "ABC1234"))
        val sameValues = EntryCommand(vehicle = Vehicle(plate = "ABC1234"))
        val different = original.copy(vehicle = Vehicle(plate = "XYZ9876"))

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }
}
