package com.charlesluxinger.estaparking.domain.port.outbound

sealed interface SimulatorGarageClientError {
    data class UnexpectedStatus(
        val statusCode: Int,
        val responseBody: String,
    ) : SimulatorGarageClientError

    data class PayloadMappingFailure(
        val message: String,
    ) : SimulatorGarageClientError

    data class TransportFailure(
        val message: String,
    ) : SimulatorGarageClientError
}
