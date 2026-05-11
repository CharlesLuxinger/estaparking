package com.charlesluxinger.estaparking.domain.port.inbound.revenue.model

import java.time.LocalDate

data class RevenueQueryRequest(
    val date: LocalDate,
    val sector: String,
)
