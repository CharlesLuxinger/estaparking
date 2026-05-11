package com.charlesluxinger.estaparking.infra.persistence.billing

import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository

interface BillingTransactionSpringDataRepository : JpaRepository<BillingTransactionEntity, Long> {
    fun findBySectorAndExitTimeBetween(
        sector: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): List<BillingTransactionEntity>
}
