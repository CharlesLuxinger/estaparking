package com.charlesluxinger.estaparking.domain.billing

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import java.math.BigDecimal
import java.time.LocalDateTime

data class BillingTransaction(
    val id: String,
    val vehicle: Vehicle,
    val sector: String,
    val amount: BigDecimal,
    val exitTime: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(id.isNotBlank()) { "Id must not be blank" }
        require(sector.isNotBlank()) { "Sector must not be blank" }
        require(amount >= BigDecimal.ZERO) { "Amount must be non-negative" }
        require(exitTime <= LocalDateTime.now()) { "Exit time cannot be in the future" }
    }
}
