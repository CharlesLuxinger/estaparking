package com.charlesluxinger.estaparking.domain.spot

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.error.DomainResult.Error
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class SpotTest {
    @Test
    fun `constructor with valid id and sector succeeds`() {
        val spot = createAvailableSpot()

        assertEquals("spot-1", spot.id)
        assertEquals("A", spot.sector)
    }

    @Test
    fun `constructor with blank id throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Spot(
                    id = "   ",
                    sector = "A",
                    coordinates =
                        Coordinates(
                            latitude = BigDecimal("-23.55052"),
                            longitude = BigDecimal("-46.633308"),
                        ),
                )
            }

        assertEquals("Spot id must not be blank", exception.message)
    }

    @Test
    fun `constructor with blank sector throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Spot(
                    id = "spot-1",
                    sector = "   ",
                    coordinates =
                        Coordinates(
                            latitude = BigDecimal("-23.55052"),
                            longitude = BigDecimal("-46.633308"),
                        ),
                )
            }

        assertEquals("Spot sector must not be blank", exception.message)
    }

    @Test
    fun `transition ENTRY then PARKED then EXIT returns to AVAILABLE for same vehicle`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val spot = createAvailableSpot()

        val afterEntry = spot.transition(EventType.ENTRY, vehicle)
        assertTrue(afterEntry is DomainResult.Success)
        val entrySpot = (afterEntry as DomainResult.Success).value
        assertEquals(SpotStatus.ENTRY_REGISTERED, entrySpot.status)
        assertEquals(vehicle, entrySpot.occupiedBy)

        val afterParked = entrySpot.transition(EventType.PARKED, vehicle)
        assertTrue(afterParked is DomainResult.Success)
        val parkedSpot = (afterParked as DomainResult.Success).value
        assertEquals(SpotStatus.PARKED, parkedSpot.status)
        assertEquals(vehicle, parkedSpot.occupiedBy)

        val afterExit = parkedSpot.transition(EventType.EXIT, vehicle)
        assertTrue(afterExit is DomainResult.Success)
        val availableSpot = (afterExit as DomainResult.Success).value
        assertEquals(SpotStatus.AVAILABLE, availableSpot.status)
        assertEquals(null, availableSpot.occupiedBy)
    }

    @Test
    fun `transition EXIT on AVAILABLE returns ExitBeforeEntry error`() {
        val spot = createAvailableSpot()
        val vehicle = Vehicle(plate = "ABC1234")

        val result = spot.transition(EventType.EXIT, vehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.ExitBeforeEntry(
                spotId = spot.id,
                currentStatus = SpotStatus.AVAILABLE,
            ),
            (result as Error).error,
        )
    }

    @Test
    fun `transition PARKED on AVAILABLE returns InvalidParkedOrdering error`() {
        val spot = createAvailableSpot()
        val vehicle = Vehicle(plate = "ABC1234")

        val result = spot.transition(EventType.PARKED, vehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.InvalidParkedOrdering(
                spotId = spot.id,
                currentStatus = SpotStatus.AVAILABLE,
            ),
            (result as Error).error,
        )
    }

    @Test
    fun `transition EXIT on ENTRY_REGISTERED returns InvalidExitOrdering error`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val spot = createAvailableSpot()
        val enteredSpot = (spot.transition(EventType.ENTRY, vehicle) as DomainResult.Success).value

        val result = enteredSpot.transition(EventType.EXIT, vehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.InvalidExitOrdering(
                spotId = spot.id,
                currentStatus = SpotStatus.ENTRY_REGISTERED,
            ),
            (result as Error).error,
        )
    }

    @Test
    fun `transition PARKED with wrong vehicle returns WrongVehicleTransitionAttempt error`() {
        val entryVehicle = Vehicle(plate = "ABC1234")
        val wrongVehicle = Vehicle(plate = "XYZ9876")
        val spot = createAvailableSpot()
        val enteredSpot = (spot.transition(EventType.ENTRY, entryVehicle) as DomainResult.Success).value

        val result = enteredSpot.transition(EventType.PARKED, wrongVehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = spot.id,
                expectedPlate = entryVehicle.plate,
                attemptedPlate = wrongVehicle.plate,
            ),
            (result as Error).error,
        )
    }

    @Test
    fun `transition EXIT with wrong vehicle returns WrongVehicleTransitionAttempt error`() {
        val entryVehicle = Vehicle(plate = "ABC1234")
        val wrongVehicle = Vehicle(plate = "XYZ9876")
        val spot = createAvailableSpot()
        val enteredSpot = (spot.transition(EventType.ENTRY, entryVehicle) as DomainResult.Success).value
        val parkedSpot = (enteredSpot.transition(EventType.PARKED, entryVehicle) as DomainResult.Success).value

        val result = parkedSpot.transition(EventType.EXIT, wrongVehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = spot.id,
                expectedPlate = entryVehicle.plate,
                attemptedPlate = wrongVehicle.plate,
            ),
            (result as Error).error,
        )
    }

    @Test
    fun `transition ENTRY on available spot with occupiedBy set returns InvalidParkedOrdering`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val spot =
            Spot(
                id = "spot-1",
                sector = "A",
                coordinates =
                    Coordinates(
                        latitude = BigDecimal("-23.55052"),
                        longitude = BigDecimal("-46.633308"),
                    ),
                status = SpotStatus.AVAILABLE,
                occupiedBy = Vehicle(plate = "ZZZ9999"),
            )

        val result = spot.transition(EventType.ENTRY, vehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.InvalidParkedOrdering(
                spotId = "spot-1",
                currentStatus = SpotStatus.AVAILABLE,
            ),
            (result as Error).error,
        )
    }

    @Test
    fun `transition PARKED with null occupiedBy returns expected none plate error`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val spot =
            Spot(
                id = "spot-1",
                sector = "A",
                coordinates =
                    Coordinates(
                        latitude = BigDecimal("-23.55052"),
                        longitude = BigDecimal("-46.633308"),
                    ),
                status = SpotStatus.ENTRY_REGISTERED,
                occupiedBy = null,
            )

        val result = spot.transition(EventType.PARKED, vehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = "spot-1",
                expectedPlate = "none",
                attemptedPlate = vehicle.plate,
            ),
            (result as Error).error,
        )
    }

    @Test
    fun `transition EXIT with null occupiedBy returns expected none plate error`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val spot =
            Spot(
                id = "spot-1",
                sector = "A",
                coordinates =
                    Coordinates(
                        latitude = BigDecimal("-23.55052"),
                        longitude = BigDecimal("-46.633308"),
                    ),
                status = SpotStatus.PARKED,
                occupiedBy = null,
            )

        val result = spot.transition(EventType.EXIT, vehicle)

        assertTrue(result is Error)
        assertEquals(
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = "spot-1",
                expectedPlate = "none",
                attemptedPlate = vehicle.plate,
            ),
            (result as Error).error,
        )
    }

    private fun createAvailableSpot(): Spot =
        Spot(
            id = "spot-1",
            sector = "A",
            coordinates =
                Coordinates(
                    latitude = BigDecimal("-23.55052"),
                    longitude = BigDecimal("-46.633308"),
                ),
        )
}
