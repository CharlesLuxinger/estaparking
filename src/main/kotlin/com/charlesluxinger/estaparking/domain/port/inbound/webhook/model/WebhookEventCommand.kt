package com.charlesluxinger.estaparking.domain.port.inbound.webhook.model

import com.charlesluxinger.estaparking.domain.event.EventType
import java.time.LocalDateTime

data class WebhookEventCommand(
    val parkingId: String,
    val licensePlate: String,
    val eventType: EventType,
    val occurredAt: LocalDateTime? = null,
)
