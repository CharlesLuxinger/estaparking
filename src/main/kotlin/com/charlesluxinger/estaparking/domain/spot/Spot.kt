package com.charlesluxinger.estaparking.domain.spot

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

data class Spot(
    val id: String,
    val sector: String,
    val coordinates: Coordinates,
    val status: SpotStatus = SpotStatus.AVAILABLE,
    val occupiedBy: Vehicle? = null,
) {
    init {
        require(id.isNotBlank()) { "Spot id must not be blank" }
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
            return DomainResult.Error(
                ParkingDomainError.InvalidParkedOrdering(
                    spotId = id,
                    currentStatus = status,
                ),
            )
        }

        return DomainResult.Success(copy(status = SpotStatus.ENTRY_REGISTERED, occupiedBy = vehicle))
    }

    private fun markParked(vehicle: Vehicle): DomainResult<Spot, ParkingDomainError> =
        when {
            status != SpotStatus.ENTRY_REGISTERED -> {
                DomainResult.Error(
                    ParkingDomainError.InvalidParkedOrdering(
                        spotId = id,
                        currentStatus = status,
                    ),
                )
            }

            occupiedBy == null || occupiedBy != vehicle -> {
                DomainResult.Error(
                    ParkingDomainError.WrongVehicleTransitionAttempt(
                        spotId = id,
                        expectedPlate = occupiedBy?.plate ?: "none",
                        attemptedPlate = vehicle.plate,
                    ),
                )
            }

            else -> DomainResult.Success(copy(status = SpotStatus.PARKED, occupiedBy = vehicle))
        }

    private fun registerExit(vehicle: Vehicle): DomainResult<Spot, ParkingDomainError> =
        when {
            status == SpotStatus.AVAILABLE -> {
                DomainResult.Error(
                    ParkingDomainError.ExitBeforeEntry(
                        spotId = id,
                        currentStatus = status,
                    ),
                )
            }

            status != SpotStatus.PARKED -> {
                DomainResult.Error(
                    ParkingDomainError.InvalidExitOrdering(
                        spotId = id,
                        currentStatus = status,
                    ),
                )
            }

            occupiedBy == null || occupiedBy != vehicle -> {
                DomainResult.Error(
                    ParkingDomainError.WrongVehicleTransitionAttempt(
                        spotId = id,
                        expectedPlate = occupiedBy?.plate ?: "none",
                        attemptedPlate = vehicle.plate,
                    ),
                )
            }

            else -> DomainResult.Success(copy(status = SpotStatus.AVAILABLE, occupiedBy = null))
        }
}
