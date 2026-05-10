package com.charlesluxinger.estaparking.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer

@TestConfiguration(proxyBeanMethods = false)
class ContainersConfig {
    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer<*> =
        MySQLContainer("mysql:8.4.0")
            .withDatabaseName("estaparking_test")
            .withUsername("estaparking")
            .withPassword("estaparking")
}
