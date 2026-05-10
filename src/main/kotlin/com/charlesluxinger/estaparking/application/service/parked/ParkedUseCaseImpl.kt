package com.charlesluxinger.estaparking.application.service.parked

import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.port.inbound.parked.ParkedCommandPort
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

class ParkedUseCaseImpl : ParkedCommandPort {
    override fun execute(
        parking: Parking,
        vehicle: Vehicle,
    ): DomainResult<Parking, ParkingDomainError> = parking.apply(EventType.PARKED, vehicle)
}
