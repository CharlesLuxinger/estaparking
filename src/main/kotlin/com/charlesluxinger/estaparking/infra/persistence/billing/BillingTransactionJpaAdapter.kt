package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.billing.BillingTransaction
import com.charlesluxinger.estaparking.domain.port.outbound.BillingTransactionRepositoryPort
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class BillingTransactionJpaAdapter(
    private val repository: BillingTransactionSpringDataRepository,
) : BillingTransactionRepositoryPort {
    override fun save(billingTransaction: BillingTransaction): BillingTransaction =
        repository.save(BillingTransactionEntity.fromDomain(billingTransaction)).toDomain()

    override fun findBySectorAndExitTimeBetween(
        sector: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): List<BillingTransaction> =
        repository.findBySectorAndExitTimeBetween(sector, startTime, endTime).map { it.toDomain() }
}
