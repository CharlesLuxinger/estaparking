package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.port.outbound.dto.SimulatorGarageSnapshot
import com.charlesluxinger.estaparking.domain.result.DomainResult

interface SimulatorGarageClientPort {
    fun fetchGarage(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError>
}
