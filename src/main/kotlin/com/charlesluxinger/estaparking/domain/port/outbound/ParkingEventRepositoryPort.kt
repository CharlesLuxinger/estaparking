package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.event.ParkingEvent

interface ParkingEventRepositoryPort {
    fun save(event: ParkingEvent): ParkingEvent

    fun findByParkingId(parkingId: String): List<ParkingEvent>
}
