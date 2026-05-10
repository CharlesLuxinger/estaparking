package com.charlesluxinger.estaparking.domain.pricing

import java.math.BigDecimal
import java.math.RoundingMode

object PricingPolicy {
    private const val FREE_MINUTES_THRESHOLD = 30
    private val ONE_HUNDRED = BigDecimal("100")
    private val TWENTY_FIVE = BigDecimal("25")
    private val FIFTY = BigDecimal("50")
    private val SEVENTY_FIVE = BigDecimal("75")

    fun calculateAmount(
        basePrice: BigDecimal,
        parkedMinutes: Long,
        occupancyPercentage: BigDecimal,
    ): BigDecimal {
        require(basePrice > BigDecimal.ZERO) { "Base price must be greater than zero" }
        require(parkedMinutes >= 0) { "Parked minutes must be non-negative" }

        if (parkedMinutes <= FREE_MINUTES_THRESHOLD) {
            return BigDecimal.ZERO.setScale(2)
        }

        val billedHours = parkedMinutes.toBigDecimal().divide(BigDecimal("60"), 0, RoundingMode.CEILING)
        val multiplierFactor = occupancyMultiplier(occupancyPercentage)

        return basePrice
            .multiply(billedHours)
            .multiply(multiplierFactor)
            .setScale(2, RoundingMode.HALF_UP)
    }

    fun occupancyMultiplier(occupancyPercentage: BigDecimal): BigDecimal {
        require(occupancyPercentage >= BigDecimal.ZERO) { "Occupancy percentage must be non-negative" }
        require(occupancyPercentage <= ONE_HUNDRED) { "Occupancy percentage must be at most 100" }

        return when {
            occupancyPercentage <= TWENTY_FIVE -> BigDecimal("0.90")
            occupancyPercentage <= FIFTY -> BigDecimal("1.00")
            occupancyPercentage <= SEVENTY_FIVE -> BigDecimal("1.10")
            else -> BigDecimal("1.25")
        }
    }
}
