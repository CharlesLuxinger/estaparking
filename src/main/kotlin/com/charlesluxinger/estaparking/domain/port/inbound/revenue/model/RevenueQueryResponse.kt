package com.charlesluxinger.estaparking.domain.port.inbound.revenue.model

import com.charlesluxinger.estaparking.domain.common.Currency
import java.math.BigDecimal
import java.time.LocalDateTime

data class RevenueQueryResponse(
    val amount: BigDecimal,
    val currency: Currency = Currency.BRL,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
