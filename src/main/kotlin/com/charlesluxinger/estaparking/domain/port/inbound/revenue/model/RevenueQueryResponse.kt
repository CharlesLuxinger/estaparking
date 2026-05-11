package com.charlesluxinger.estaparking.domain.port.inbound.revenue.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class RevenueQueryResponse(
    val amount: BigDecimal,
    val currency: String = "BRL",
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
