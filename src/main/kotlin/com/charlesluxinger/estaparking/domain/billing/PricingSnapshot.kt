package com.charlesluxinger.estaparking.domain.billing

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.time.Instant

data class PricingSnapshot(
    val parkingId: String,
    val vehicle: Vehicle,
    val sector: String,
    val basePrice: BigDecimal,
    val occupancyPercentageAtEntry: BigDecimal,
    val multiplierAtEntry: BigDecimal,
    val entryAt: Instant,
) {
    init {
        require(parkingId.isNotBlank()) { "Parking id must not be blank" }
        require(sector.isNotBlank()) { "Sector must not be blank" }
        require(basePrice > BigDecimal.ZERO) { "Base price must be greater than zero" }
        require(occupancyPercentageAtEntry >= BigDecimal.ZERO) { "Occupancy percentage must be non-negative" }
        require(multiplierAtEntry > BigDecimal.ZERO) { "Multiplier must be greater than zero" }
    }
}
