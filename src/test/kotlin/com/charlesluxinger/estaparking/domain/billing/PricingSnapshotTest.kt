package com.charlesluxinger.estaparking.domain.billing

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PricingSnapshotTest {
    private val vehicle = Vehicle("AAA9999")
    private val entryAt = Instant.parse("2026-01-01T00:00:00Z")

    @Test
    fun `should construct PricingSnapshot successfully`() {
        val now = Instant.now()
        val snapshot =
            PricingSnapshot(
                parkingId = "P1",
                vehicle = vehicle,
                sector = "A",
                basePrice = BigDecimal("10.0"),
                occupancyPercentageAtEntry = BigDecimal("50.0"),
                multiplierAtEntry = BigDecimal("1.5"),
                entryAt = now,
            )

        assertEquals(vehicle, snapshot.vehicle)
        assertEquals("P1", snapshot.parkingId)
        assertEquals("A", snapshot.sector)
        assertEquals(BigDecimal("10.0"), snapshot.basePrice)
        assertEquals(BigDecimal("50.0"), snapshot.occupancyPercentageAtEntry)
        assertEquals(BigDecimal("1.5"), snapshot.multiplierAtEntry)
        assertEquals(now, snapshot.entryAt)
    }

    @Test
    fun `should fail when parking id is blank`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                PricingSnapshot(
                    parkingId = " ",
                    vehicle = vehicle,
                    sector = "A",
                    basePrice = BigDecimal("10.0"),
                    occupancyPercentageAtEntry = BigDecimal("50.0"),
                    multiplierAtEntry = BigDecimal("1.5"),
                    entryAt = entryAt,
                )
            }

        assertEquals("Parking id must not be blank", exception.message)
    }

    @Test
    fun `should fail when sector is blank`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                PricingSnapshot(
                    parkingId = "P1",
                    vehicle = vehicle,
                    sector = " ",
                    basePrice = BigDecimal("10.0"),
                    occupancyPercentageAtEntry = BigDecimal("50.0"),
                    multiplierAtEntry = BigDecimal("1.5"),
                    entryAt = entryAt,
                )
            }

        assertEquals("Sector must not be blank", exception.message)
    }

    @Test
    fun `should fail when base price is zero`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                PricingSnapshot(
                    parkingId = "P1",
                    vehicle = vehicle,
                    sector = "A",
                    basePrice = BigDecimal.ZERO,
                    occupancyPercentageAtEntry = BigDecimal("50.0"),
                    multiplierAtEntry = BigDecimal("1.5"),
                    entryAt = entryAt,
                )
            }

        assertEquals("Base price must be greater than zero", exception.message)
    }

    @Test
    fun `should fail when base price is negative`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                PricingSnapshot(
                    parkingId = "P1",
                    vehicle = vehicle,
                    sector = "A",
                    basePrice = BigDecimal("-1.0"),
                    occupancyPercentageAtEntry = BigDecimal("50.0"),
                    multiplierAtEntry = BigDecimal("1.5"),
                    entryAt = entryAt,
                )
            }

        assertEquals("Base price must be greater than zero", exception.message)
    }

    @Test
    fun `should fail when occupancy percentage is negative`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                PricingSnapshot(
                    parkingId = "P1",
                    vehicle = vehicle,
                    sector = "A",
                    basePrice = BigDecimal("10.0"),
                    occupancyPercentageAtEntry = BigDecimal("-0.1"),
                    multiplierAtEntry = BigDecimal("1.5"),
                    entryAt = entryAt,
                )
            }

        assertEquals("Occupancy percentage must be non-negative", exception.message)
    }

    @Test
    fun `should fail when multiplier is zero`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                PricingSnapshot(
                    parkingId = "P1",
                    vehicle = vehicle,
                    sector = "A",
                    basePrice = BigDecimal("10.0"),
                    occupancyPercentageAtEntry = BigDecimal("50.0"),
                    multiplierAtEntry = BigDecimal.ZERO,
                    entryAt = entryAt,
                )
            }

        assertEquals("Multiplier must be greater than zero", exception.message)
    }

    @Test
    fun `should fail when multiplier is negative`() {
        val exception =
            assertThrows<IllegalArgumentException> {
            PricingSnapshot(
                parkingId = "P1",
                vehicle = vehicle,
                sector = "A",
                basePrice = BigDecimal("10.0"),
                occupancyPercentageAtEntry = BigDecimal("50.0"),
                multiplierAtEntry = BigDecimal("-1.0"),
                entryAt = entryAt,
            )
        }

        assertEquals("Multiplier must be greater than zero", exception.message)
    }
}
