package com.charlesluxinger.estaparking.infra.client.simulator.garage

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.PayloadMappingFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.TransportFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.UnexpectedStatus
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate

class SimulatorGarageRestTemplateAdapterTest {
    @Test
    fun `fetchGarage maps simulator payload to domain snapshot`() {
        val restTemplate = RestTemplate()
        val server = MockRestServiceServer.bindTo(restTemplate).build()
        server
            .expect(once(), requestTo("http://simulator.local/garage"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                        """
                        {
                          "garage": [{"sector": "A", "basePrice": 10.0, "max_capacity": 100}],
                          "spots": [{"id": 1, "sector": "A", "lat": -23.561684, "lng": -46.655981}]
                        }
                        """.trimIndent(),
                    ),
            )

        val adapter =
            SimulatorGarageRestTemplateAdapter(
                simulatorBaseUrl = "http://simulator.local",
                restTemplate = restTemplate,
                objectMapper = jacksonObjectMapper(),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Success)
        result as DomainResult.Success
        assertEquals(
            "A",
            result.value.garages
                .first()
                .sector,
        )
        assertEquals(
            1L,
            result.value.spots
                .first()
                .id,
        )
        server.verify()
    }

    @Test
    fun `fetchGarage translates non-2xx response into unexpected status error`() {
        val restTemplate = RestTemplate()
        val server = MockRestServiceServer.bindTo(restTemplate).build()
        server
            .expect(once(), requestTo("http://simulator.local/garage"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.BAD_GATEWAY).body("downstream failure"))

        val adapter =
            SimulatorGarageRestTemplateAdapter(
                simulatorBaseUrl = "http://simulator.local",
                restTemplate = restTemplate,
                objectMapper = jacksonObjectMapper(),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Error)
        result as DomainResult.Error
        assertTrue(result.error is UnexpectedStatus)
        val error = result.error as UnexpectedStatus
        assertEquals(502, error.statusCode)
        assertTrue(error.responseBody.contains("downstream"))
    }

    @Test
    fun `fetchGarage translates malformed payload into mapping failure`() {
        val restTemplate = RestTemplate()
        val server = MockRestServiceServer.bindTo(restTemplate).build()
        server
            .expect(once(), requestTo("http://simulator.local/garage"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""{"garage":[{"sector":"A","basePrice":"abc","max_capacity":100}],"spots":[]}"""),
            )

        val adapter =
            SimulatorGarageRestTemplateAdapter(
                simulatorBaseUrl = "http://simulator.local",
                restTemplate = restTemplate,
                objectMapper = jacksonObjectMapper(),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Error)
        result as DomainResult.Error
        assertTrue(result.error is PayloadMappingFailure)
    }

    @Test
    fun `fetchGarage translates transport errors into transport failure`() {
        val restTemplate = mockk<RestTemplate>()
        every {
            restTemplate.getForEntity("http://simulator.local/garage", String::class.java)
        } throws ResourceAccessException("Connection refused")

        val adapter =
            SimulatorGarageRestTemplateAdapter(
                simulatorBaseUrl = "http://simulator.local",
                restTemplate = restTemplate,
                objectMapper = jacksonObjectMapper(),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Error)
        result as DomainResult.Error
        assertTrue(result.error is TransportFailure)
    }

    @Test
    fun `fetchGarage rethrows unknown failures`() {
        val restTemplate = mockk<RestTemplate>()
        every {
            restTemplate.getForEntity("http://simulator.local/garage", String::class.java)
        } throws IllegalStateException("Unexpected failure")

        val adapter =
            SimulatorGarageRestTemplateAdapter(
                simulatorBaseUrl = "http://simulator.local",
                restTemplate = restTemplate,
                objectMapper = jacksonObjectMapper(),
            )

        assertThrows(IllegalStateException::class.java) {
            adapter.fetchGarage()
        }
    }
}
