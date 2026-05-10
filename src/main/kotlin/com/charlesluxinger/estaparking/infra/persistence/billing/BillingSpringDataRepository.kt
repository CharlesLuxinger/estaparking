package com.charlesluxinger.estaparking.infra.persistence.billing

import org.springframework.data.jpa.repository.JpaRepository

interface BillingSpringDataRepository : JpaRepository<GarageEntity, Long> {
    fun findBySector(sector: String): GarageEntity?
}
