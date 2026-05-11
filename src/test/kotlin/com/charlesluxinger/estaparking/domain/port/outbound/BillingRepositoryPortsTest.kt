package com.charlesluxinger.estaparking.domain.port.outbound

import com.charlesluxinger.estaparking.domain.vehicle.Vehicle
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class BillingRepositoryPortsTest {
    @Test
    fun `BillingRecordRepositoryPort should use Vehicle instead of String for license plate`() {
        val port = mockk<BillingRecordRepositoryPort>(relaxed = true)
        val vehicle = Vehicle("ABC1234")

        port.findByParkingIdAndLicensePlate("parking-123", vehicle)

        verify { port.findByParkingIdAndLicensePlate("parking-123", vehicle) }
    }

    @Test
    fun `PricingSnapshotRepositoryPort should use Vehicle instead of String for license plate`() {
        val port = mockk<PricingSnapshotRepositoryPort>(relaxed = true)
        val vehicle = Vehicle("ABC1234")

        port.findLatestByParkingIdAndLicensePlate("parking-123", vehicle)

        verify { port.findLatestByParkingIdAndLicensePlate("parking-123", vehicle) }
    }
}
