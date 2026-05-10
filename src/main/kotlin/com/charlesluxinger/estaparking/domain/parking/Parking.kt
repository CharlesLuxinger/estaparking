package com.charlesluxinger.estaparking.domain.parking

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.error.DomainResult.Error
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

data class Parking(
    val id: String,
    val name: String,
    val spots: List<Spot> = emptyList(),
) {
    init {
        require(id.isNotBlank()) { "Parking id must not be blank" }
        require(name.isNotBlank()) { "Parking name must not be blank" }
    }

    fun isFull(): Boolean = spots.none { it.canAcceptEntry() }

    fun apply(
        eventType: EventType,
        vehicle: Vehicle,
    ): DomainResult<Parking, ParkingDomainError> =
        when (eventType) {
            EventType.ENTRY -> allocateForEntry(vehicle)
            EventType.PARKED -> transitionOccupiedSpot(eventType, vehicle)
            EventType.EXIT -> transitionOccupiedSpot(eventType, vehicle)
        }

    private fun allocateForEntry(vehicle: Vehicle): DomainResult<Parking, ParkingDomainError> =
        if (isFull()) {
            DomainResult.Error(ParkingDomainError.FullOccupancyEntryDenied)
        } else {
            val nextSpots = spots.toMutableList()
            val availableIndex = spots.indexOfFirst { it.canAcceptEntry() }

            when (val transition = spots[availableIndex].transition(EventType.ENTRY, vehicle)) {
                is DomainResult.Success -> {
                    nextSpots[availableIndex] = transition.value
                    DomainResult.Success(copy(spots = nextSpots))
                }

                is Error -> DomainResult.Error(transition.error)
            }
        }

    private fun transitionOccupiedSpot(
        eventType: EventType,
        vehicle: Vehicle,
    ): DomainResult<Parking, ParkingDomainError> {
        val index = spots.indexOfFirst { it.occupiedBy == vehicle }

        return if (index < 0) {
            resolveMissingVehicleTransition(eventType, vehicle)
        } else {
            when (val transition = spots[index].transition(eventType, vehicle)) {
                is DomainResult.Success -> {
                    val nextSpots = spots.toMutableList().apply { this[index] = transition.value }
                    DomainResult.Success(copy(spots = nextSpots))
                }

                is Error -> DomainResult.Error(transition.error)
            }
        }
    }

    private fun resolveMissingVehicleTransition(
        eventType: EventType,
        vehicle: Vehicle,
    ): DomainResult<Parking, ParkingDomainError> {
        val expectedStatus =
            when (eventType) {
                EventType.PARKED -> SpotStatus.ENTRY_REGISTERED
                EventType.EXIT -> SpotStatus.PARKED
                EventType.ENTRY -> SpotStatus.AVAILABLE
            }

        val spotExpectingDifferentVehicle = spots.firstOrNull { it.status == expectedStatus && it.occupiedBy != null }
        if (spotExpectingDifferentVehicle != null) {
            return DomainResult.Error(
                ParkingDomainError.WrongVehicleTransitionAttempt(
                    spotId = spotExpectingDifferentVehicle.id,
                    expectedPlate = spotExpectingDifferentVehicle.occupiedBy!!.plate,
                    attemptedPlate = vehicle.plate,
                ),
            )
        }

        return when (eventType) {
            EventType.PARKED -> {
                DomainResult.Error(
                    ParkingDomainError.InvalidParkedOrdering(
                        spotId = "unknown",
                        currentStatus = SpotStatus.AVAILABLE,
                    ),
                )
            }

            EventType.EXIT -> {
                DomainResult.Error(
                    ParkingDomainError.ExitBeforeEntry(
                        spotId = "unknown",
                        currentStatus = SpotStatus.AVAILABLE,
                    ),
                )
            }

            EventType.ENTRY -> {
                DomainResult.Error(
                    ParkingDomainError.VehicleNotFoundForTransition(
                        eventType = eventType,
                        vehiclePlate = vehicle.plate,
                    ),
                )
            }
        }
    }
}
