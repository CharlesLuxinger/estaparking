package com.charlesluxinger.estaparking.infra.persistence.parking

import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import org.springframework.stereotype.Repository

@Repository
class ParkingSessionJpaAdapter(
    private val repository: ParkingSessionSpringDataRepository,
) : ParkingSessionRepositoryPort {
    override fun findById(parkingId: String): Parking? = repository.findByParkingId(parkingId)?.toDomain()

    override fun save(parking: Parking): Parking = repository.save(ParkingSessionEntity.fromDomain(parking)).toDomain()
}
