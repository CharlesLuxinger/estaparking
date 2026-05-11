package com.charlesluxinger.estaparking.infra.persistence.event

import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.ParkingEvent
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "parking_events")
class ParkingEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "event_id", nullable = false, unique = true)
    val eventId: String = "",
    @Column(name = "parking_id", nullable = false)
    val parkingId: String = "",
    @Column(name = "license_plate", nullable = false)
    val licensePlate: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    val eventType: EventType = EventType.ENTRY,
    @Column(name = "timestamp", nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): ParkingEvent =
        StoredParkingEvent(
            parkingId = parkingId,
            vehicle = Vehicle(licensePlate),
            eventType = eventType,
            timestamp = timestamp,
        )

    companion object {
        fun fromDomain(
            domain: ParkingEvent,
            eventId: String,
        ): ParkingEventEntity =
            ParkingEventEntity(
                eventId = eventId,
                parkingId = domain.parkingId,
                licensePlate = domain.vehicle.plate,
                eventType = domain.eventType,
                timestamp = domain.timestamp,
            )
    }
}
