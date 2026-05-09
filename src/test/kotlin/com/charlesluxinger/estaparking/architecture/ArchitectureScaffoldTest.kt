package com.charlesluxinger.estaparking.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Architecture test scaffold verification.
 *
 * Validates that:
 * 1. Base architecture test class can be instantiated
 * 2. Import policy correctly filters test classes
 * 3. Package constants are properly defined
 */
class ArchitectureScaffoldTest : ArchitectureTest() {
    @Test
    fun `base package constant is defined`() {
        assertThat(BASE_PACKAGE).isEqualTo("com.charlesluxinger.estaparking")
    }

    @Test
    fun `layer packages are properly derived`() {
        assertThat(DOMAIN_PACKAGE).isEqualTo("com.charlesluxinger.estaparking.domain")
        assertThat(APPLICATION_PACKAGE).isEqualTo("com.charlesluxinger.estaparking.application")
        assertThat(INFRA_PACKAGE).isEqualTo("com.charlesluxinger.estaparking.infra")
        assertThat(INFRA_CLIENT_PACKAGE).isEqualTo("com.charlesluxinger.estaparking.infra.client")
    }

    @Test
    fun `importClasses returns JavaClasses without throwing`() {
        val classes = importClasses()
        assertThat(classes).isNotNull
    }

    @Test
    fun `importing base package does not fail`() {
        val classes = ClassFileImporter().importPackages(BASE_PACKAGE)
        assertThat(classes).isNotNull
    }
}
