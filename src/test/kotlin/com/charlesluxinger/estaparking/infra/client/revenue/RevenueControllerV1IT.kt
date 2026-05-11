package com.charlesluxinger.estaparking.infra.client.revenue

import com.charlesluxinger.estaparking.domain.port.inbound.revenue.RevenueQueryPort
import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryRequest
import com.charlesluxinger.estaparking.domain.port.inbound.revenue.model.RevenueQueryResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDateTime

@WebMvcTest(RevenueControllerV1::class)
@Import(RevenueControllerV1IT.TestConfig::class)
class RevenueControllerV1IT {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var recordingRevenueQueryPort: RecordingRevenueQueryPort

    @Test
    fun `get revenue with valid request returns 200`() {
        recordingRevenueQueryPort.nextResponse =
            RevenueQueryResponse(
                amount = BigDecimal("100.00"),
                currency = "BRL",
                timestamp = LocalDateTime.of(2025, 1, 1, 12, 0),
            )

        mockMvc
            .perform(
                get("/revenue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"date\":\"2025-01-01\"," +
                            "\"sector\":\"A\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.amount").value(100.00))
            .andExpect(jsonPath("$.currency").value("BRL"))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    fun `get revenue with missing date returns 400`() {
        mockMvc
            .perform(
                get("/revenue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"sector\":\"A\"" +
                            "}",
                    ),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `get revenue with missing sector returns 400`() {
        mockMvc
            .perform(
                get("/revenue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"date\":\"2025-01-01\"" +
                            "}",
                    ),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `get revenue with empty sector returns 400`() {
        mockMvc
            .perform(
                get("/revenue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"date\":\"2025-01-01\"," +
                            "\"sector\":\"\"" +
                            "}",
                    ),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `get revenue returns zero amount when no data`() {
        recordingRevenueQueryPort.nextResponse =
            RevenueQueryResponse(
                amount = BigDecimal.ZERO,
                currency = "BRL",
                timestamp = LocalDateTime.of(2025, 1, 1, 12, 0),
            )

        mockMvc
            .perform(
                get("/revenue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{" +
                            "\"date\":\"2025-01-01\"," +
                            "\"sector\":\"A\"" +
                            "}",
                    ),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.amount").value(0))
            .andExpect(jsonPath("$.currency").value("BRL"))
    }

    class TestConfig {
        @Bean
        fun recordingRevenueQueryPort(): RecordingRevenueQueryPort = RecordingRevenueQueryPort()

        @Bean
        fun revenueControllerV1(port: RecordingRevenueQueryPort): RevenueControllerV1 = RevenueControllerV1(port)
    }

    class RecordingRevenueQueryPort : RevenueQueryPort {
        var nextResponse: RevenueQueryResponse? = null

        override fun getRevenue(request: RevenueQueryRequest): RevenueQueryResponse =
            nextResponse
                ?: RevenueQueryResponse(
                    amount = BigDecimal.ZERO,
                    currency = "BRL",
                    timestamp = LocalDateTime.now(),
                )
    }
}
