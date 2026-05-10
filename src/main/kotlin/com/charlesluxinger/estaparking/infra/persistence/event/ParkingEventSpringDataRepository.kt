package com.charlesluxinger.estaparking.infra.persistence.event

import org.springframework.data.jpa.repository.JpaRepository

interface ParkingEventSpringDataRepository : JpaRepository<ParkingEventEntity, Long> {
    fun findByParkingIdOrderByIdAsc(parkingId: String): List<ParkingEventEntity>

    fun countByParkingId(parkingId: String): Long
}
