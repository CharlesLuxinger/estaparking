package com.charlesluxinger.estaparking.domain.billing

import java.math.BigDecimal
import java.time.LocalDateTime

data class BillingTransaction(
    val id: String,
    val licensePlate: String,
    val sector: String,
    val amount: BigDecimal,
    val exitTime: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
