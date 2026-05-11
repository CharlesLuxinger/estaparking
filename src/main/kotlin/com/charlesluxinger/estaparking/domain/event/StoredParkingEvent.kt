package com.charlesluxinger.estaparking.domain.event

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.time.LocalDateTime

data class StoredParkingEvent(
    override val parkingId: String,
    override val vehicle: Vehicle,
    override val eventType: EventType,
    override val timestamp: LocalDateTime,
) : ParkingEvent
