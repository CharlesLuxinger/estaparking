package com.charlesluxinger.estaparking.infra.client.revenue

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class RevenueResponse(
    @JsonProperty("amount")
    val amount: BigDecimal,
    @JsonProperty("currency")
    val currency: String,
    @JsonProperty("timestamp")
    val timestamp: LocalDateTime,
)
