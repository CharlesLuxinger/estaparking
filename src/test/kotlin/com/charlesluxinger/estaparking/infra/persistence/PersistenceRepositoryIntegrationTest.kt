package com.charlesluxinger.estaparking.infra.persistence

import com.charlesluxinger.estaparking.config.ContainersConfig
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import com.charlesluxinger.estaparking.infra.persistence.event.ParkingEventJpaAdapter
import com.charlesluxinger.estaparking.infra.persistence.event.ParkingEventSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.parking.ParkingSessionJpaAdapter
import com.charlesluxinger.estaparking.infra.persistence.parking.ParkingSessionSpringDataRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@SpringBootTest
@Import(ContainersConfig::class)
@ActiveProfiles("test")
class PersistenceRepositoryIntegrationTest {
    @Autowired
    private lateinit var parkingSessionJpaAdapter: ParkingSessionJpaAdapter

    @Autowired
    private lateinit var parkingEventJpaAdapter: ParkingEventJpaAdapter

    @Autowired
    private lateinit var parkingSessionSpringDataRepository: ParkingSessionSpringDataRepository

    @Autowired
    private lateinit var parkingEventSpringDataRepository: ParkingEventSpringDataRepository

    @BeforeEach
    fun cleanTables() {
        parkingEventSpringDataRepository.deleteAll()
        parkingSessionSpringDataRepository.deleteAll()
    }

    @Test
    fun `Repository persists and retrieves parking session with ordered events`() {
        val parkingId = "parking-repository-it"
        val expectedParking =
            Parking(
                id = parkingId,
                name = "Repository IT Parking",
                spots =
                    listOf(
                        Spot(
                            id = "A-01",
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.5500000"), BigDecimal("-46.6300000")),
                            status = SpotStatus.ENTRY_REGISTERED,
                            occupiedBy = Vehicle("ABC1234"),
                        ),
                        Spot(
                            id = "A-02",
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.5501000"), BigDecimal("-46.6301000")),
                            status = SpotStatus.PARKED,
                            occupiedBy = Vehicle("XYZ4321"),
                        ),
                    ),
            )

        parkingSessionJpaAdapter.save(expectedParking)

        val savedEvents =
            listOf(
                StoredParkingEvent(parkingId = parkingId, licensePlate = "ABC1234", eventType = EventType.ENTRY),
                StoredParkingEvent(parkingId = parkingId, licensePlate = "ABC1234", eventType = EventType.PARKED),
                StoredParkingEvent(parkingId = parkingId, licensePlate = "ABC1234", eventType = EventType.EXIT),
            ).map(parkingEventJpaAdapter::save)

        val reloadedParking = parkingSessionJpaAdapter.findById(parkingId)
        val reloadedEvents = parkingEventJpaAdapter.findByParkingId(parkingId)

        assertEquals(expectedParking, reloadedParking)
        assertEquals(savedEvents, reloadedEvents)
        assertTrue(reloadedEvents.zip(savedEvents).all { (reloaded, saved) -> reloaded == saved })
    }

    @Test
    fun `Repository maps duplicate parking id constraint to DataIntegrityViolationException`() {
        val parkingId = "parking-duplicate"
        val original =
            Parking(
                id = parkingId,
                name = "Original",
                spots =
                    listOf(
                        Spot(
                            id = "B-01",
                            sector = "B",
                            coordinates = Coordinates(BigDecimal("-23.5510000"), BigDecimal("-46.6310000")),
                        ),
                    ),
            )
        val duplicate = original.copy(name = "Duplicate")

        parkingSessionJpaAdapter.save(original)

        val exception =
            org.junit.jupiter.api.assertThrows<DataIntegrityViolationException> {
                parkingSessionJpaAdapter.save(duplicate)
            }

        assertTrue(
            exception.message?.contains("parking_id", ignoreCase = true) == true ||
                exception.mostSpecificCause.message?.contains("parking_id", ignoreCase = true) == true,
        )
    }
}
