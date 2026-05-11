package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.config.ContainersConfig
import com.charlesluxinger.estaparking.config.EndpointIntegrationTestBase
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventCommand
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.model.WebhookEventOutcome
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.infra.persistence.billing.BillingRecordSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.billing.BillingSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.billing.BillingTransactionSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.billing.PricingSnapshotSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.event.ParkingEventSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.parking.ParkingSessionSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.spot.SpotSpringDataRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(
    EndpointIntegrationTestBase::class,
    ContainersConfig::class,
    WebhookControllerV1IT.TestConfig::class,
)
class WebhookControllerV1IT : EndpointIntegrationTestBase() {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var spyWebhookEventCommandPort: SpyWebhookEventCommandPort

    @Autowired
    private lateinit var parkingSessionSpringDataRepository: ParkingSessionSpringDataRepository

    @Autowired
    private lateinit var parkingEventSpringDataRepository: ParkingEventSpringDataRepository

    @Autowired
    private lateinit var billingTransactionSpringDataRepository: BillingTransactionSpringDataRepository

    @Autowired
    private lateinit var billingSpringDataRepository: BillingSpringDataRepository

    @Autowired
    private lateinit var billingRecordSpringDataRepository: BillingRecordSpringDataRepository

    @Autowired
    private lateinit var pricingSnapshotSpringDataRepository: PricingSnapshotSpringDataRepository

    @Autowired
    private lateinit var spotSpringDataRepository: SpotSpringDataRepository

    @BeforeEach
    fun setup() {
        billingTransactionSpringDataRepository.deleteAll()
        billingRecordSpringDataRepository.deleteAll()
        pricingSnapshotSpringDataRepository.deleteAll()
        billingSpringDataRepository.deleteAll()
        parkingEventSpringDataRepository.deleteAll()
        parkingSessionSpringDataRepository.deleteAll()
        spotSpringDataRepository.deleteAll()

        spyWebhookEventCommandPort.clear()
    }

    @Test
    fun `post webhook with ENTRY payload returns 200`() {
        val response = postWebhook(buildPayload("parking-entry", "ABC1234", EventType.ENTRY.name))

        assertEquals(HttpStatus.OK, response.statusCode)

        val command = spyWebhookEventCommandPort.lastCommand()
        assertEquals("parking-entry", command.parkingId)
        assertEquals("ABC1234", command.licensePlate)
        assertEquals(EventType.ENTRY, command.eventType)
    }

    @Test
    fun `post webhook with PARKED payload returns 200`() {
        val response = postWebhook(buildPayload("parking-parked", "ABC1234", EventType.PARKED.name))

        assertEquals(HttpStatus.OK, response.statusCode)

        val command = spyWebhookEventCommandPort.lastCommand()
        assertEquals("parking-parked", command.parkingId)
        assertEquals("ABC1234", command.licensePlate)
        assertEquals(EventType.PARKED, command.eventType)
    }

    @Test
    fun `post webhook with EXIT payload returns 200`() {
        val response = postWebhook(buildPayload("parking-exit", "ABC1234", EventType.EXIT.name))

        assertEquals(HttpStatus.OK, response.statusCode)

        val command = spyWebhookEventCommandPort.lastCommand()
        assertEquals("parking-exit", command.parkingId)
        assertEquals("ABC1234", command.licensePlate)
        assertEquals(EventType.EXIT, command.eventType)
    }

