package com.charlesluxinger.estaparking.infra.client.webhook

import com.charlesluxinger.estaparking.application.service.entry.EntryUseCaseImpl
import com.charlesluxinger.estaparking.application.service.parked.ParkedUseCaseImpl
import com.charlesluxinger.estaparking.application.service.webhook.HandleWebhookEventUseCaseImpl
import com.charlesluxinger.estaparking.domain.port.inbound.entry.EntryCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.parked.ParkedCommandPort
import com.charlesluxinger.estaparking.domain.port.inbound.webhook.WebhookEventCommandPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRecordRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.LifecycleEventPublisherPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingEventRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.ParkingSessionRepositoryPort
import com.charlesluxinger.estaparking.domain.port.outbound.PricingSnapshotRepositoryPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

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
        billingRepositoryPort: BillingRepositoryPort,
        pricingSnapshotRepositoryPort: PricingSnapshotRepositoryPort,
        billingRecordRepositoryPort: BillingRecordRepositoryPort,
        lifecycleEventPublisherPort: LifecycleEventPublisherPort,
        clock: Clock,
    ): WebhookEventCommandPort =
        HandleWebhookEventUseCaseImpl(
            parkingSessionRepositoryPort = parkingSessionRepositoryPort,
            parkingEventRepositoryPort = parkingEventRepositoryPort,
            entryCommandPort = entryCommandPort,
            parkedCommandPort = parkedCommandPort,
            billingRepositoryPort = billingRepositoryPort,
            pricingSnapshotRepositoryPort = pricingSnapshotRepositoryPort,
            billingRecordRepositoryPort = billingRecordRepositoryPort,
            lifecycleEventPublisherPort = lifecycleEventPublisherPort,
            clock = clock,
        )

    @Bean
    fun utcClock(): Clock = Clock.systemUTC()
}
