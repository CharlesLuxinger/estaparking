package com.charlesluxinger.estaparking.domain.parking

import com.charlesluxinger.estaparking.domain.common.Coordinates
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.result.DomainResult.Error
import com.charlesluxinger.estaparking.domain.result.DomainResult.Success
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.math.RoundingMode

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
        coordinates: Coordinates? = null,
    ): DomainResult<Parking, ParkingDomainError> =
        when (eventType) {
            EventType.ENTRY -> allocateForEntry(vehicle)
            EventType.PARKED -> transitionOccupiedSpot(eventType, vehicle, coordinates)
            EventType.EXIT -> transitionOccupiedSpot(eventType, vehicle, coordinates)
        }

    private fun allocateForEntry(vehicle: Vehicle): DomainResult<Parking, ParkingDomainError> =
        if (isFull()) {
            Error(ParkingDomainError.FullOccupancyEntryDenied)
        } else {
            val nextSpots = spots.toMutableList()
            val availableIndex = spots.indexOfFirst { it.canAcceptEntry() }

            when (val transition = spots[availableIndex].transition(EventType.ENTRY, vehicle)) {
                is Success -> {
                    nextSpots[availableIndex] = transition.value
                    Success(copy(spots = nextSpots))
                }

                is Error -> Error(transition.error)
            }
        }

    private fun transitionOccupiedSpot(
        eventType: EventType,
        vehicle: Vehicle,
        coordinates: Coordinates? = null,
    ): DomainResult<Parking, ParkingDomainError> {
        val index = spots.indexOfFirst { it.occupiedBy == vehicle }

        return if (index < 0) {
            resolveMissingVehicleTransition(eventType, vehicle)
        } else {
            when (val transition = spots[index].transition(eventType, vehicle, coordinates)) {
                is Success -> {
                    val nextSpots = spots.toMutableList().apply { this[index] = transition.value }
                    Success(copy(spots = nextSpots))
                }

                is Error -> Error(transition.error)
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
            return Error(
                ParkingDomainError.WrongVehicleTransitionAttempt(
                    spotId = spotExpectingDifferentVehicle.id,
                    expectedPlate = spotExpectingDifferentVehicle.occupiedBy!!.plate,
                    attemptedPlate = vehicle.plate,
                ),
            )
        }

        return when (eventType) {
            EventType.PARKED -> {
                Error(
                    ParkingDomainError.InvalidParkedOrdering(
                        spotId = -1L,
                        currentStatus = SpotStatus.AVAILABLE,
                    ),
                )
            }

            EventType.EXIT -> {
                Error(
                    ParkingDomainError.ExitBeforeEntry(
                        spotId = -1L,
                        currentStatus = SpotStatus.AVAILABLE,
                    ),
                )
            }

            EventType.ENTRY -> {
                Error(
                    ParkingDomainError.VehicleNotFoundForTransition(
                        eventType = eventType,
                        vehiclePlate = vehicle.plate,
                    ),
                )
            }
        }
    }

    fun occupancyPercentage(): BigDecimal {
        if (this.spots.isEmpty()) {
            return BigDecimal.ZERO
        }

        val occupied = this.spots.count(Spot::canAcceptEntry).let { this.spots.size - it }
        return BigDecimal
            .valueOf(occupied.toLong())
            .multiply(BigDecimal("100"))
            .divide(BigDecimal.valueOf(this.spots.size.toLong()), 2, RoundingMode.HALF_UP)
    }
}
