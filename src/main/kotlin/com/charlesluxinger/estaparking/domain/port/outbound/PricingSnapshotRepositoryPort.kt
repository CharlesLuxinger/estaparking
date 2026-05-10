package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.billing.PricingSnapshot

interface PricingSnapshotRepositoryPort {
    fun save(snapshot: PricingSnapshot): PricingSnapshot

    fun findLatestByParkingIdAndLicensePlate(
        parkingId: String,
        licensePlate: String,
    ): PricingSnapshot?
}
