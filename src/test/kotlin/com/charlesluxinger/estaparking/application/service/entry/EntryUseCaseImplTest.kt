package com.charlesluxinger.estaparking.application.service.entry

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EntryUseCaseImplTest {
    private val useCase = EntryUseCaseImpl()

    @Test
    fun `execute returns full occupancy error when no entry is possible`() {
        val parking =
            Parking(
                id = "parking-full",
                name = "Full Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63")),
                            status = SpotStatus.PARKED,
                            occupiedBy = Vehicle("AAA1111"),
                        ),
                    ),
            )

        val result = useCase.execute(parking, Vehicle("BBB2222"))

        assertTrue(result is DomainResult.Error)
        assertEquals(ParkingDomainError.FullOccupancyEntryDenied, (result as DomainResult.Error).error)
    }

    @Test
    fun `execute registers entry when parking has available spot`() {
        val parking =
            Parking(
                id = "parking-open",
                name = "Open Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63")),
                        ),
                    ),
            )

        val result = useCase.execute(parking, Vehicle("CCC3333"))

        assertTrue(result is DomainResult.Success)
        val updated = (result as DomainResult.Success).value
        assertEquals(SpotStatus.ENTRY_REGISTERED, updated.spots.first().status)
        assertEquals(Vehicle("CCC3333"), updated.spots.first().occupiedBy)
    }
}
