package com.charlesluxinger.estaparking.domain.error

import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class ParkingDomainErrorTest {
    @Test
    fun `vehicle not found for transition keeps event type and vehicle plate`() {
        val error =
            ParkingDomainError.VehicleNotFoundForTransition(
                eventType = EventType.ENTRY,
                vehiclePlate = "ABC1234",
            )

        assertEquals(EventType.ENTRY, error.eventType)
        assertEquals("ABC1234", error.vehiclePlate)
    }

    @Test
    fun `full occupancy entry denied singleton is stable`() {
        assertEquals(ParkingDomainError.FullOccupancyEntryDenied, ParkingDomainError.FullOccupancyEntryDenied)
    }

    @Test
    fun `exit before entry keeps spot data`() {
        val error =
            ParkingDomainError.ExitBeforeEntry(
                spotId = 1L,
                currentStatus = SpotStatus.AVAILABLE,
            )

        assertEquals(1L, error.spotId)
        assertEquals(SpotStatus.AVAILABLE, error.currentStatus)
    }

    @Test
    fun `invalid parked ordering keeps spot data`() {
        val error =
            ParkingDomainError.InvalidParkedOrdering(
                spotId = 1L,
                currentStatus = SpotStatus.ENTRY_REGISTERED,
            )

        assertEquals(1L, error.spotId)
        assertEquals(SpotStatus.ENTRY_REGISTERED, error.currentStatus)
    }

    @Test
    fun `invalid exit ordering keeps spot data`() {
        val error =
            ParkingDomainError.InvalidExitOrdering(
                spotId = 1L,
                currentStatus = SpotStatus.PARKED,
            )

        assertEquals(1L, error.spotId)
        assertEquals(SpotStatus.PARKED, error.currentStatus)
    }

    @Test
    fun `wrong vehicle transition attempt keeps expected and attempted plates`() {
        val error =
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = 1L,
                expectedPlate = "ABC1234",
                attemptedPlate = "XYZ9876",
            )

        assertEquals(1L, error.spotId)
        assertEquals("ABC1234", error.expectedPlate)
        assertEquals("XYZ9876", error.attemptedPlate)
    }

    @Test
    fun `error variants support equality and copy semantics`() {
        val vehicleNotFound =
            ParkingDomainError.VehicleNotFoundForTransition(
                eventType = EventType.EXIT,
                vehiclePlate = "AAA0000",
            )
        val invalidExitOrdering =
            ParkingDomainError.InvalidExitOrdering(
                spotId = 10L,
                currentStatus = SpotStatus.PARKED,
            )
        val wrongVehicleTransition =
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = 10L,
                expectedPlate = "AAA0000",
                attemptedPlate = "BBB1111",
            )

        assertEquals(vehicleNotFound, vehicleNotFound.copy())
        assertNotEquals(vehicleNotFound, vehicleNotFound.copy(vehiclePlate = "CCC2222"))

        assertEquals(invalidExitOrdering, invalidExitOrdering.copy())
        assertNotEquals(invalidExitOrdering, invalidExitOrdering.copy(currentStatus = SpotStatus.AVAILABLE))

        assertEquals(wrongVehicleTransition, wrongVehicleTransition.copy())
        assertNotEquals(wrongVehicleTransition, wrongVehicleTransition.copy(attemptedPlate = "CCC2222"))
    }
}
