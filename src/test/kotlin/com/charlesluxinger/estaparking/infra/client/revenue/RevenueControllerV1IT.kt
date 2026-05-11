package com.charlesluxinger.estaparking.infra.client.revenue

import com.charlesluxinger.estaparking.config.ContainersConfig
import com.charlesluxinger.estaparking.config.EndpointIntegrationTestBase
import com.charlesluxinger.estaparking.domain.billing.BillingTransaction
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import com.charlesluxinger.estaparking.infra.persistence.billing.BillingTransactionJpaAdapter
import com.charlesluxinger.estaparking.infra.persistence.billing.BillingTransactionSpringDataRepository
import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EndpointIntegrationTestBase::class, ContainersConfig::class)
@ActiveProfiles("test")
class RevenueControllerV1IT : EndpointIntegrationTestBase() {
    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var billingTransactionJpaAdapter: BillingTransactionJpaAdapter

    @Autowired
    private lateinit var billingTransactionSpringDataRepository: BillingTransactionSpringDataRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun cleanup() {
        billingTransactionSpringDataRepository.deleteAll()
    }

    @Test
    fun `get revenue with valid request returns 200`() {
        billingTransactionJpaAdapter.save(
            BillingTransaction(
                id = "bt-1",
                vehicle = Vehicle("ABC1234"),
                sector = "A",
                amount = BigDecimal("100.00"),
                exitTime = LocalDateTime.of(2025, 1, 1, 10, 0),
                createdAt = LocalDateTime.of(2025, 1, 1, 10, 0),
            ),
        )

        val response =
            executeRevenueRequest(
                "{" +
                    "\"date\":\"2025-01-01\"," +
                    "\"sector\":\"A\"" +
                    "}",
            )

        assertEquals(200, response.statusCode.value())
        val body = objectMapper.readTree(response.body)
        assertTrue(body.path("amount").decimalValue().compareTo(BigDecimal("100.00")) == 0)
        assertEquals("BRL", body.path("currency").asText())
        assertNotNull(body.path("timestamp").asText().takeIf { it.isNotBlank() })
    }

    @Test
    fun `get revenue with missing date returns 400`() {
        val response =
            executeRevenueRequest(
                "{" +
                    "\"sector\":\"A\"" +
                    "}",
            )

        assertEquals(400, response.statusCode.value())
    }

    @Test
    fun `get revenue with missing sector returns 400`() {
        val response =
            executeRevenueRequest(
                "{" +
                    "\"date\":\"2025-01-01\"" +
                    "}",
            )

        assertEquals(400, response.statusCode.value())
    }

    @Test
    fun `get revenue with empty sector returns 400`() {
        val response =
            executeRevenueRequest(
                "{" +
                    "\"date\":\"2025-01-01\"," +
                    "\"sector\":\"\"" +
                    "}",
            )

        assertEquals(400, response.statusCode.value())
    }

    @Test
    fun `get revenue returns zero amount when no data`() {
        val response =
            executeRevenueRequest(
                "{" +
                    "\"date\":\"2025-01-01\"," +
                    "\"sector\":\"A\"" +
                    "}",
            )

        assertEquals(200, response.statusCode.value())
        val body = objectMapper.readTree(response.body)
        assertTrue(body.path("amount").decimalValue().compareTo(BigDecimal.ZERO) == 0)
        assertEquals("BRL", body.path("currency").asText())
    }

    private fun executeRevenueRequest(requestBody: String): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(requestBody, headers)

        return testRestTemplate.exchange("/revenue", HttpMethod.GET, request, String::class.java)
    }
}
