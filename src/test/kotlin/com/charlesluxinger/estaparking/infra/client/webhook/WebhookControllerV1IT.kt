package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(WebhookControllerV1::class)
@Import(WebhookControllerV1IT.TestConfig::class)
class WebhookControllerV1IT {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var recordingWebhookEventCommandPort: RecordingWebhookEventCommandPort

    @Test
    fun `post webhook with ENTRY payload returns 200`() {
        recordingWebhookEventCommandPort.clear()

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"parking_id\":\"parking-entry\"," +
                            "\"license_plate\":\"ABC1234\"," +
                            "\"event_type\":\"ENTRY\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)

        val command = recordingWebhookEventCommandPort.lastCommand()
        org.junit.jupiter.api.Assertions
            .assertEquals("parking-entry", command.parkingId)
        org.junit.jupiter.api.Assertions
            .assertEquals("ABC1234", command.licensePlate)
        org.junit.jupiter.api.Assertions
            .assertEquals(EventType.ENTRY, command.eventType)
    }

    @Test
    fun `post webhook with PARKED payload returns 200`() {
        recordingWebhookEventCommandPort.clear()

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"parking_id\":\"parking-parked\"," +
                            "\"license_plate\":\"ABC1234\"," +
                            "\"event_type\":\"PARKED\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)

        val command = recordingWebhookEventCommandPort.lastCommand()
        org.junit.jupiter.api.Assertions
            .assertEquals("parking-parked", command.parkingId)
        org.junit.jupiter.api.Assertions
            .assertEquals("ABC1234", command.licensePlate)
        org.junit.jupiter.api.Assertions
            .assertEquals(EventType.PARKED, command.eventType)
    }

    @Test
    fun `post webhook with EXIT payload returns 200`() {
        recordingWebhookEventCommandPort.clear()

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"parking_id\":\"parking-exit\"," +
                            "\"license_plate\":\"ABC1234\"," +
                            "\"event_type\":\"EXIT\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)

        val command = recordingWebhookEventCommandPort.lastCommand()
        org.junit.jupiter.api.Assertions
            .assertEquals("parking-exit", command.parkingId)
        org.junit.jupiter.api.Assertions
            .assertEquals("ABC1234", command.licensePlate)
        org.junit.jupiter.api.Assertions
            .assertEquals(EventType.EXIT, command.eventType)
    }

    @Test
    fun `post webhook with ENTRY and entry_time maps to occuredAt`() {
        recordingWebhookEventCommandPort.clear()

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"parking_id\":\"parking-entry-time\"," +
                            "\"license_plate\":\"ABC1234\"," +
                            "\"event_type\":\"ENTRY\"," +
                            "\"entry_time\":\"2025-01-01T10:00:00.000Z\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)

        val command = recordingWebhookEventCommandPort.lastCommand()
        org.junit.jupiter.api.Assertions
            .assertNotNull(command.occurredAt)
    }

    @Test
    fun `post webhook with EXIT and exit_time maps to occuredAt`() {
        recordingWebhookEventCommandPort.clear()

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"parking_id\":\"parking-exit-time\"," +
                            "\"license_plate\":\"ABC1234\"," +
                            "\"event_type\":\"EXIT\"," +
                            "\"exit_time\":\"2025-01-01T12:00:00.000Z\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)

        val command = recordingWebhookEventCommandPort.lastCommand()
        org.junit.jupiter.api.Assertions
            .assertNotNull(command.occurredAt)
    }

    @Test
    fun `post webhook without timestamp sets occuredAt to null`() {
        recordingWebhookEventCommandPort.clear()

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"parking_id\":\"parking-no-time\"," +
                            "\"license_plate\":\"ABC1234\"," +
                            "\"event_type\":\"ENTRY\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)

        val command = recordingWebhookEventCommandPort.lastCommand()
        org.junit.jupiter.api.Assertions
            .assertNull(command.occurredAt)
    }

    @Test
    fun `post duplicate ENTRY payload still returns 200`() {
        recordingWebhookEventCommandPort.clear()
        recordingWebhookEventCommandPort.nextOutcome = WebhookEventOutcome.IgnoredDuplicate

        val duplicatePayload =
            "{" +
                "\"parking_id\":\"parking-entry\"," +
                "\"license_plate\":\"ABC1234\"," +
                "\"event_type\":\"ENTRY\"" +
                "}"

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(duplicatePayload),
            ).andExpect(status().isOk)

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(duplicatePayload),
            ).andExpect(status().isOk)

        org.junit.jupiter.api.Assertions
            .assertEquals(2, recordingWebhookEventCommandPort.receivedCount())
    }

    @Test
    fun `post rejected transition payload still returns 200`() {
        recordingWebhookEventCommandPort.clear()
        recordingWebhookEventCommandPort.nextOutcome =
            WebhookEventOutcome.RejectedTransition(
                ParkingDomainError.InvalidParkedOrdering(
                    spotId = 1L,
                    currentStatus = SpotStatus.AVAILABLE,
                ),
            )

        mockMvc
            .perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"parking_id\":\"parking-rejected\"," +
                            "\"license_plate\":\"ABC1234\"," +
                            "\"event_type\":\"PARKED\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)

        org.junit.jupiter.api.Assertions
            .assertEquals(1, recordingWebhookEventCommandPort.receivedCount())
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        fun recordingWebhookEventCommandPort(): RecordingWebhookEventCommandPort = RecordingWebhookEventCommandPort()

        @Bean
        fun webhookEventCommandPort(recordingPort: RecordingWebhookEventCommandPort): WebhookEventCommandPort =
            recordingPort
    }

    class RecordingWebhookEventCommandPort : WebhookEventCommandPort {
        private val received = mutableListOf<WebhookEventCommand>()
        var nextOutcome: WebhookEventOutcome = WebhookEventOutcome.Processed

        override fun handle(command: WebhookEventCommand): WebhookEventOutcome {
            received.add(command)
            return nextOutcome
        }

        fun lastCommand(): WebhookEventCommand = received.last()

        fun clear() {
            received.clear()
            nextOutcome = WebhookEventOutcome.Processed
        }

        fun receivedCount(): Int = received.size
    }
}
