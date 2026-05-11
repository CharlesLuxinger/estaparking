package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.billing.BillingRecord
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRecordRepositoryPort
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import org.springframework.stereotype.Repository

@Repository
class BillingRecordJpaAdapter(
    private val repository: BillingRecordSpringDataRepository,
) : BillingRecordRepositoryPort {
    override fun save(record: BillingRecord): BillingRecord =
        repository.save(BillingRecordEntity.fromDomain(record)).toDomain()

    override fun findByParkingIdAndLicensePlate(
        parkingId: String,
        vehicle: Vehicle,
    ): List<BillingRecord> =
        repository
            .findByParkingIdAndLicensePlateOrderByIdAsc(
                parkingId = parkingId,
                licensePlate = vehicle.plate,
            ).map(BillingRecordEntity::toDomain)
}
