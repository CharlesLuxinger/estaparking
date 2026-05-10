package com.charlesluxinger.estaparking.infra.client.simulator.garage.dto

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.error.flatMap
import com.charlesluxinger.estaparking.domain.error.map
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import com.charlesluxinger.estaparking.domain.port.outbound.dto.SimulatorGarageSnapshot

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
