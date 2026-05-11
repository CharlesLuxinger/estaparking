package com.charlesluxinger.estaparking.domain.billing

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class BillingTransactionTest {
    @Test
    fun `constructor with valid parameters succeeds`() {
        val vehicle = Vehicle("ABC1234")
        val now = LocalDateTime.now()
        val transaction =
            BillingTransaction(
                id = "txn-123",
                vehicle = vehicle,
                sector = "North",
                amount = BigDecimal("15.00"),
                exitTime = now,
            )

        assertEquals("txn-123", transaction.id)
        assertEquals(vehicle, transaction.vehicle)
        assertEquals("North", transaction.sector)
        assertEquals(BigDecimal("15.00"), transaction.amount)
        assertEquals(now, transaction.exitTime)
    }

    @Test
    fun `constructor with blank id throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                BillingTransaction(
                    id = "   ",
                    vehicle = Vehicle("ABC1234"),
                    sector = "North",
                    amount = BigDecimal("15.00"),
                    exitTime = LocalDateTime.now(),
                )
            }

        assertEquals("Id must not be blank", exception.message)
    }

    @Test
    fun `constructor with blank sector throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                BillingTransaction(
                    id = "txn-123",
                    vehicle = Vehicle("ABC1234"),
                    sector = "  ",
                    amount = BigDecimal("15.00"),
                    exitTime = LocalDateTime.now(),
                )
            }

        assertEquals("Sector must not be blank", exception.message)
    }

    @Test
    fun `constructor with negative amount throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                BillingTransaction(
                    id = "txn-123",
                    vehicle = Vehicle("ABC1234"),
                    sector = "North",
                    amount = BigDecimal("-5.00"),
                    exitTime = LocalDateTime.now(),
                )
            }

        assertEquals("Amount must be non-negative", exception.message)
    }

    @Test
    fun `constructor with future exit time throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                BillingTransaction(
                    id = "txn-123",
                    vehicle = Vehicle("ABC1234"),
                    sector = "North",
                    amount = BigDecimal("15.00"),
                    exitTime = LocalDateTime.now().plusDays(1),
                )
            }

        assertEquals("Exit time cannot be in the future", exception.message)
    }
}
