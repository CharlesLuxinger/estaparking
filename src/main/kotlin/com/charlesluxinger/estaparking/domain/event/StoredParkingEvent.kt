package com.charlesluxinger.estaparking.domain.event

data class StoredParkingEvent(
    override val parkingId: String,
    override val licensePlate: String,
    override val eventType: EventType,
) : ParkingEvent
