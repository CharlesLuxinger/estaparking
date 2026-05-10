package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.fasterxml.jackson.annotation.JsonProperty

data class WebhookEventRequest(
    @JsonProperty("parking_id")
    val parkingId: String,
    @JsonProperty("license_plate")
    val licensePlate: String,
    @JsonProperty("event_type")
    val eventType: EventType,
) {
    fun toCommand() = WebhookEventCommand(parkingId, licensePlate, eventType)
}
