package com.charlesluxinger.estaparking.application.service.sync

import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageError
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageError.PersistenceFailure
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageError.SimulatorFetchFailed
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageSummary
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientPort
import com.charlesluxinger.estaparking.domain.port.outbound.SpotRepositoryPort
import com.charlesluxinger.estaparking.domain.result.DomainResult
import com.charlesluxinger.estaparking.domain.result.DomainResult.Error
import com.charlesluxinger.estaparking.domain.result.DomainResult.Success

class StartupGarageSyncUseCaseImpl(
    private val simulatorGarageClientPort: SimulatorGarageClientPort,
    private val billingRepositoryPort: BillingRepositoryPort,
    private val spotRepositoryPort: SpotRepositoryPort,
) : SyncGarageCommandPort {
    override fun sync(): DomainResult<SyncGarageSummary, SyncGarageError> =
        when (val snapshotResult = simulatorGarageClientPort.fetchGarage()) {
            is DomainResult.Error -> {
                Error(SimulatorFetchFailed(snapshotResult.error))
            }

            is DomainResult.Success -> {
                runCatching {
                    val syncedGarages = billingRepositoryPort.saveAll(snapshotResult.value.garages)
                    val syncedSpots = spotRepositoryPort.saveAll(snapshotResult.value.spots)

                    Success(
                        SyncGarageSummary(
                            garagesSynced = syncedGarages.size,
                            spotsSynced = syncedSpots.size,
                        ),
                    )
                }.getOrElse { exception ->
                    Error(
                        PersistenceFailure(
                            exception.message ?: "Unexpected persistence error during startup sync",
                        ),
                    )
                }
            }
        }
}
