package com.charlesluxinger.estaparking.infra.startup

import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageError
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageSummary
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError.TransportFailure
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class StartupGarageSyncConfigTest {
    @Test
    fun `startup runner executes use case once when sync succeeds`() {
        val useCase = mockk<SyncGarageCommandPort>()
        every { useCase.sync() } returns DomainResult.Success(SyncGarageSummary(garagesSynced = 2, spotsSynced = 5))

        val runner = StartupGarageSyncConfig().startupGarageSyncRunner(useCase)
        runner.run(mockk(relaxed = true))

        verify(exactly = 1) { useCase.sync() }
    }

    @Test
    fun `startup runner keeps boot flow non-fatal when sync returns error`() {
        val useCase = mockk<SyncGarageCommandPort>()
        every { useCase.sync() } returns
            DomainResult.Error(
                SyncGarageError.SimulatorFetchFailed(
                    TransportFailure("timeout"),
                ),
            )

        val runner = StartupGarageSyncConfig().startupGarageSyncRunner(useCase)
        runner.run(mockk(relaxed = true))

        verify(exactly = 1) { useCase.sync() }
    }
}
