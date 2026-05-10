package com.charlesluxinger.estaparking.infra.persistence.spot

import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "spots")
class SpotEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "spot_id", nullable = false, unique = true)
    val spotId: String = "",
    @Column(name = "sector", nullable = false)
    val sector: String = "",
    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    val latitude: BigDecimal = BigDecimal.ZERO,
    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    val longitude: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: SpotStatus = SpotStatus.AVAILABLE,
    @Column(name = "occupied_by_plate")
    val occupiedByPlate: String? = null,
) {
    fun toDomain(): Spot =
        Spot(
            id = spotId,
            sector = sector,
            coordinates =
                Coordinates(
                    latitude = latitude,
                    longitude = longitude,
                ),
            status = status,
            occupiedBy = occupiedByPlate?.let(::Vehicle),
        )

    companion object {
        fun fromDomain(domain: Spot): SpotEntity =
            SpotEntity(
                spotId = domain.id,
                sector = domain.sector,
                latitude = domain.coordinates.latitude,
                longitude = domain.coordinates.longitude,
                status = domain.status,
                occupiedByPlate = domain.occupiedBy?.plate,
            )
    }
}
