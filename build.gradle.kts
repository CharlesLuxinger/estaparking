plugins {
	kotlin("jvm") version "2.3.10"
	kotlin("plugin.spring") version "2.3.10"
	kotlin("plugin.jpa") version "2.3.10"
	id("org.springframework.boot") version "3.5.14"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
	id("dev.detekt") version "2.0.0-alpha.2"
	jacoco
}

group = "com.charlesluxinger.estaparking"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	matching { it.name.startsWith("detekt") }.configureEach {
		resolutionStrategy.eachDependency {
			if (requested.group == "org.jetbrains.kotlin") {
				useVersion("2.3.0")
			}
		}
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	//TEST
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("com.tngtech.archunit:archunit-junit5:1.4.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

detekt {
	toolVersion = "2.0.0-alpha.2"
	buildUponDefaultConfig = true
	config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
	baseline = file("$rootDir/config/detekt/baseline.xml")
	ignoreFailures = false
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
	}
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude("**/config/**", "**/health/**")
                }
            },
        ),
    )
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.9".toBigDecimal()
			}
		}
	}
}
