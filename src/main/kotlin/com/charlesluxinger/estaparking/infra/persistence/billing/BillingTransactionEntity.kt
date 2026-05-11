package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.billing.BillingTransaction
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "billing_transactions")
class BillingTransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "billing_id", nullable = false, unique = true)
    val billingId: String = "",
    @Column(name = "license_plate", nullable = false)
    val licensePlate: String = "",
    @Column(name = "sector", nullable = false)
    val sector: String = "",
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "exit_time", nullable = false)
    val exitTime: LocalDateTime = LocalDateTime.now(),
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): BillingTransaction =
        BillingTransaction(
            id = billingId,
            licensePlate = licensePlate,
            sector = sector,
            amount = amount,
            exitTime = exitTime,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(domain: BillingTransaction): BillingTransactionEntity =
            BillingTransactionEntity(
                billingId = domain.id,
                licensePlate = domain.licensePlate,
                sector = domain.sector,
                amount = domain.amount,
                exitTime = domain.exitTime,
                createdAt = domain.createdAt,
            )
    }
}
