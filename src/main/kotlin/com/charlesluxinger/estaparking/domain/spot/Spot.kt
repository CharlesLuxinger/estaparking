package com.charlesluxinger.estaparking.domain.spot

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError.ExitBeforeEntry
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError.InvalidExitOrdering
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError.InvalidParkedOrdering
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError.WrongVehicleTransitionAttempt
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.result.DomainResult.Error
import com.charlesluxinger.estaparking.domain.result.DomainResult.Success
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

data class Spot(
    val id: Long,
    val sector: String,
    val coordinates: Coordinates,
    val status: SpotStatus = SpotStatus.AVAILABLE,
    val occupiedBy: Vehicle? = null,
) {
    init {
        require(id > 0) { "Spot id must be greater than zero" }
        require(sector.isNotBlank()) { "Spot sector must not be blank" }
    }

    fun canAcceptEntry(): Boolean = status == SpotStatus.AVAILABLE && occupiedBy == null

    fun transition(
        eventType: EventType,
        vehicle: Vehicle,
    ): DomainResult<Spot, ParkingDomainError> =
        when (eventType) {
            EventType.ENTRY -> registerEntry(vehicle)
            EventType.PARKED -> markParked(vehicle)
            EventType.EXIT -> registerExit(vehicle)
        }

    private fun registerEntry(vehicle: Vehicle): DomainResult<Spot, ParkingDomainError> {
        if (!canAcceptEntry()) {
            return Error(
                InvalidParkedOrdering(
                    spotId = id,
                    currentStatus = status,
                ),
            )
        }

        return Success(copy(status = SpotStatus.ENTRY_REGISTERED, occupiedBy = vehicle))
    }

    private fun markParked(vehicle: Vehicle): DomainResult<Spot, ParkingDomainError> =
        when {
            status != SpotStatus.ENTRY_REGISTERED -> {
                Error(
                    InvalidParkedOrdering(
                        spotId = id,
                        currentStatus = status,
                    ),
                )
            }

            occupiedBy == null || occupiedBy != vehicle -> {
                Error(
                    WrongVehicleTransitionAttempt(
                        spotId = id,
                        expectedPlate = occupiedBy?.plate ?: "none",
                        attemptedPlate = vehicle.plate,
                    ),
                )
            }

            else -> Success(copy(status = SpotStatus.PARKED, occupiedBy = vehicle))
        }

    private fun registerExit(vehicle: Vehicle): DomainResult<Spot, ParkingDomainError> =
        when {
            status == SpotStatus.AVAILABLE -> {
                Error(
                    ExitBeforeEntry(
                        spotId = id,
                        currentStatus = status,
                    ),
                )
            }

            status != SpotStatus.PARKED -> {
                Error(
                    InvalidExitOrdering(
                        spotId = id,
                        currentStatus = status,
                    ),
                )
            }

            occupiedBy == null || occupiedBy != vehicle -> {
                Error(
                    WrongVehicleTransitionAttempt(
                        spotId = id,
                        expectedPlate = occupiedBy?.plate ?: "none",
                        attemptedPlate = vehicle.plate,
                    ),
                )
            }

            else -> Success(copy(status = SpotStatus.AVAILABLE, occupiedBy = null))
        }
}
