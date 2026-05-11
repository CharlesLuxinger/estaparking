package com.charlesluxinger.estaparking.infra.client.revenue

import com.charlesluxinger.estaparking.domain.common.Currency
import java.math.BigDecimal
import java.time.LocalDateTime

data class RevenueResponse(
    val amount: BigDecimal,
    val currency: Currency,
    val timestamp: LocalDateTime,
)
