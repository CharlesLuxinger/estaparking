package com.charlesluxinger.estaparking.application.service.entry

import com.charlesluxinger.estaparking.domain.common.Coordinates
import com.charlesluxinger.estaparking.domain.error.ParkingDomainError
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.port.inbound.entry.EntryCommandPort
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

class EntryUseCaseImpl : EntryCommandPort {
    override fun execute(
        parking: Parking,
        vehicle: Vehicle,
        coordinates: Coordinates,
    ): DomainResult<Parking, ParkingDomainError> =
        if (parking.isFull()) {
            DomainResult.Error(ParkingDomainError.FullOccupancyEntryDenied)
        } else {
            parking.apply(EventType.ENTRY, vehicle, coordinates)
        }
}
