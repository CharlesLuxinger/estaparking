package com.charlesluxinger.estaparking.domain.event

import com.charlesluxinger.estaparking.domain.common.Coordinates
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.time.LocalDateTime

sealed interface ParkingEvent {
    val parkingId: String
    val eventType: EventType
    val vehicle: Vehicle
    val coordinates: Coordinates
    val timestamp: LocalDateTime
}
