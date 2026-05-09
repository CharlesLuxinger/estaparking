package com.charlesluxinger.estaparking.domain.event

sealed interface ParkingEvent {
    val parkingId: String
    val eventType: EventType
}
