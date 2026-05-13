package com.charlesluxinger.estaparking.domain.spot

import com.charlesluxinger.estaparking.domain.common.Coordinates
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoordinatesTest {
    @Test
    fun `data class generated methods are exercised`() {
        val original =
            Coordinates(
                latitude = BigDecimal("-23.55052"),
                longitude = BigDecimal("-46.633308"),
            )

        val sameValues =
            Coordinates(
                latitude = BigDecimal("-23.55052"),
                longitude = BigDecimal("-46.633308"),
            )

        val different = original.copy(latitude = BigDecimal("-22.90000"))
        val (latitude, longitude) = original

        assertEquals(original, sameValues)
        assertEquals(original.hashCode(), sameValues.hashCode())
        assertNotEquals(original, different)
        assertEquals(BigDecimal("-23.55052"), original.latitude)
        assertEquals(BigDecimal("-46.633308"), original.longitude)
        assertEquals(BigDecimal("-23.55052"), latitude)
        assertEquals(BigDecimal("-46.633308"), longitude)
        assertTrue(original.toString().contains("Coordinates(latitude=-23.55052, longitude=-46.633308)"))
    }
}
