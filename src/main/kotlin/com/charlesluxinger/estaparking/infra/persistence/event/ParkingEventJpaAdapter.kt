package com.charlesluxinger.estaparking.infra.persistence.event

import com.charlesluxinger.estaparking.domain.event.ParkingEvent
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import org.springframework.stereotype.Repository

@Repository
class ParkingEventJpaAdapter(
    private val repository: ParkingEventSpringDataRepository,
) : ParkingEventRepositoryPort {
    override fun save(event: ParkingEvent): ParkingEvent {
        val eventId = nextEventId(event.parkingId)
        return repository.save(ParkingEventEntity.fromDomain(event, eventId)).toDomain()
    }

    override fun findByParkingId(parkingId: String): List<ParkingEvent> {
        val events = repository.findByParkingIdOrderByIdAsc(parkingId)
        return events.map(ParkingEventEntity::toDomain)
    }

    private fun nextEventId(parkingId: String): String {
        val count = repository.countByParkingId(parkingId)
        return "$parkingId-${count + 1}"
    }
}
