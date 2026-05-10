package com.charlesluxinger.estaparking.domain.event

import java.time.Instant
import java.time.Instant.now

data class PublishedLifecycleEvent(
    val parkingId: String,
    val licensePlate: String,
    val eventType: EventType,
    val occurredAt: Instant,
) {
    init {
        require(parkingId.isNotBlank()) { "Parking id must not be blank" }
        require(licensePlate.isNotBlank()) { "License plate must not be blank" }
        require(occurredAt.isBefore(now())) { "Occurred At cannot be a future date" }
    }
}
