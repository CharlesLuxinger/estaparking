package com.charlesluxinger.estaparking.domain.event

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.time.LocalDateTime

sealed interface ParkingEvent {
    val parkingId: String
    val eventType: EventType
    val vehicle: Vehicle
    val timestamp: LocalDateTime
}
