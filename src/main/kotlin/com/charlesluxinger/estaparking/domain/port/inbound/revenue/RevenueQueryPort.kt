package com.charlesluxinger.estaparking.domain.port.inbound.revenue

import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryRequest
import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryResponse

interface RevenueQueryPort {
    fun getRevenue(request: RevenueQueryRequest): RevenueQueryResponse
}
