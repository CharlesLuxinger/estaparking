package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.spot.Spot

interface SpotRepositoryPort {
    fun findById(spotId: Long): Spot?

    fun save(spot: Spot): Spot

    fun saveAll(spots: List<Spot>): List<Spot>
}
