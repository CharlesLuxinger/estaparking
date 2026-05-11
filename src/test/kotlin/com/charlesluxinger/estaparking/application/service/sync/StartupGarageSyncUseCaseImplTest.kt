package com.charlesluxinger.estaparking.application.service.sync

import com.charlesluxinger.estaparking.domain.garage.Garage
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageError
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.TransportFailure
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientPort
import com.charlesluxinger.estaparking.domain.port.outbound.SpotRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.dto.SimulatorGarageSnapshot
import com.charlesluxinger.estaparking.domain.result.DomainResult.Error
import com.charlesluxinger.estaparking.domain.result.DomainResult.Success
import com.charlesluxinger.estaparking.domain.spot.Coordinates
import com.charlesluxinger.estaparking.domain.spot.Spot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StartupGarageSyncUseCaseImplTest {
    @Test
    fun `sync persists simulator garages and spots through outbound repositories`() {
        val simulatorGarageClientPort = mockk<SimulatorGarageClientPort>()
        val billingRepositoryPort = mockk<BillingRepositoryPort>()
        val spotRepositoryPort = mockk<SpotRepositoryPort>()

        val garages = listOf(Garage(sector = "A", basePrice = BigDecimal("12.50"), maxCapacity = 80))
        val spots =
            listOf(
                Spot(
                    id = 1L,
                    sector = "A",
                    coordinates =
                        Coordinates(
                            latitude = BigDecimal("-23.5616840"),
                            longitude = BigDecimal("-46.6559810"),
                        ),
                ),
            )

        every { simulatorGarageClientPort.fetchGarage() } returns
            Success(SimulatorGarageSnapshot(garages = garages, spots = spots))
        every { billingRepositoryPort.saveAll(garages) } returns garages
        every { spotRepositoryPort.saveAll(spots) } returns spots

        val useCase =
            StartupGarageSyncUseCaseImpl(
                simulatorGarageClientPort = simulatorGarageClientPort,
                billingRepositoryPort = billingRepositoryPort,
                spotRepositoryPort = spotRepositoryPort,
            )

        val result = useCase.sync()

        assertTrue(result is Success)
        result as Success
        assertEquals(1, result.value.garagesSynced)
        assertEquals(1, result.value.spotsSynced)

        verify(exactly = 1) { simulatorGarageClientPort.fetchGarage() }
        verify(exactly = 1) { billingRepositoryPort.saveAll(garages) }
        verify(exactly = 1) { spotRepositoryPort.saveAll(spots) }
    }

    @Test
    fun `returns SimulatorFetchFailed when fetch fails`() {
        val simulatorGarageClientPort = mockk<SimulatorGarageClientPort>()
        val billingRepositoryPort = mockk<BillingRepositoryPort>()
        val spotRepositoryPort = mockk<SpotRepositoryPort>()

        val fetchError = TransportFailure("API Error")
        every { simulatorGarageClientPort.fetchGarage() } returns Error(fetchError)

        val useCase =
            StartupGarageSyncUseCaseImpl(
                simulatorGarageClientPort = simulatorGarageClientPort,
                billingRepositoryPort = billingRepositoryPort,
                spotRepositoryPort = spotRepositoryPort,
            )

        val result = useCase.sync()

        assertTrue(result is Error)
        result as Error
        assertTrue(result.error is SyncGarageError.SimulatorFetchFailed)
        assertEquals(fetchError, (result.error as SyncGarageError.SimulatorFetchFailed).cause)
    }

    @Test
    fun `returns PersistenceFailure when persistence fails`() {
        val simulatorGarageClientPort = mockk<SimulatorGarageClientPort>()
        val billingRepositoryPort = mockk<BillingRepositoryPort>()
        val spotRepositoryPort = mockk<SpotRepositoryPort>()

        val garages = listOf(Garage(sector = "A", basePrice = BigDecimal("12.50"), maxCapacity = 80))
        val spots = emptyList<Spot>()

        every { simulatorGarageClientPort.fetchGarage() } returns
            Success(SimulatorGarageSnapshot(garages = garages, spots = spots))
        every { billingRepositoryPort.saveAll(garages) } throws RuntimeException("DB Error")

        val useCase =
            StartupGarageSyncUseCaseImpl(
                simulatorGarageClientPort = simulatorGarageClientPort,
                billingRepositoryPort = billingRepositoryPort,
                spotRepositoryPort = spotRepositoryPort,
            )

        val result = useCase.sync()

        assertTrue(result is Error)
        result as Error
        assertTrue(result.error is SyncGarageError.PersistenceFailure)
        assertEquals("DB Error", (result.error as SyncGarageError.PersistenceFailure).message)
    }
}
