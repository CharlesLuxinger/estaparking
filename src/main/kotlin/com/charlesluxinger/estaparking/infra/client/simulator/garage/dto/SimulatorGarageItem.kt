package com.charlesluxinger.estaparking.infra.client.simulator.garage.dto

import com.charlesluxinger.estaparking.domain.garage.Garage
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.result.DomainResult.Success
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class SimulatorGarageItem(
    val sector: String,
    @JsonProperty("base_price")
    val basePrice: BigDecimal,
    @JsonProperty("max_capacity")
    val maxCapacity: Int,
)

fun List<SimulatorGarageItem>.toDomainGarages(): DomainResult<List<Garage>, SimulatorGarageClientError> =
    this
        .map { Garage(it.sector, it.basePrice, it.maxCapacity) }
        .let { Success(it) }
