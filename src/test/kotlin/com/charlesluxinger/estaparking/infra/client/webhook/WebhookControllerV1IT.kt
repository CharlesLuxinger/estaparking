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
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
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
    @LocalServerPort
    private var serverPort: Int = 0

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
        assertEquals("ABC1234", command.vehicle.plate)
        assertEquals(EventType.ENTRY, command.eventType)
    }

    @Test
    fun `post webhook with PARKED payload returns 200`() {
        val response = postWebhook(buildPayload("parking-parked", "ABC1234", EventType.PARKED.name))

        assertEquals(HttpStatus.OK, response.statusCode)

        val command = spyWebhookEventCommandPort.lastCommand()
        assertEquals("parking-parked", command.parkingId)
        assertEquals("ABC1234", command.vehicle.plate)
        assertEquals(EventType.PARKED, command.eventType)
    }

    @Test
    fun `post webhook with EXIT payload returns 200`() {
        val response = postWebhook(buildPayload("parking-exit", "ABC1234", EventType.EXIT.name))

        assertEquals(HttpStatus.OK, response.statusCode)

        val command = spyWebhookEventCommandPort.lastCommand()
        assertEquals("parking-exit", command.parkingId)
        assertEquals("ABC1234", command.vehicle.plate)
        assertEquals(EventType.EXIT, command.eventType)
    }

    @Test
    fun `post webhook with ENTRY and entry_time maps to occurredAt`() {
        val response =
            postWebhook(
                buildPayload(
                    parkingId = "parking-entry-time",
                    licensePlate = "ABC1234",
                    eventType = EventType.ENTRY.name,
                    entryTime = LocalDateTime.of(2025, 1, 1, 12, 0)
                ),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(spyWebhookEventCommandPort.lastCommand().occurredAt)
    }

    @Test
    fun `post webhook with EXIT and exit_time maps to occurredAt`() {
        val response =
            postWebhook(
                buildPayload(
                    parkingId = "parking-exit-time",
                    licensePlate = "ABC1234",
                    eventType = EventType.EXIT.name,
                    exitTime = LocalDateTime.of(2025, 1, 1, 12, 0),
                ),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(spyWebhookEventCommandPort.lastCommand().occurredAt)
    }

    @Test
    fun `post webhook without timestamp sets occurredAt to null`() {
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

    @Test
    fun `post webhook missing parking_id returns client error and keeps database clean`() {
        val response =
            postWebhook(
                """
                {
                    "license_plate":"ABC1234",
                    "event_type":"ENTRY"
                }
                """.trimIndent(),
            )
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode.value())
    }

    @Test
    fun `missing parking_id returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "license_plate":"ABC1234",
                "event_type":"ENTRY"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
            .contentType("application/problem+json")
            .body("status", org.hamcrest.Matchers.equalTo(HttpStatus.BAD_REQUEST.value()))
            .body("title", org.hamcrest.Matchers.equalTo("Bad Request"))
            .body("code", org.hamcrest.Matchers.equalTo("BAD_REQUEST"))
            .body("traceId", org.hamcrest.Matchers.not(org.hamcrest.Matchers.blankOrNullString()))
    }

    @Test
    fun `not found exception returns 404 problem detail`() {
        spyWebhookEventCommandPort.failNext(NoSuchElementException("parking session missing"))

        postWebhookWithRestAssured(
            buildPayload("parking-missing", "ABC1234", EventType.ENTRY.name),
        ).statusCode(HttpStatus.NOT_FOUND.value())
            .contentType("application/problem+json")
            .body("status", org.hamcrest.Matchers.equalTo(HttpStatus.NOT_FOUND.value()))
            .body("title", org.hamcrest.Matchers.equalTo("Not Found"))
            .body("detail", org.hamcrest.Matchers.equalTo("Resource not found."))
            .body("instance", org.hamcrest.Matchers.equalTo("/webhook"))
            .body("code", org.hamcrest.Matchers.equalTo("NOT_FOUND"))
            .body("traceId", org.hamcrest.Matchers.not(org.hamcrest.Matchers.blankOrNullString()))
    }

    @Test
    fun `unexpected exception returns sanitized 500 problem detail`() {
        spyWebhookEventCommandPort.failNext(IllegalStateException("database credentials leaked"))

        val response =
            postWebhookWithRestAssured(
                buildPayload("parking-error", "ABC1234", EventType.ENTRY.name),
            ).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType("application/problem+json")
                .extract()
                .response()

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.statusCode)
        assertEquals("application/problem+json", response.contentType)
        assertTrue(response.jsonPath().getString("title") == "Internal Server Error")
        assertTrue(response.jsonPath().getString("detail") == "An unexpected error occurred.")
        assertTrue(response.jsonPath().getString("instance") == "/webhook")
        assertTrue(response.jsonPath().getString("code") == "INTERNAL_ERROR")
        assertTrue(response.jsonPath().getString("traceId").isNotBlank())
        assertTrue(!response.body.asString().contains("database credentials leaked"))
    }

    @Test
    fun `missing license_plate returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "parking_id":"parking-missing-plate",
                "event_type":"ENTRY"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `missing event_type returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "parking_id":"parking-missing-event-type",
                "license_plate":"ABC1234"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `invalid event_type returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "parking_id":"parking-invalid-event-type",
                "license_plate":"ABC1234",
                "event_type":"INVALID_EVENT_TYPE"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `null parking_id returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "parking_id":null,
                "license_plate":"ABC1234",
                "event_type":"ENTRY"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `empty string parking_id returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "parking_id":"",
                "license_plate":"ABC1234",
                "event_type":"ENTRY"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `license plate invalid format returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "parking_id":"parking-invalid-license-plate",
                "license_plate":"abc1234",
                "event_type":"ENTRY"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `empty string license_plate returns 400 validation error`() {
        postWebhookWithRestAssured(
            """
            {
                "parking_id":"parking-empty-license-plate",
                "license_plate":"",
                "event_type":"ENTRY"
            }
            """.trimIndent(),
        ).statusCode(HttpStatus.BAD_REQUEST.value())
    }

    private fun postWebhook(payload: String): ResponseEntity<Unit> {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val request = HttpEntity(payload, headers)

        return restTemplate.postForEntity("/webhook", request, Unit::class.java)
    }

    private fun postWebhookWithRestAssured(payload: String) =
        Given {
            port(serverPort)
            contentType(ContentType.JSON)
            body(payload)
        } When {
            post("/webhook")
        } Then {
        }

    private fun buildPayload(
        parkingId: String = "parking-1",
        licensePlate: String = "ABC1234",
        eventType: String = EventType.ENTRY.name,
        entryTime: LocalDateTime? = null,
        exitTime: LocalDateTime? = null,
    ): String {
        val timestampJson =
            when {
                entryTime != null -> """, "entry_time":"$entryTime""""
                exitTime != null -> """, "exit_time":"$exitTime""""
                else -> ""
            }
        return """
            {
                "parking_id":"$parkingId",
                "license_plate":"$licensePlate",
                "event_type":"$eventType",
                "lat": -23.5505,
                "lng": -46.6333
                $timestampJson
            }
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
        private val overriddenFailures = mutableListOf<Exception>()

        override fun handle(command: WebhookEventCommand): WebhookEventOutcome {
            received.add(command)

            overriddenFailures.firstOrNull()?.also {
                overriddenFailures.removeAt(0)
                throw it
            }

            val overridden = overriddenOutcomes.firstOrNull()?.also { overriddenOutcomes.removeAt(0) }
            return overridden ?: delegate.handle(command)
        }

        fun overrideNextOutcomes(vararg outcomes: WebhookEventOutcome) {
            overriddenOutcomes.clear()
            outcomes.forEach(overriddenOutcomes::add)
        }

        fun failNext(vararg exceptions: Exception) {
            overriddenFailures.clear()
            exceptions.forEach(overriddenFailures::add)
        }

        fun lastCommand(): WebhookEventCommand = received.last()

        fun receivedCount(): Int = received.size

        fun clear() {
            received.clear()
            overriddenOutcomes.clear()
            overriddenFailures.clear()
        }
    }
}
