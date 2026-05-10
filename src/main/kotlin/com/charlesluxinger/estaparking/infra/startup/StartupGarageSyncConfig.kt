package com.charlesluxinger.estaparking.infra.startup

import com.charlesluxinger.estaparking.application.service.sync.StartupGarageSyncUseCaseImpl
import com.charlesluxinger.estaparking.domain.error.DomainResult
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.sync.SyncGarageError
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.SimulatorGarageClientPort
import com.charlesluxinger.estaparking.domain.port.outbound.SpotRepositoryPort
import mu.KotlinLogging
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StartupGarageSyncConfig {
    @Bean
    fun startupGarageSyncUseCase(
        simulatorGarageClientPort: SimulatorGarageClientPort,
        billingRepositoryPort: BillingRepositoryPort,
        spotRepositoryPort: SpotRepositoryPort,
    ): SyncGarageCommandPort =
        StartupGarageSyncUseCaseImpl(
            simulatorGarageClientPort = simulatorGarageClientPort,
            billingRepositoryPort = billingRepositoryPort,
            spotRepositoryPort = spotRepositoryPort,
        )

    @Bean
    fun startupGarageSyncRunner(startupGarageSyncUseCase: SyncGarageCommandPort): ApplicationRunner =
        ApplicationRunner {
            when (val syncResult = startupGarageSyncUseCase.sync()) {
                is DomainResult.Success -> {
                    logger.info {
                        "Startup garage sync completed: garages=${syncResult.value.garagesSynced}, " +
                            "spots=${syncResult.value.spotsSynced}"
                    }
                }

                is DomainResult.Error -> {
                    logger.warn {
                        "Startup garage sync skipped due to non-fatal error: ${syncResult.error.toLogMessage()}"
                    }
                }
            }
        }

    private fun SyncGarageError.toLogMessage(): String =
        when (this) {
            is SyncGarageError.PersistenceFailure -> message
            is SyncGarageError.SimulatorFetchFailed -> cause.toString()
        }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
