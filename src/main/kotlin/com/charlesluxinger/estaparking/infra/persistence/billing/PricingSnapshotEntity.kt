package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.billing.PricingSnapshot
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "pricing_snapshots")
class PricingSnapshotEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "parking_id", nullable = false)
    val parkingId: String = "",
    @Column(name = "license_plate", nullable = false)
    val licensePlate: String = "",
    @Column(name = "sector", nullable = false)
    val sector: String = "",
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    val basePrice: BigDecimal = BigDecimal.ZERO,
    @Column(name = "occupancy_percentage_at_entry", nullable = false, precision = 5, scale = 2)
    val occupancyPercentageAtEntry: BigDecimal = BigDecimal.ZERO,
    @Column(name = "multiplier_at_entry", nullable = false, precision = 5, scale = 2)
    val multiplierAtEntry: BigDecimal = BigDecimal.ONE,
    @Column(name = "entry_at", nullable = false)
    val entryAt: Instant = Instant.EPOCH,
) {
    fun toDomain(): PricingSnapshot =
        PricingSnapshot(
            parkingId = parkingId,
            licensePlate = licensePlate,
            sector = sector,
            basePrice = basePrice,
            occupancyPercentageAtEntry = occupancyPercentageAtEntry,
            multiplierAtEntry = multiplierAtEntry,
            entryAt = entryAt,
        )

    companion object {
        fun fromDomain(domain: PricingSnapshot): PricingSnapshotEntity =
            PricingSnapshotEntity(
                parkingId = domain.parkingId,
                licensePlate = domain.licensePlate,
                sector = domain.sector,
                basePrice = domain.basePrice,
                occupancyPercentageAtEntry = domain.occupancyPercentageAtEntry,
                multiplierAtEntry = domain.multiplierAtEntry,
                entryAt = domain.entryAt,
            )
    }
}
