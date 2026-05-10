package com.charlesluxinger.estaparking.infra.persistence.billing

import org.springframework.data.jpa.repository.JpaRepository

interface BillingRecordSpringDataRepository : JpaRepository<BillingRecordEntity, Long> {
    fun findByParkingIdAndLicensePlateOrderByIdAsc(
        parkingId: String,
        licensePlate: String,
    ): List<BillingRecordEntity>
}
