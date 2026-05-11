package com.charlesluxinger.estaparking.infra.persistence

import com.charlesluxinger.estaparking.config.ContainersConfig
import com.charlesluxinger.estaparking.domain.billing.BillingRecord
import com.charlesluxinger.estaparking.domain.billing.PricingSnapshot
import com.charlesluxinger.estaparking.domain.event.EventType
import com.charlesluxinger.estaparking.domain.event.StoredParkingEvent
import com.charlesluxinger.estaparking.domain.parking.Parking
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import com.charlesluxinger.estaparking.infra.persistence.billing.BillingRecordJpaAdapter
import com.charlesluxinger.estaparking.infra.persistence.billing.BillingRecordSpringDataRepository
import com.charlesluxinger.estaparking.infra.persistence.billing.PricingSnapshotJpaAdapter
import com.charlesluxinger.estaparking.infra.persistence.billing.PricingSnapshotSpringDataRepository
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
import java.time.Instant

@SpringBootTest
@Import(ContainersConfig::class)
@ActiveProfiles("test")
class PersistenceRepositoryIntegrationTest {
    @Autowired
    private lateinit var parkingSessionJpaAdapter: ParkingSessionJpaAdapter

    @Autowired
    private lateinit var parkingEventJpaAdapter: ParkingEventJpaAdapter

    @Autowired
    private lateinit var pricingSnapshotJpaAdapter: PricingSnapshotJpaAdapter

    @Autowired
    private lateinit var billingRecordJpaAdapter: BillingRecordJpaAdapter

    @Autowired
    private lateinit var parkingSessionSpringDataRepository: ParkingSessionSpringDataRepository

    @Autowired
    private lateinit var parkingEventSpringDataRepository: ParkingEventSpringDataRepository

    @Autowired
    private lateinit var pricingSnapshotSpringDataRepository: PricingSnapshotSpringDataRepository

    @Autowired
    private lateinit var billingRecordSpringDataRepository: BillingRecordSpringDataRepository

    @BeforeEach
    fun cleanTables() {
        billingRecordSpringDataRepository.deleteAll()
        pricingSnapshotSpringDataRepository.deleteAll()
        parkingEventSpringDataRepository.deleteAll()
        parkingSessionSpringDataRepository.deleteAll()
    }

    @Test
    fun `Repository persists pricing snapshot and billing record`() {
        val parkingId = "parking-billing-repository-it"
        val licensePlate = "ABC1234"

        val savedSnapshot =
            pricingSnapshotJpaAdapter.save(
                PricingSnapshot(
                    parkingId = parkingId,
                    licensePlate = licensePlate,
                    sector = "A",
                    basePrice = BigDecimal("10.00"),
                    occupancyPercentageAtEntry = BigDecimal("25.00"),
                    multiplierAtEntry = BigDecimal("1.10"),
                    entryAt = Instant.parse("2026-05-10T10:00:00Z"),
                ),
            )

        val savedBilling =
            billingRecordJpaAdapter.save(
                BillingRecord(
                    parkingId = parkingId,
                    licensePlate = licensePlate,
                    sector = "A",
                    amount = BigDecimal("11.00"),
                    parkedMinutes = 40,
                    billedAt = Instant.parse("2026-05-10T10:40:00Z"),
                ),
            )

        val loadedSnapshot = pricingSnapshotJpaAdapter.findLatestByParkingIdAndLicensePlate(parkingId, licensePlate)
        val loadedBilling = billingRecordJpaAdapter.findByParkingIdAndLicensePlate(parkingId, licensePlate)

        assertEquals(savedSnapshot, loadedSnapshot)
        assertEquals(listOf(savedBilling), loadedBilling)
    }

    @Test
    fun `Repository maps duplicate billing record constraint to DataIntegrityViolationException`() {
        val parkingId = "parking-duplicate-billing"
        val licensePlate = "ABC1234"

        billingRecordJpaAdapter.save(
            BillingRecord(
                parkingId = parkingId,
                licensePlate = licensePlate,
                sector = "A",
                amount = BigDecimal("10.00"),
                parkedMinutes = 60,
                billedAt = Instant.parse("2026-05-10T11:00:00Z"),
            ),
        )

        val exception =
            org.junit.jupiter.api.assertThrows<DataIntegrityViolationException> {
                billingRecordJpaAdapter.save(
                    BillingRecord(
                        parkingId = parkingId,
                        licensePlate = licensePlate,
                        sector = "A",
                        amount = BigDecimal("20.00"),
                        parkedMinutes = 120,
                        billedAt = Instant.parse("2026-05-10T12:00:00Z"),
                    ),
                )
            }

        assertTrue(
            exception.message?.contains("uk_billing_records_parking_plate", ignoreCase = true) == true ||
                exception.mostSpecificCause.message?.contains("uk_billing_records_parking_plate", ignoreCase = true) ==
                true,
        )
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
                            id = 1L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.5500000"), BigDecimal("-46.6300000")),
                            status = SpotStatus.ENTRY_REGISTERED,
                            occupiedBy = Vehicle("ABC1234"),
                        ),
                        Spot(
                            id = 2L,
                            sector = "A",
                            coordinates = Coordinates(BigDecimal("-23.5501000"), BigDecimal("-46.6301000")),
                            status = SpotStatus.PARKED,
                            occupiedBy = Vehicle("XYZ4321"),
                        ),
                    ),
            )

        parkingSessionJpaAdapter.save(expectedParking)

        val fixedTimestamp = java.time.LocalDateTime.of(2025, 1, 1, 12, 0)
        val savedEvents =
            listOf(
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = "ABC1234",
                    eventType = EventType.ENTRY,
                    timestamp = fixedTimestamp,
                ),
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = "ABC1234",
                    eventType = EventType.PARKED,
                    timestamp = fixedTimestamp.plusHours(1),
                ),
                StoredParkingEvent(
                    parkingId = parkingId,
                    licensePlate = "ABC1234",
                    eventType = EventType.EXIT,
                    timestamp = fixedTimestamp.plusHours(2),
                ),
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
                            id = 3L,
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
