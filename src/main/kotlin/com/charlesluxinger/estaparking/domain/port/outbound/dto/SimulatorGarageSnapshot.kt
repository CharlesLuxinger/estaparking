package com.charlesluxinger.estaparking.domain.port.outbound.dto

import com.charlesluxinger.estaparking.domain.garage.Garage
import com.charlesluxinger.estaparking.domain.spot.Spot

data class SimulatorGarageSnapshot(
    val garages: List<Garage>,
    val spots: List<Spot>,
)
