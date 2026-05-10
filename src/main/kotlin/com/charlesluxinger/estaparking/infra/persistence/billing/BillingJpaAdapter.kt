package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.garage.Garage
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import org.springframework.stereotype.Repository

@Repository
class BillingJpaAdapter(
    private val repository: BillingSpringDataRepository,
) : BillingRepositoryPort {
    override fun findGarageBySector(sector: String): Garage? = repository.findBySector(sector)?.toDomain()

    override fun save(garage: Garage): Garage = repository.save(GarageEntity.fromDomain(garage)).toDomain()

    override fun saveAll(garages: List<Garage>): List<Garage> =
        repository.saveAll(garages.map(GarageEntity::fromDomain)).map(GarageEntity::toDomain)
}
