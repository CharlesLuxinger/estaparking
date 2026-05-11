package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.application.service.entry.EntryUseCaseImpl
import com.charlesluxinger.estaparking.application.service.parked.ParkedUseCaseImpl
import com.charlesluxinger.estaparking.application.service.webhook.HandleWebhookEventUseCaseImpl
import com.charlesluxinger.estaparking.domain.port.inbound.entry.EntryCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.parked.ParkedCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingTransactionRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebhookConfig {
    @Bean
    fun entryUseCase(): EntryCommandPort = EntryUseCaseImpl()

    @Bean
    fun parkedUseCase(): ParkedCommandPort = ParkedUseCaseImpl()

    @Bean
    fun handleWebhookEventUseCase(
        parkingSessionRepositoryPort: ParkingSessionRepositoryPort,
        parkingEventRepositoryPort: ParkingEventRepositoryPort,
        entryCommandPort: EntryCommandPort,
        parkedCommandPort: ParkedCommandPort,
        billingTransactionRepositoryPort: BillingTransactionRepositoryPort,
        billingRepositoryPort: BillingRepositoryPort,
    ): WebhookEventCommandPort =
        HandleWebhookEventUseCaseImpl(
            parkingSessionRepositoryPort = parkingSessionRepositoryPort,
            parkingEventRepositoryPort = parkingEventRepositoryPort,
            entryCommandPort = entryCommandPort,
            parkedCommandPort = parkedCommandPort,
            billingTransactionRepositoryPort = billingTransactionRepositoryPort,
            billingRepositoryPort = billingRepositoryPort,
        )
}
