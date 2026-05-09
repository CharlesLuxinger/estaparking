package com.charlesluxinger.estaparking.domain.error

import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.spot.SpotStatus

sealed interface ParkingDomainError {
    data object FullOccupancyEntryDenied : ParkingDomainError

    data class VehicleNotFoundForTransition(
        val eventType: EventType,
        val vehiclePlate: String,
    ) : ParkingDomainError

    data class ExitBeforeEntry(
        val spotId: String,
        val currentStatus: SpotStatus,
    ) : ParkingDomainError

    data class InvalidParkedOrdering(
        val spotId: String,
        val currentStatus: SpotStatus,
    ) : ParkingDomainError

    data class InvalidExitOrdering(
        val spotId: String,
        val currentStatus: SpotStatus,
    ) : ParkingDomainError

    data class WrongVehicleTransitionAttempt(
        val spotId: String,
        val expectedPlate: String,
        val attemptedPlate: String,
    ) : ParkingDomainError
}
