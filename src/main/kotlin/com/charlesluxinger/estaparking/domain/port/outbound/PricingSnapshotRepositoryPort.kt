package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.billing.PricingSnapshot
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

interface PricingSnapshotRepositoryPort {
    fun save(snapshot: PricingSnapshot): PricingSnapshot

    fun findLatestByParkingIdAndLicensePlate(
        parkingId: String,
        vehicle: Vehicle,
    ): PricingSnapshot?
}
