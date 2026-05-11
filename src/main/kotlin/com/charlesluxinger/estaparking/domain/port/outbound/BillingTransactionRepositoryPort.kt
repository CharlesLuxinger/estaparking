package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.billing.BillingTransaction
import java.time.LocalDateTime

interface BillingTransactionRepositoryPort {
    fun save(billingTransaction: BillingTransaction): BillingTransaction

    fun findBySectorAndExitTimeBetween(
        sector: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): List<BillingTransaction>
}
