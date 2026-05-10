package com.charlesluxinger.estaparking.infra.client.simulator.garage

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.PayloadMappingFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.TransportFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.UnexpectedStatus
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientPort
import com.charlesluxinger.estaparking.domain.port.outbound.dto.SimulatorGarageSnapshot
import com.charlesluxinger.estaparking.infra.client.simulator.garage.dto.SimulatorGarageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.io.IOException

@Repository
class SimulatorGarageKtorAdapter(
    @Value("\${simulator.base-url:http://localhost:3000}")
    private val simulatorBaseUrl: String,
    @Qualifier("simulatorGarageHttpClient")
    private val client: HttpClient,
) : SimulatorGarageClientPort {
    override fun fetchGarage(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError> =
        runBlocking {
            runCatching {
                val response = client.get("$simulatorBaseUrl/garage")

                if (!response.status.isSuccess()) {
                    return@runBlocking DomainResult.Error(
                        UnexpectedStatus(
                            statusCode = response.status.value,
                            responseBody = response.bodyAsText(),
                        ),
                    )
                }

                response.body<SimulatorGarageResponse>().toSnapshot()
            }.getOrElse { it.toDomainResultOrThrow() }
        }

    private fun Throwable.toDomainResultOrThrow(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError> =
        when (this) {
            is IOException,
            -> DomainResult.Error(TransportFailure(message ?: "I/O error while calling simulator /garage"))
            is IllegalArgumentException,
            is JsonConvertException,
            is NoTransformationFoundException,
            -> DomainResult.Error(PayloadMappingFailure(message ?: "Invalid simulator /garage payload"))
            else -> throw this
        }
}
