package com.charlesluxinger.estaparking.domain.port.inbound.webhook.model

import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.time.LocalDateTime

data class WebhookEventCommand(
    val parkingId: String,
    val vehicle: Vehicle,
    val eventType: EventType,
    val occurredAt: LocalDateTime? = null,
) {
    init {
        require(parkingId.isNotBlank()) { "Parking id must not be blank" }
    }
}
