package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.billing.BillingRecord

interface BillingRecordRepositoryPort {
    fun save(record: BillingRecord): BillingRecord

    fun findByParkingIdAndLicensePlate(
        parkingId: String,
        licensePlate: String,
    ): List<BillingRecord>
}
