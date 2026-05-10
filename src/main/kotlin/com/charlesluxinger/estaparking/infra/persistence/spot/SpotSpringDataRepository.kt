package com.charlesluxinger.estaparking.infra.persistence.spot

import org.springframework.data.jpa.repository.JpaRepository

interface SpotSpringDataRepository : JpaRepository<SpotEntity, Long> {
    fun findBySpotId(spotId: String): SpotEntity?
}
