package com.charlesluxinger.estaparking.domain.garage

import java.math.BigDecimal

data class Garage(
    val sector: String,
    val basePrice: BigDecimal,
    val maxCapacity: Int,
) {
    init {
        require(sector.isNotBlank()) { "Garage sector must not be blank" }
        require(basePrice > BigDecimal.ZERO) { "Garage base price must be greater than zero" }
        require(maxCapacity > 0) { "Garage max capacity must be greater than zero" }
    }
}
