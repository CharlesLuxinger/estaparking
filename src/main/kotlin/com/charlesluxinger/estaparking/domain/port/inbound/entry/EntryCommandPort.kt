package com.charlesluxinger.estaparking.domain.port.inbound.entry

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

interface EntryCommandPort {
    fun execute(
        parking: Parking,
        vehicle: Vehicle,
    ): DomainResult<Parking, ParkingDomainError>
}
