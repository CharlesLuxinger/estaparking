package com.charlesluxinger.estaparking.domain.billing

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BillingRecordTest {
    @Test
    fun `should create billing record with valid vehicle`() {
        val vehicle = Vehicle("ABC1234")
        val now = Instant.now()

        val record =
            BillingRecord(
                parkingId = "P123",
                vehicle = vehicle,
                sector = "A",
                amount = BigDecimal("10.00"),
                parkedMinutes = 60,
                billedAt = now,
            )

        assertEquals("P123", record.parkingId)
        assertEquals(vehicle, record.vehicle)
        assertEquals("A", record.sector)
        assertEquals(BigDecimal("10.00"), record.amount)
        assertEquals(60, record.parkedMinutes)
        assertEquals(now, record.billedAt)
    }

    @Test
    fun `should fail when parkingId is blank`() {
        val vehicle = Vehicle("ABC1234")

        val exception =
            assertThrows<IllegalArgumentException> {
                BillingRecord(
                    parkingId = "  ",
                    vehicle = vehicle,
                    sector = "A",
                    amount = BigDecimal("10.00"),
                    parkedMinutes = 60,
                    billedAt = Instant.now(),
                )
            }

        assertEquals("Parking id must not be blank", exception.message)
    }

    @Test
    fun `should fail with blank plate via vehicle validation`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                val vehicle = Vehicle("  ")
                BillingRecord(
                    parkingId = "P123",
                    vehicle = vehicle,
                    sector = "A",
                    amount = BigDecimal("10.00"),
                    parkedMinutes = 60,
                    billedAt = Instant.now(),
                )
            }

        assertEquals("Plate must not be blank", exception.message)
    }
}
