package com.charlesluxinger.estaparking.infra.client.revenue

import com.charlesluxinger.estaparking.application.service.revenue.GetRevenueQueryUseCaseImpl
import com.charlesluxinger.estaparking.domain.port.inbound.revenue.RevenueQueryPort
import com.charlesluxinger.estaparking.domain.port.outbound.BillingTransactionRepositoryPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RevenueConfig {
    @Bean
    fun getRevenueQueryUseCase(billingTransactionRepositoryPort: BillingTransactionRepositoryPort): RevenueQueryPort =
        GetRevenueQueryUseCaseImpl(billingTransactionRepositoryPort)
}
