package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.billing.PricingSnapshot
import com.charlesluxinger.estaparking.domain.port.outbound.PricingSnapshotRepositoryPort
import org.springframework.stereotype.Repository

@Repository
class PricingSnapshotJpaAdapter(
    private val repository: PricingSnapshotSpringDataRepository,
) : PricingSnapshotRepositoryPort {
    override fun save(snapshot: PricingSnapshot): PricingSnapshot =
        repository.save(PricingSnapshotEntity.fromDomain(snapshot)).toDomain()

    override fun findLatestByParkingIdAndLicensePlate(
        parkingId: String,
        licensePlate: String,
    ): PricingSnapshot? =
        repository
            .findTopByParkingIdAndLicensePlateOrderByEntryAtDesc(
                parkingId = parkingId,
                licensePlate = licensePlate,
            )?.toDomain()
}
