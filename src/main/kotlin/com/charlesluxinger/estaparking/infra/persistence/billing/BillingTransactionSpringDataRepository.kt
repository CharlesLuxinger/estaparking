package com.charlesluxinger.estaparking.infra.persistence.billing

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BillingTransactionSpringDataRepository : JpaRepository<BillingTransactionEntity, Long> {
    fun findBySectorAndExitTimeBetween(
        sector: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): List<BillingTransactionEntity>
}
