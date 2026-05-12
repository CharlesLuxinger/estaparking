package com.charlesluxinger.estaparking.infra.persistence.spot

import com.charlesluxinger.estaparking.domain.spot.SpotStatus
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SpotJpaAdapterTest {
    @Test
    fun `findById returns spot when found`() {
        val repository = mockk<SpotSpringDataRepository>()
        val entity =
            SpotEntity(
                id = 1L,
                sector = "A",
                latitude = BigDecimal("-23.550000"),
                longitude = BigDecimal("-46.630000"),
                status = SpotStatus.AVAILABLE,
            )

        every { repository.findById(1L) } returns java.util.Optional.of(entity)

        val adapter = SpotJpaAdapter(repository)
        val result = adapter.findById(1L)

        assertEquals(1L, result?.id)
        assertEquals("A", result?.sector)
    }

    @Test
    fun `findById returns null when not found`() {
        val repository = mockk<SpotSpringDataRepository>()

        every { repository.findById(999L) } returns java.util.Optional.empty()

        val adapter = SpotJpaAdapter(repository)
        val result = adapter.findById(999L)

        assertNull(result)
    }
}
