package com.charlesluxinger.estaparking.domain.port.inbound.entry.model

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

data class EntryCommand(
    val vehicle: Vehicle,
)
