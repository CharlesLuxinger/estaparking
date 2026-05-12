package com.charlesluxinger.estaparking.domain.spot

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SpotStatusTest {
    @Test
    fun `enum constants are fully covered`() {
        assertEquals(SpotStatus.AVAILABLE, SpotStatus.valueOf("AVAILABLE"))
        assertEquals(SpotStatus.ENTRY_REGISTERED, SpotStatus.valueOf("ENTRY_REGISTERED"))
        assertEquals(SpotStatus.PARKED, SpotStatus.valueOf("PARKED"))
        assertEquals(listOf("AVAILABLE", "ENTRY_REGISTERED", "PARKED"), SpotStatus.entries.map { it.name })
    }
}
