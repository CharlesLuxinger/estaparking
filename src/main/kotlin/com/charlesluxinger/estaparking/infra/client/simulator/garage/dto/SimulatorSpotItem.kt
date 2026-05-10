package com.charlesluxinger.estaparking.infra.client.simulator.garage.dto

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import java.math.BigDecimal

data class SimulatorSpotItem(
    val id: Long,
    val sector: String,
    val lat: BigDecimal,
    val lng: BigDecimal,
)

fun List<SimulatorSpotItem>.toDomainSpots(): DomainResult<List<Spot>, SimulatorGarageClientError> =
    this
        .map { Spot(it.id, it.sector, Coordinates(it.lat, it.lng)) }
        .let { DomainResult.Success(it) }
