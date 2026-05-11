package com.charlesluxinger.estaparking.application.service.revenue

import com.charlesluxinger.estaparking.domain.port.inbound.revenue.RevenueQueryPort
import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryRequest
import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryResponse
import com.charlesluxinger.estaparking.domain.port.outbound.BillingTransactionRepositoryPort
import java.math.BigDecimal
import java.time.LocalTime

class GetRevenueQueryUseCaseImpl(
    private val billingTransactionRepositoryPort: BillingTransactionRepositoryPort,
) : RevenueQueryPort {
    override fun getRevenue(request: RevenueQueryRequest): RevenueQueryResponse {
        val startOfDay = request.date.atStartOfDay()
        val endOfDay = request.date.atTime(LocalTime.MAX)

        val transactions =
            billingTransactionRepositoryPort.findBySectorAndExitTimeBetween(
                sector = request.sector,
                startTime = startOfDay,
                endTime = endOfDay,
            )

        val totalAmount =
            transactions
                .map { it.amount }
                .fold(BigDecimal.ZERO, BigDecimal::add)

        return RevenueQueryResponse(
            amount = totalAmount,
        )
    }
}
