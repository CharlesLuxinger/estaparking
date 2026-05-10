package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.application.service.webhook.HandleWebhookEventUseCaseImpl
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebhookConfig {
    @Bean
    fun handleWebhookEventUseCase(
        parkingSessionRepositoryPort: ParkingSessionRepositoryPort,
        parkingEventRepositoryPort: ParkingEventRepositoryPort,
    ): WebhookEventCommandPort =
        HandleWebhookEventUseCaseImpl(
            parkingSessionRepositoryPort = parkingSessionRepositoryPort,
            parkingEventRepositoryPort = parkingEventRepositoryPort,
        )
}
