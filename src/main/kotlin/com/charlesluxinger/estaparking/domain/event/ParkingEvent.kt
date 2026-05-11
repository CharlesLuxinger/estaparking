package com.charlesluxinger.estaparking.domain.event

import java.time.LocalDateTime

sealed interface ParkingEvent {
    val parkingId: String
    val eventType: EventType
    val licensePlate: String
    val timestamp: LocalDateTime
}
