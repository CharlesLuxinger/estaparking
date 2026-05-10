package com.charlesluxinger.estaparking.domain.billing

import java.math.BigDecimal
import java.time.Instant

data class BillingRecord(
    val parkingId: String,
    val licensePlate: String,
    val sector: String,
    val amount: BigDecimal,
    val parkedMinutes: Long,
    val billedAt: Instant,
) {
    init {
        require(parkingId.isNotBlank()) { "Parking id must not be blank" }
        require(licensePlate.isNotBlank()) { "License plate must not be blank" }
        require(sector.isNotBlank()) { "Sector must not be blank" }
        require(amount >= BigDecimal.ZERO) { "Amount must be non-negative" }
        require(parkedMinutes >= 0) { "Parked minutes must be non-negative" }
    }
}
