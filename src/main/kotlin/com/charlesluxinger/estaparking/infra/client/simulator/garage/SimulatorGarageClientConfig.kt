package com.charlesluxinger.estaparking.infra.client.simulator.garage

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class SimulatorGarageClientConfig {
    @Bean("simulatorGarageRestTemplate")
    fun simulatorGarageRestTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()
}
