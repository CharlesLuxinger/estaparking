package com.charlesluxinger.estaparking.infra.client.simulator.garage.dto

import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import com.charlesluxinger.estaparking.domain.port.outbound.dto.SimulatorGarageSnapshot
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.result.flatMap
import com.charlesluxinger.estaparking.domain.result.map

data class SimulatorGarageResponse(
    val garage: List<SimulatorGarageItem>,
    val spots: List<SimulatorSpotItem>,
) {
    fun toSnapshot(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError> =
        garage
            .toDomainGarages()
            .flatMap { garages ->
                spots
                    .toDomainSpots()
                    .map { spots -> SimulatorGarageSnapshot(garages, spots) }
            }
}
