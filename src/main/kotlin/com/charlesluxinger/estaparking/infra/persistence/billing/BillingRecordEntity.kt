package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.billing.BillingRecord
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "billing_records")
class BillingRecordEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "parking_id", nullable = false)
    val parkingId: String = "",
    @Column(name = "license_plate", nullable = false)
    val licensePlate: String = "",
    @Column(name = "sector", nullable = false)
    val sector: String = "",
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "parked_minutes", nullable = false)
    val parkedMinutes: Long = 0,
    @Column(name = "billed_at", nullable = false)
    val billedAt: Instant = Instant.EPOCH,
) {
    fun toDomain(): BillingRecord =
        BillingRecord(
            parkingId = parkingId,
            vehicle = Vehicle(licensePlate),
            sector = sector,
            amount = amount,
            parkedMinutes = parkedMinutes,
            billedAt = billedAt,
        )

    companion object {
        fun fromDomain(domain: BillingRecord): BillingRecordEntity =
            BillingRecordEntity(
                parkingId = domain.parkingId,
                licensePlate = domain.vehicle.plate,
                sector = domain.sector,
                amount = domain.amount,
                parkedMinutes = domain.parkedMinutes,
                billedAt = domain.billedAt,
            )
    }
}
