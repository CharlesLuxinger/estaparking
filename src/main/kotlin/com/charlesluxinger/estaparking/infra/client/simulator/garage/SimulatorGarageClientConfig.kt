package com.charlesluxinger.estaparking.infra.client.simulator.garage

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SimulatorGarageClientConfig {
    @Bean("simulatorGarageHttpClient")
    fun simulatorGarageHttpClient(): HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson()
            }
            expectSuccess = false
        }
}
