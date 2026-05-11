package com.charlesluxinger.estaparking.infra.persistence.parking

import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "parking_sessions")
class ParkingSessionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "parking_id", nullable = false, unique = true)
    val parkingId: String = "",
    @Column(name = "name", nullable = false)
    val name: String = "",
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "parking_session_id", nullable = false)
    val spotSnapshots: MutableList<ParkingSpotSnapshotEntity> = ArrayList(),
) {
    fun toDomain(): Parking =
        Parking(
            id = parkingId,
            name = name,
            spots = spotSnapshots.map(ParkingSpotSnapshotEntity::toDomain),
        )

    companion object {
        fun fromDomain(domain: Parking): ParkingSessionEntity =
            ParkingSessionEntity(
                parkingId = domain.id,
                name = domain.name,
                spotSnapshots = domain.spots.map(ParkingSpotSnapshotEntity::fromDomain).toMutableList(),
            )
    }
}

@Entity
@Table(name = "parking_spot_snapshots")
class ParkingSpotSnapshotEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "spot_id", nullable = false)
    val spotId: Long = 0,
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
        fun fromDomain(domain: Spot): ParkingSpotSnapshotEntity =
            ParkingSpotSnapshotEntity(
                spotId = domain.id,
                sector = domain.sector,
                latitude = domain.coordinates.latitude,
                longitude = domain.coordinates.longitude,
                status = domain.status,
                occupiedByPlate = domain.occupiedBy?.plate,
            )
    }
}
