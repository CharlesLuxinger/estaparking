package com.charlesluxinger.estaparking.infra.persistence.billing

import org.springframework.data.jpa.repository.JpaRepository

interface PricingSnapshotSpringDataRepository : JpaRepository<PricingSnapshotEntity, Long> {
    fun findTopByParkingIdAndLicensePlateOrderByEntryAtDesc(
        parkingId: String,
        licensePlate: String,
    ): PricingSnapshotEntity?
}
