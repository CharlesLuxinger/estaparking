package com.charlesluxinger.estaparking.domain.event

import java.time.LocalDateTime

data class StoredParkingEvent(
    override val parkingId: String,
    override val licensePlate: String,
    override val eventType: EventType,
    override val timestamp: LocalDateTime,
) : ParkingEvent
