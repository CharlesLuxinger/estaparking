package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.port.outbound.dto.SimulatorGarageSnapshot

interface SimulatorGarageClientPort {
    fun fetchGarage(): DomainResult<SimulatorGarageSnapshot, SimulatorGarageClientError>
}
