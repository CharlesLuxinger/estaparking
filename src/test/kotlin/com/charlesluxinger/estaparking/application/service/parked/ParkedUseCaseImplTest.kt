package com.charlesluxinger.estaparking.application.service.parked

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ParkedUseCaseImplTest {
    private val useCase = ParkedUseCaseImpl()

    @Test
    fun `execute marks entry registered spot as parked`() {
        val vehicle = Vehicle("DDD4444")
        val parking =
            Parking(
                id = "parking-parked",
                name = "Parked Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63")),
                            status = SpotStatus.ENTRY_REGISTERED,
                            occupiedBy = vehicle,
                        ),
                    ),
            )

        val result = useCase.execute(parking, vehicle)

        assertTrue(result is DomainResult.Success)
        val updated = (result as DomainResult.Success).value
        assertEquals(SpotStatus.PARKED, updated.spots.first().status)
    }

    @Test
    fun `execute returns invalid parked ordering when spot is available`() {
        val parking =
            Parking(
                id = "parking-invalid",
                name = "Invalid Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63")),
                            status = SpotStatus.AVAILABLE,
                        ),
                    ),
            )

        val result = useCase.execute(parking, Vehicle("EEE5555"))

        assertTrue(result is DomainResult.Error)
        assertTrue((result as DomainResult.Error).error is ParkingDomainError.InvalidParkedOrdering)
    }
}
