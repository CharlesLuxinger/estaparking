package com.charlesluxinger.estaparking.domain.port.inbound.sync

import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class SyncGarageModelsTest {
    @Test
    fun `sync garage summary should keep counters`() {
        val summary = SyncGarageSummary(garagesSynced = 2, spotsSynced = 40)

        assertEquals(2, summary.garagesSynced)
        assertEquals(40, summary.spotsSynced)
    }

    @Test
    fun `sync garage summary data class generated methods are exercised`() {
        val original = SyncGarageSummary(garagesSynced = 2, spotsSynced = 40)
        val sameValues = SyncGarageSummary(garagesSynced = 2, spotsSynced = 40)
        val different = original.copy(spotsSynced = 41)

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }

    @Test
    fun `simulator fetch failed should expose cause`() {
        val cause = SimulatorGarageClientError.TransportFailure(message = "timeout")

        val error = SyncGarageError.SimulatorFetchFailed(cause = cause)

        assertEquals(cause, error.cause)
    }

    @Test
    fun `simulator fetch failed data class generated methods are exercised`() {
        val cause = SimulatorGarageClientError.TransportFailure(message = "timeout")
        val anotherCause = SimulatorGarageClientError.TransportFailure(message = "connection reset")

        val original = SyncGarageError.SimulatorFetchFailed(cause = cause)
        val sameValues = SyncGarageError.SimulatorFetchFailed(cause = cause)
        val different = original.copy(cause = anotherCause)

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }

    @Test
    fun `persistence failure should expose message`() {
        val error = SyncGarageError.PersistenceFailure(message = "db unavailable")

        assertEquals("db unavailable", error.message)
    }

    @Test
    fun `persistence failure data class generated methods are exercised`() {
        val original = SyncGarageError.PersistenceFailure(message = "db unavailable")
        val sameValues = SyncGarageError.PersistenceFailure(message = "db unavailable")
        val different = original.copy(message = "deadlock")

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
    }
}