    @Test
    fun `post webhook with ENTRY and entry_time maps to occuredAt`() {
        val response =
            postWebhook(
                buildPayload(
                    parkingId = "parking-entry-time",
                    licensePlate = "ABC1234",
                    eventType = EventType.ENTRY.name,
                    timestampField = "entry_time",
                    timestampValue = "2025-01-01T10:00:00.000Z",
                ),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(spyWebhookEventCommandPort.lastCommand().occurredAt)
    }

    @Test
    fun `post webhook with EXIT and exit_time maps to occuredAt`() {
        val response =
            postWebhook(
                buildPayload(
                    parkingId = "parking-exit-time",
                    licensePlate = "ABC1234",
                    eventType = EventType.EXIT.name,
                    timestampField = "exit_time",
                    timestampValue = "2025-01-01T12:00:00.000Z",
                ),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(spyWebhookEventCommandPort.lastCommand().occurredAt)
    }

    @Test
    fun `post webhook without timestamp sets occuredAt to null`() {
        val response = postWebhook(buildPayload("parking-no-time", "ABC1234", EventType.ENTRY.name))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(spyWebhookEventCommandPort.lastCommand().occurredAt)
    }

    @Test
    fun `post duplicate ENTRY payload still returns 200`() {
        spyWebhookEventCommandPort.overrideNextOutcomes(
            WebhookEventOutcome.IgnoredDuplicate,
            WebhookEventOutcome.IgnoredDuplicate,
        )

        val payload = buildPayload("parking-entry", "ABC1234", EventType.ENTRY.name)

        val firstResponse = postWebhook(payload)
        val secondResponse = postWebhook(payload)

        assertEquals(HttpStatus.OK, firstResponse.statusCode)
        assertEquals(HttpStatus.OK, secondResponse.statusCode)
        assertEquals(2, spyWebhookEventCommandPort.receivedCount())
    }

    @Test
    fun `post rejected transition payload still returns 200`() {
        spyWebhookEventCommandPort.overrideNextOutcomes(
            WebhookEventOutcome.RejectedTransition(
                ParkingDomainError.InvalidParkedOrdering(
                    spotId = 1L,
                    currentStatus = SpotStatus.AVAILABLE,
                ),
            ),
        )

        val response = postWebhook(buildPayload("parking-rejected", "ABC1234", EventType.PARKED.name))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, spyWebhookEventCommandPort.receivedCount())
    }

    private fun postWebhook(payload: String): ResponseEntity<Unit> {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val request = HttpEntity(payload, headers)

        return restTemplate.postForEntity("/webhook", request, Unit::class.java)
    }

    private fun buildPayload(
        parkingId: String,
        licensePlate: String,
        eventType: String,
        timestampField: String? = null,
        timestampValue: String? = null,
    ): String {
        val timestampJson =
            if (timestampField != null && timestampValue != null) {
                ",\"$timestampField\":\"$timestampValue\""
            } else {
                ""
            }

        return """
            |{
            |    "parking_id":"$parkingId",
            |    "license_plate":"$licensePlate",
            |    "event_type":"$eventType"$timestampJson
            |}
            """.trimIndent().replace("|", "")
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun spyWebhookEventCommandPort(
            @Qualifier("handleWebhookEventUseCase") realWebhookEventCommandPort: WebhookEventCommandPort,
        ): SpyWebhookEventCommandPort = SpyWebhookEventCommandPort(realWebhookEventCommandPort)
    }

    class SpyWebhookEventCommandPort(
        private val delegate: WebhookEventCommandPort,
    ) : WebhookEventCommandPort {
        private val received = mutableListOf<WebhookEventCommand>()
        private val overriddenOutcomes = mutableListOf<WebhookEventOutcome>()

        override fun handle(command: WebhookEventCommand): WebhookEventOutcome {
            received.add(command)

            val overridden = overriddenOutcomes.firstOrNull()?.also { overriddenOutcomes.removeAt(0) }
            return overridden ?: delegate.handle(command)
        }

        fun overrideNextOutcomes(vararg outcomes: WebhookEventOutcome) {
            overriddenOutcomes.clear()
            outcomes.forEach(overriddenOutcomes::add)
        }

        fun lastCommand(): WebhookEventCommand = received.last()

        fun receivedCount(): Int = received.size

        fun clear() {
            received.clear()
            overriddenOutcomes.clear()
        }
    }
}
