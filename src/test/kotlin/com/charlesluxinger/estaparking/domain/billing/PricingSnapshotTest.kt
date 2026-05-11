package com.charlesluxinger.estaparking.domain.billing

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PricingSnapshotTest {
    @Test
    fun `should construct PricingSnapshot with Vehicle successfully`() {
        val vehicle = Vehicle("AAA9999")

        val snapshot =
            PricingSnapshot(
                parkingId = "P1",
                vehicle = vehicle,
                sector = "A",
                basePrice = BigDecimal("10.0"),
                occupancyPercentageAtEntry = BigDecimal("50.0"),
                multiplierAtEntry = BigDecimal("1.5"),
                entryAt = Instant.now(),
            )

        assertEquals(vehicle, snapshot.vehicle)
        assertEquals("P1", snapshot.parkingId)
    }

    @Test
    fun `should fail when Vehicle has invalid plate`() {
        assertThrows<IllegalArgumentException> {
            PricingSnapshot(
                parkingId = "P1",
                vehicle = Vehicle("invalid"),
                sector = "A",
                basePrice = BigDecimal("10.0"),
                occupancyPercentageAtEntry = BigDecimal("50.0"),
                multiplierAtEntry = BigDecimal("1.5"),
                entryAt = Instant.now(),
            )
        }
    }
}
