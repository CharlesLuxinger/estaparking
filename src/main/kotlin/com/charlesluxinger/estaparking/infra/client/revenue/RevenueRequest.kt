package com.charlesluxinger.estaparking.infra.client.revenue

import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryRequest
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class RevenueRequest(
    val date: LocalDate,
    @field:NotBlank(message = "Sector is required")
    val sector: String,
) {
    fun toQueryRequest(): RevenueQueryRequest =
        RevenueQueryRequest(
            date = date,
            sector = sector,
        )
}
