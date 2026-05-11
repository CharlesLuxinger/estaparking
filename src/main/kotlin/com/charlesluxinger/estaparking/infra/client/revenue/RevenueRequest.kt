package com.charlesluxinger.estaparking.infra.client.revenue

import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryRequest
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class RevenueRequest(
    @field:NotNull(message = "Date is required")
    @JsonProperty("date")
    val date: LocalDate? = null,
    @field:NotBlank(message = "Sector is required")
    @JsonProperty("sector")
    val sector: String? = null,
) {
    fun toQueryRequest(): RevenueQueryRequest =
        RevenueQueryRequest(
            date = date!!,
            sector = sector!!,
        )
}
