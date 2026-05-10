package com.charlesluxinger.estaparking.infra.client.simulator.garage

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.PayloadMappingFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.TransportFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.UnexpectedStatus
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.jackson.jackson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.ConnectException

class SimulatorGarageKtorAdapterTest {
    @Test
    fun `fetchGarage maps simulator payload to domain snapshot`() {
        val engine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "garage": [
                            {
                              "sector": "A",
                              "basePrice": 10.0,
                              "max_capacity": 100
                            }
                          ],
                          "spots": [
                            {
                              "id": 1,
                              "sector": "A",
                              "lat": -23.561684,
                              "lng": -46.655981
                            }
                          ]
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

        val adapter =
            SimulatorGarageKtorAdapter(
                simulatorBaseUrl = "http://simulator.local",
                client = mockHttpClient(engine),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Success)
        result as DomainResult.Success

        assertEquals(1, result.value.garages.size)
        assertEquals(
            "A",
            result.value.garages
                .first()
                .sector,
        )
        assertEquals(1, result.value.spots.size)
        assertEquals(
            "1",
            result.value.spots
                .first()
                .id,
        )
    }

    @Test
    fun `fetchGarage translates non-2xx response into unexpected status error`() {
        val engine =
            MockEngine {
                respond(
                    content = "downstream failure",
                    status = HttpStatusCode.BadGateway,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
                )
            }

        val adapter =
            SimulatorGarageKtorAdapter(
                simulatorBaseUrl = "http://simulator.local",
                client = mockHttpClient(engine),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Error)
        result as DomainResult.Error
        assertTrue(result.error is UnexpectedStatus)

        val error = result.error as UnexpectedStatus
        assertEquals(502, error.statusCode)
        assertEquals("downstream failure", error.responseBody)
    }

    @Test
    fun `fetchGarage translates malformed payload into mapping failure`() {
        val engine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "garage": [
                            {
                              "sector": "A",
                              "basePrice": "abc",
                              "max_capacity": 100
                            }
                          ],
                          "spots": []
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }

        val adapter =
            SimulatorGarageKtorAdapter(
                simulatorBaseUrl = "http://simulator.local",
                client = mockHttpClient(engine),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Error)
        result as DomainResult.Error
        assertTrue(result.error is PayloadMappingFailure)
    }

    @Test
    fun `fetchGarage translates transport errors into transport failure`() {
        val engine =
            MockEngine {
                throw ConnectException("Connection refused")
            }

        val adapter =
            SimulatorGarageKtorAdapter(
                simulatorBaseUrl = "http://simulator.local",
                client = mockHttpClient(engine),
            )

        val result = adapter.fetchGarage()

        assertTrue(result is DomainResult.Error)
        result as DomainResult.Error
        assertTrue(result.error is TransportFailure)
    }

    @Test
    fun `fetchGarage rethrows unknown failures`() {
        val engine =
            MockEngine {
                throw IllegalStateException("Unexpected failure")
            }

        val adapter =
            SimulatorGarageKtorAdapter(
                simulatorBaseUrl = "http://simulator.local",
                client = mockHttpClient(engine),
            )

        assertThrows(IllegalStateException::class.java) {
            adapter.fetchGarage()
        }
    }

    private fun mockHttpClient(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            install(ContentNegotiation) {
                jackson()
            }
            expectSuccess = false
        }
}
