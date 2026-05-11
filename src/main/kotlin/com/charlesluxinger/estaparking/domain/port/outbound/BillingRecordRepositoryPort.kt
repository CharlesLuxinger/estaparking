package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.billing.BillingRecord
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle

interface BillingRecordRepositoryPort {
    fun save(record: BillingRecord): BillingRecord

    fun findByParkingIdAndLicensePlate(
        parkingId: String,
        vehicle: Vehicle,
    ): List<BillingRecord>
}
