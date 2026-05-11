package com.charlesluxinger.estaparking.infra.client.simulator.garage

import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.PayloadMappingFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.TransportFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.UnexpectedStatus
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientPort
import com.charlesluxinger.estaparking.domain.port.outbound.dto.SimulatorGarageSnapshot
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.result.DomainResult.Error
import com.charlesluxinger.estaparking.infra.client.simulator.garage.dto.SimulatorGarageResponse
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Repository
class SimulatorGarageRestTemplateAdapter(
    @Value("\${simulator.base-url:http://localhost:3000}")
    private val simulatorBaseUrl: String,
    @Qualifier("simulatorGarageRestTemplate")
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
) : SimulatorGarageClientPort {
    override fun fetchGarage(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError> =
        runCatching {
            val response = restTemplate.getForEntity("$simulatorBaseUrl/garage", String::class.java)
            response.toDomain()
        }.getOrElse { it.toDomainResultOrThrow() }

    private fun ResponseEntity<String>.toDomain(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError> =
        when {
            !statusCode.is2xxSuccessful ->
                Error(
                    UnexpectedStatus(
                        statusCode = statusCode.value(),
                        responseBody = body.orEmpty(),
                    ),
                )

            body == null -> Error(PayloadMappingFailure("Empty simulator /garage payload"))

            else -> {
                val payload = objectMapper.readValue(body, SimulatorGarageResponse::class.java)
                payload.toSnapshot()
            }
        }

    private fun Throwable.toDomainResultOrThrow(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError> =
        when (this) {
            is JsonProcessingException,
            -> Error(PayloadMappingFailure(message ?: "Invalid simulator /garage payload"))

            is ResourceAccessException,
            is IOException,
            -> Error(TransportFailure(message ?: "I/O error while calling simulator /garage"))

            is IllegalArgumentException,
            -> Error(PayloadMappingFailure(message ?: "Invalid simulator /garage payload"))

            is RestClientResponseException,
            -> Error(UnexpectedStatus(statusCode.value(), responseBodyAsString))

            else -> throw this
        }
}
