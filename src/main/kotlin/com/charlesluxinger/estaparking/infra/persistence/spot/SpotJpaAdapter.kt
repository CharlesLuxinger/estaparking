package com.charlesluxinger.estaparking.infra.persistence.spot

import com.charlesluxinger.estaparking.domain.port.outbound.SpotRepositoryPort
import com.charlesluxinger.estaparking.domain.spot.Spot
import org.springframework.stereotype.Repository

@Repository
class SpotJpaAdapter(
    private val repository: SpotSpringDataRepository,
) : SpotRepositoryPort {
    override fun findById(spotId: String): Spot? = repository.findBySpotId(spotId)?.toDomain()

    override fun save(spot: Spot): Spot = repository.save(SpotEntity.fromDomain(spot)).toDomain()

    override fun saveAll(spots: List<Spot>): List<Spot> =
        repository.saveAll(spots.map(SpotEntity::fromDomain)).map(SpotEntity::toDomain)
}
