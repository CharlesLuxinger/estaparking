package com.charlesluxinger.estaparking.domain.vehicle

data class Vehicle(
    val plate: String,
) {
    init {
        require(plate.isNotBlank()) { "Plate must not be blank" }
        require(plate == plate.trim()) { "Plate must not contain surrounding spaces" }
        require(plate == plate.uppercase()) { "Plate must be uppercase" }
        require(PLATE_PATTERN.matches(plate)) { "Plate must match format AAA9999" }
    }

    private companion object {
        private val PLATE_PATTERN = Regex("^[A-Z]{3}[0-9]{4}$")
    }
}
