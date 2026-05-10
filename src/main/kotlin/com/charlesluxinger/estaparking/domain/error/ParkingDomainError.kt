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
        val spotId: Long,
        val currentStatus: SpotStatus,
    ) : ParkingDomainError

    data class InvalidParkedOrdering(
        val spotId: Long,
        val currentStatus: SpotStatus,
    ) : ParkingDomainError

    data class InvalidExitOrdering(
        val spotId: Long,
        val currentStatus: SpotStatus,
    ) : ParkingDomainError

    data class WrongVehicleTransitionAttempt(
        val spotId: Long,
        val expectedPlate: String,
        val attemptedPlate: String,
    ) : ParkingDomainError
}
