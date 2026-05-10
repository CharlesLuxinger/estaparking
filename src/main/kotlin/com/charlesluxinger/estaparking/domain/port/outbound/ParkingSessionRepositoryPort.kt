package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.parking.Parking

interface ParkingSessionRepositoryPort {
    fun findById(parkingId: String): Parking?

    fun save(parking: Parking): Parking
}
