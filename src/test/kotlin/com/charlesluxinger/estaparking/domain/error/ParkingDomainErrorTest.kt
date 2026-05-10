package com.charlesluxinger.estaparking.domain.error

import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import org.junit.jupiter.api.Assertions.assertEquals
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
                spotId = "spot-1",
                currentStatus = SpotStatus.AVAILABLE,
            )

        assertEquals("spot-1", error.spotId)
        assertEquals(SpotStatus.AVAILABLE, error.currentStatus)
    }

    @Test
    fun `invalid parked ordering keeps spot data`() {
        val error =
            ParkingDomainError.InvalidParkedOrdering(
                spotId = "spot-1",
                currentStatus = SpotStatus.ENTRY_REGISTERED,
            )

        assertEquals("spot-1", error.spotId)
        assertEquals(SpotStatus.ENTRY_REGISTERED, error.currentStatus)
    }

    @Test
    fun `invalid exit ordering keeps spot data`() {
        val error =
            ParkingDomainError.InvalidExitOrdering(
                spotId = "spot-1",
                currentStatus = SpotStatus.PARKED,
            )

        assertEquals("spot-1", error.spotId)
        assertEquals(SpotStatus.PARKED, error.currentStatus)
    }

    @Test
    fun `wrong vehicle transition attempt keeps expected and attempted plates`() {
        val error =
            ParkingDomainError.WrongVehicleTransitionAttempt(
                spotId = "spot-1",
                expectedPlate = "ABC1234",
                attemptedPlate = "XYZ9876",
            )

        assertEquals("spot-1", error.spotId)
        assertEquals("ABC1234", error.expectedPlate)
        assertEquals("XYZ9876", error.attemptedPlate)
    }
}
