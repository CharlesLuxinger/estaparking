package com.charlesluxinger.estaparking.infra.client.revenue

import com.charlesluxinger.estaparking.domain.port.inbound.revenue.RevenueQueryPort
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RevenueControllerV1(
    private val revenueQueryPort: RevenueQueryPort,
) {
    @GetMapping("/revenue")
    fun getRevenue(
        @Valid @RequestBody request: RevenueRequest,
    ): ResponseEntity<RevenueResponse> {
        val response = revenueQueryPort.getRevenue(request.toQueryRequest())
        return ResponseEntity.ok(
            RevenueResponse(
                amount = response.amount,
                currency = response.currency,
                timestamp = response.timestamp,
            ),
        )
    }
}
