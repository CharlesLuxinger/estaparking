package com.charlesluxinger.estaparking.domain.port.inbound.sync

import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientError
import com.charlesluxinger.estaparking.domain.result.DomainResult

interface SyncGarageCommandPort {
    fun sync(): DomainResult<SyncGarageSummary, SyncGarageError>
}

data class SyncGarageSummary(
    val garagesSynced: Int,
    val spotsSynced: Int,
)

sealed interface SyncGarageError {
    data class SimulatorFetchFailed(
        val cause: SimulatorGarageClientError,
    ) : SyncGarageError

    data class PersistenceFailure(
        val message: String,
    ) : SyncGarageError
}
