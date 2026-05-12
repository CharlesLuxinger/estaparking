package com.charlesluxinger.estaparking.domain.result

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DomainResultTest {
    @Test
    fun `map transforms success value`() {
        val result: DomainResult<Int, ParkingDomainError> = DomainResult.Success(2)

        val mapped = result.map { it * 2 }

        assertEquals(DomainResult.Success(4), mapped)
    }

    @Test
    fun `map keeps error unchanged`() {
        val error = ParkingDomainError.FullOccupancyEntryDenied
        val result: DomainResult<Int, ParkingDomainError> = DomainResult.Error(error)

        val mapped = result.map { it * 2 }

        assertEquals(DomainResult.Error(error), mapped)
    }

    @Test
    fun `flatMap transforms success value`() {
        val result: DomainResult<Int, ParkingDomainError> = DomainResult.Success(3)

        val mapped = result.flatMap { DomainResult.Success(it + 1) }

        assertEquals(DomainResult.Success(4), mapped)
    }

    @Test
    fun `flatMap keeps error unchanged`() {
        val error = ParkingDomainError.FullOccupancyEntryDenied
        val result: DomainResult<Int, ParkingDomainError> = DomainResult.Error(error)

        val mapped = result.flatMap { DomainResult.Success(it + 1) }

        assertEquals(DomainResult.Error(error), mapped)
    }

    @Test
    fun `fold uses success branch`() {
        val result: DomainResult<Int, ParkingDomainError> = DomainResult.Success(10)

        val folded = result.fold(onSuccess = { "value:$it" }, onFailure = { "error" })

        assertEquals("value:10", folded)
    }

    @Test
    fun `fold uses error branch`() {
        val result: DomainResult<Int, ParkingDomainError> =
            DomainResult.Error(
                ParkingDomainError.FullOccupancyEntryDenied,
            )

        val folded = result.fold(onSuccess = { "value:$it" }, onFailure = { "error" })

        assertEquals("error", folded)
    }

    @Test
    fun `success data class supports equality and copy`() {
        val success = DomainResult.Success(1)

        assertEquals(DomainResult.Success(1), success)
        assertEquals(DomainResult.Success(2), success.copy(value = 2))
    }

    @Test
    fun `error data class supports equality and copy`() {
        val error: DomainResult.Error<ParkingDomainError> =
            DomainResult.Error(
                ParkingDomainError.FullOccupancyEntryDenied,
            )
        val transitionError = ParkingDomainError.ExitBeforeEntry(spotId = 1L, currentStatus = SpotStatus.AVAILABLE)

        assertEquals(DomainResult.Error(ParkingDomainError.FullOccupancyEntryDenied), error)
        assertEquals(DomainResult.Error(transitionError), error.copy(error = transitionError))
    }
}
