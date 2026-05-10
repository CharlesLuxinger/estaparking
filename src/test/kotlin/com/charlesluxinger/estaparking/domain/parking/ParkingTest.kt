package com.charlesluxinger.estaparking.domain.parking

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ParkingTest {
    @Test
    fun `constructor with valid id and name succeeds`() {
        val parking = Parking(id = "parking-1", name = "Central")

        assertEquals("parking-1", parking.id)
        assertEquals("Central", parking.name)
    }

    @Test
    fun `constructor with blank id throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Parking(id = "   ", name = "Central")
            }

        assertEquals("Parking id must not be blank", exception.message)
    }

    @Test
    fun `constructor with blank name throws exception`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Parking(id = "parking-1", name = "   ")
            }

        assertEquals("Parking name must not be blank", exception.message)
    }

    @Test
    fun `apply ENTRY PARKED EXIT succeeds and returns spot to AVAILABLE`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val parking = createParkingWithAvailableSpots()

        val afterEntry = parking.apply(EventType.ENTRY, vehicle)
        assertTrue(afterEntry is DomainResult.Success)
        val entryParking = (afterEntry as DomainResult.Success).value
        assertEquals(SpotStatus.ENTRY_REGISTERED, entryParking.spots[0].status)
        assertEquals(vehicle, entryParking.spots[0].occupiedBy)

        val afterParked = entryParking.apply(EventType.PARKED, vehicle)
        assertTrue(afterParked is DomainResult.Success)
        val parkedParking = (afterParked as DomainResult.Success).value
        assertEquals(SpotStatus.PARKED, parkedParking.spots[0].status)
        assertEquals(vehicle, parkedParking.spots[0].occupiedBy)

        val afterExit = parkedParking.apply(EventType.EXIT, vehicle)
        assertTrue(afterExit is DomainResult.Success)
        val exitedParking = (afterExit as DomainResult.Success).value
        assertEquals(SpotStatus.AVAILABLE, exitedParking.spots[0].status)
        assertEquals(null, exitedParking.spots[0].occupiedBy)
    }

    @Test
    fun `apply ENTRY when parking is full returns FullOccupancyEntryDenied`() {
        val occupiedSpot =
            createSpot(
                id = "spot-1",
                status = SpotStatus.ENTRY_REGISTERED,
                occupiedBy = Vehicle(plate = "AAA0000"),
            )
        val parking = Parking(id = "parking-1", name = "Central", spots = listOf(occupiedSpot))

        val result = parking.apply(EventType.ENTRY, Vehicle(plate = "ABC1234"))

        assertTrue(result is DomainResult.Error)
        assertEquals(ParkingDomainError.FullOccupancyEntryDenied, (result as DomainResult.Error).error)
    }

    @Test
    fun `apply EXIT before ENTRY returns ExitBeforeEntry`() {
        val parking = createParkingWithAvailableSpots()
        val result = parking.apply(EventType.EXIT, Vehicle(plate = "ABC1234"))

        assertTrue(result is DomainResult.Error)
        assertEquals(
            ParkingDomainError.ExitBeforeEntry(
                spotId = "unknown",
                currentStatus = SpotStatus.AVAILABLE,
            ),
            (result as DomainResult.Error).error,
        )
    }

    @Test
    fun `apply PARKED before ENTRY returns InvalidParkedOrdering`() {
        val parking = createParkingWithAvailableSpots()
        val result = parking.apply(EventType.PARKED, Vehicle(plate = "ABC1234"))

        assertTrue(result is DomainResult.Error)
        assertEquals(
            ParkingDomainError.InvalidParkedOrdering(
                spotId = "unknown",
                currentStatus = SpotStatus.AVAILABLE,
            ),
            (result as DomainResult.Error).error,
        )
    }

    @Test
    fun `apply PARKED with wrong vehicle returns WrongVehicleTransitionAttempt`() {
        val entryVehicle = Vehicle(plate = "ABC1234")
        val wrongVehicle = Vehicle(plate = "XYZ9876")
        val afterEntry =
            (createParkingWithAvailableSpots().apply(EventType.ENTRY, entryVehicle) as DomainResult.Success).value

        val result = afterEntry.apply(EventType.PARKED, wrongVehicle)

        assertTrue(result is DomainResult.Error)
        assertEquals(
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = "spot-1",
                expectedPlate = entryVehicle.plate,
                attemptedPlate = wrongVehicle.plate,
            ),
            (result as DomainResult.Error).error,
        )
    }

    @Test
    fun `apply EXIT with wrong vehicle while parked returns WrongVehicleTransitionAttempt`() {
        val entryVehicle = Vehicle(plate = "ABC1234")
        val wrongVehicle = Vehicle(plate = "XYZ9876")
        val afterEntry =
            (createParkingWithAvailableSpots().apply(EventType.ENTRY, entryVehicle) as DomainResult.Success).value
        val parked = (afterEntry.apply(EventType.PARKED, entryVehicle) as DomainResult.Success).value

        val result = parked.apply(EventType.EXIT, wrongVehicle)

        assertTrue(result is DomainResult.Error)
        assertEquals(
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = "spot-1",
                expectedPlate = entryVehicle.plate,
                attemptedPlate = wrongVehicle.plate,
            ),
            (result as DomainResult.Error).error,
        )
    }

    @Test
    fun `apply EXIT after ENTRY but before PARKED returns InvalidExitOrdering`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val afterEntry =
            (
                createParkingWithAvailableSpots().apply(
                    EventType.ENTRY,
                    vehicle,
                ) as DomainResult.Success
            ).value

        val result = afterEntry.apply(EventType.EXIT, vehicle)

        assertTrue(result is DomainResult.Error)
        assertEquals(
            ParkingDomainError.InvalidExitOrdering(
                spotId = "spot-1",
                currentStatus = SpotStatus.ENTRY_REGISTERED,
            ),
            (result as DomainResult.Error).error,
        )
    }

    @Test
    fun `apply ENTRY returns propagated spot transition error when spot fails`() {
        val vehicle = Vehicle(plate = "ABC1234")
        val expectedError =
            ParkingDomainError.InvalidParkedOrdering(
                spotId = "spot-err",
                currentStatus = SpotStatus.ENTRY_REGISTERED,
            )
        val failingSpot = mockk<Spot>()

        every { failingSpot.canAcceptEntry() } returns true
        every { failingSpot.transition(EventType.ENTRY, vehicle) } returns DomainResult.Error(expectedError)

        val parking = Parking(id = "parking-1", name = "Central", spots = listOf(failingSpot))

        val result = parking.apply(EventType.ENTRY, vehicle)

        assertTrue(result is DomainResult.Error)
        assertEquals(expectedError, (result as DomainResult.Error).error)
    }

    @Test
    fun `resolve missing transition with ENTRY returns VehicleNotFoundForTransition`() {
        val parking = createParkingWithAvailableSpots()
        val vehicle = Vehicle(plate = "ABC1234")
        val method =
            Parking::class.java.getDeclaredMethod(
                "resolveMissingVehicleTransition",
                EventType::class.java,
                Vehicle::class.java,
            )

        method.isAccessible = true
        val result = method.invoke(parking, EventType.ENTRY, vehicle)

        assertTrue(result is DomainResult.Error<*>)
        val error = (result as? DomainResult.Error<*>)?.error
        assertEquals(
            ParkingDomainError.VehicleNotFoundForTransition(
                eventType = EventType.ENTRY,
                vehiclePlate = vehicle.plate,
            ),
            error,
        )
    }

    private fun createParkingWithAvailableSpots(): Parking =
        Parking(
            id = "parking-1",
            name = "Central",
            spots = listOf(createSpot(id = "spot-1"), createSpot(id = "spot-2")),
        )

    private fun createSpot(
        id: String,
        status: SpotStatus = SpotStatus.AVAILABLE,
        occupiedBy: Vehicle? = null,
    ): Spot =
        Spot(
            id = id,
            sector = "A",
            coordinates =
                Coordinates(
                    latitude = BigDecimal("-23.55052"),
                    longitude = BigDecimal("-46.633308"),
                ),
            status = status,
            occupiedBy = occupiedBy,
        )
}
