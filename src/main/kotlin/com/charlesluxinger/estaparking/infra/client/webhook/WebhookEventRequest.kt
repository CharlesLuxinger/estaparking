package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.domain.common.Coordinates
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
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
    @JsonProperty("lat")
    val latitude: BigDecimal,
    @JsonProperty("lng")
    val longitude: BigDecimal,
) {
    fun toCommand(): WebhookEventCommand {
        val timestamp =
            when (eventType) {
                EventType.ENTRY -> entryTime
                EventType.EXIT -> exitTime
                EventType.PARKED -> null
            }
        return WebhookEventCommand(
            parkingId = parkingId,
            vehicle = Vehicle(licensePlate),
            eventType = eventType,
            occurredAt = timestamp,
            coordinates = Coordinates(latitude, longitude),
        )
    }
}
