package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.garage.Garage

interface BillingRepositoryPort {
    fun findGarageBySector(sector: String): Garage?
}
