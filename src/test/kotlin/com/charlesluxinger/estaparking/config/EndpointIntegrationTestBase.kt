package com.charlesluxinger.estaparking.config

import org.springframework.test.context.ActiveProfiles

/**
 * Shared base class for endpoint integration tests.
 * Provides common profile activation for container-backed database tests.
 *
 * Usage:
 * ```
 * @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 * @Import(EndpointIntegrationTestBase::class, ContainersConfig::class)
 * class MyControllerV1IT : EndpointIntegrationTestBase() {
 *
 *     @BeforeEach
 *     fun cleanup() {
 *         // clean tables via autowired repositories
 *     }
 * }
 * ```
 *
 * Subclasses manage their own `@SpringBootTest` annotation and data isolation.
 * No duplicate container bootstrap — [ContainersConfig] is imported by subclasses.
 */
@ActiveProfiles("test")
open class EndpointIntegrationTestBase
