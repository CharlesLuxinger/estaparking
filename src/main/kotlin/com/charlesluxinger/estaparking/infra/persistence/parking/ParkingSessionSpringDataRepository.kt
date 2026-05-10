package com.charlesluxinger.estaparking.infra.persistence.parking

import org.springframework.data.jpa.repository.JpaRepository

interface ParkingSessionSpringDataRepository : JpaRepository<ParkingSessionEntity, Long> {
    fun findByParkingId(parkingId: String): ParkingSessionEntity?
}
