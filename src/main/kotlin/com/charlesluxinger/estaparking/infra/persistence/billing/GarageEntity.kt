package com.charlesluxinger.estaparking.infra.persistence.billing

import com.charlesluxinger.estaparking.domain.garage.Garage
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "garages")
class GarageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "sector", nullable = false, unique = true)
    val sector: String = "",
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    val basePrice: BigDecimal = BigDecimal.ZERO,
    @Column(name = "max_capacity", nullable = false)
    val maxCapacity: Int = 0,
) {
    fun toDomain(): Garage =
        Garage(
            sector = sector,
            basePrice = basePrice,
            maxCapacity = maxCapacity,
        )

    companion object {
        fun fromDomain(domain: Garage): GarageEntity =
            GarageEntity(
                sector = domain.sector,
                basePrice = domain.basePrice,
                maxCapacity = domain.maxCapacity,
            )
    }
}
