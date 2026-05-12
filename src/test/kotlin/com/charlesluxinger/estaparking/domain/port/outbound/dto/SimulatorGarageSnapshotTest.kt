package com.charlesluxinger.estaparking.domain.port.outbound.dto

import com.charlesluxinger.estaparking.domain.garage.Garage
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class SimulatorGarageSnapshotTest {
    @Test
    fun `should keep garages and spots`() {
        val garage = Garage(sector = "A", basePrice = BigDecimal("10.00"), maxCapacity = 100)
        val spot =
            Spot(id = 1L, sector = "A", coordinates = Coordinates(BigDecimal("-23.55052"), BigDecimal("-46.633308")))

        val snapshot = SimulatorGarageSnapshot(garages = listOf(garage), spots = listOf(spot))

        assertEquals(listOf(garage), snapshot.garages)
        assertEquals(listOf(spot), snapshot.spots)
    }

    @Test
    fun `data class generated methods are exercised`() {
        val garageA = Garage(sector = "A", basePrice = BigDecimal("10.00"), maxCapacity = 100)
        val garageB = Garage(sector = "B", basePrice = BigDecimal("12.50"), maxCapacity = 150)
        val spot =
            Spot(id = 1L, sector = "A", coordinates = Coordinates(BigDecimal("-23.55052"), BigDecimal("-46.633308")))

        val original = SimulatorGarageSnapshot(garages = listOf(garageA), spots = listOf(spot))
        val sameValues = SimulatorGarageSnapshot(garages = listOf(garageA), spots = listOf(spot))
        val different = original.copy(garages = listOf(garageB))

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }
}
