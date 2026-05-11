package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class WebhookEventRequest(
    @JsonProperty("parking_id")
    val parkingId: String,
    @JsonProperty("license_plate")
    val licensePlate: String,
    @JsonProperty("event_type")
    val eventType: EventType,
    @JsonProperty("exit_time")
    val exitTime: LocalDateTime? = null,
    @JsonProperty("entry_time")
    val entryTime: LocalDateTime? = null,
) {
    fun toCommand(): WebhookEventCommand {
        val timestamp =
            when (eventType) {
                EventType.ENTRY -> entryTime
                EventType.EXIT -> exitTime
                EventType.PARKED -> null
            }
        return WebhookEventCommand(parkingId, licensePlate, eventType, timestamp)
    }
}
