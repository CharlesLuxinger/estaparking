package com.charlesluxinger.estaparking.domain.common

import java.math.BigDecimal

data class Coordinates(
    val latitude: BigDecimal,
    val longitude: BigDecimal,
) {
    init {
        val minLatitude = BigDecimal("-90")
        val maxLatitude = BigDecimal("90")
        val minLongitude = BigDecimal("-180")
        val maxLongitude = BigDecimal("180")

        require(latitude in minLatitude..maxLatitude) { "Latitude must be between -90 and 90" }
        require(longitude in minLongitude..maxLongitude) { "Longitude must be between -180 and 180" }
    }
}
