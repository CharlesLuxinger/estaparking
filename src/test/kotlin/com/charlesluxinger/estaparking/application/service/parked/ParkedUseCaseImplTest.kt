package com.charlesluxinger.estaparking.application.service.parked

import com.charlesluxinger.estaparking.domain.common.Coordinates
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParkedUseCaseImplTest {
    private val useCase = ParkedUseCaseImpl()

    @Test
    fun `execute marks entry registered spot as parked`() {
        val coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63"))
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
                            coordinates = coordinates,
                            status = SpotStatus.ENTRY_REGISTERED,
                            occupiedBy = vehicle,
                        ),
                    ),
            )

        val result = useCase.execute(parking, vehicle, coordinates)

        assertTrue(result is DomainResult.Success)
        val updated = (result as DomainResult.Success).value
        assertEquals(SpotStatus.PARKED, updated.spots.first().status)
    }

    @Test
    fun `execute returns invalid parked ordering when spot is available`() {
        val coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63"))
        val parking =
            Parking(
                id = "parking-invalid",
                name = "Invalid Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = coordinates,
                            status = SpotStatus.AVAILABLE,
                        ),
                    ),
            )

        val result = useCase.execute(parking, Vehicle("EEE5555"), coordinates)

        assertTrue(result is DomainResult.Error)
        assertTrue((result as DomainResult.Error).error is ParkingDomainError.InvalidParkedOrdering)
    }

    @Test
    fun `execute returns wrong vehicle transition attempt when different vehicle tries to mark parked`() {
        val coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63"))
        val registeredVehicle = Vehicle("FFF6666")
        val parking =
            Parking(
                id = "parking-wrong-vehicle",
                name = "Wrong Vehicle Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = coordinates,
                            status = SpotStatus.ENTRY_REGISTERED,
                            occupiedBy = registeredVehicle,
                        ),
                    ),
            )

        val result = useCase.execute(parking, Vehicle("GGG7777"), coordinates)

        assertTrue(result is DomainResult.Error)
        assertTrue((result as DomainResult.Error).error is ParkingDomainError.WrongVehicleTransitionAttempt)
    }

    @Test
    fun `execute returns invalid parked ordering when spot is already parked`() {
        val coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63"))
        val vehicle = Vehicle("HHH8888")
        val parking =
            Parking(
                id = "parking-already-parked",
                name = "Already Parked Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = coordinates,
                            status = SpotStatus.PARKED,
                            occupiedBy = vehicle,
                        ),
                    ),
            )

        val result = useCase.execute(parking, vehicle, coordinates)

        assertTrue(result is DomainResult.Error)
        assertTrue((result as DomainResult.Error).error is ParkingDomainError.InvalidParkedOrdering)
    }

    @Test
    fun `execute with unknown vehicle returns invalid parked ordering for transition`() {
        val coordinates = Coordinates(BigDecimal("-23.55"), BigDecimal("-46.63"))
        val parking =
            Parking(
                id = "parking-unknown-vehicle",
                name = "Unknown Vehicle Parking",
                spots =
                    listOf(
                        Spot(
                            id = 1L,
                            sector = "A",
                            coordinates = coordinates,
                            status = SpotStatus.AVAILABLE,
                        ),
                    ),
            )

        val result = useCase.execute(parking, Vehicle("III9999"), coordinates)

        assertTrue(result is DomainResult.Error)
        assertTrue((result as DomainResult.Error).error is ParkingDomainError.InvalidParkedOrdering)
    }
}
