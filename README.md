[![CI](https://github.com/CharlesLuxinger/estaparking/actions/workflows/ci.yml/badge.svg)](https://github.com/CharlesLuxinger/estaparking/actions/workflows/ci.yml)
[![Coverage](https://github.com/CharlesLuxinger/estaparking/blob/main/badges/jacoco.svg)](https://github.com/CharlesLuxinger/estaparking/actions/workflows/ci.yml)
[![Branches](https://github.com/CharlesLuxinger/estaparking/blob/main/badges/branches.svg)](https://github.com/CharlesLuxinger/estaparking/actions/workflows/ci.yml)

# ESTAPARKING

**Senior Kotlin Backend Software Engineer Tech Challenge**

ESTAPARKING is a parking management REST API built with Spring Boot and Kotlin. It integrates with a garage simulator via webhooks to track vehicle events and calculate dynamic pricing based on parking duration and zone.

> **IMPORTANT**: For local execution, API testing, and quality gate verification, see the comprehensive [Run and Test Guide](docs/run-and-test-guide.md).
>
> This guide covers:
> - Docker Compose setup for MySQL + Simulator + App
> - Application startup and health verification
> - API endpoint testing with curl examples
> - Running quality gates locally (ktlint, detekt, test, JaCoCo)

---

## Where to Read What

| Document                                           | Purpose |
|----------------------------------------------------|---------|
| [Implementation Specs](./.specs/)                  | Chronological development history and design decisions |
| [Run and Test Guide](./docs/run-and-test-guide.md) | Complete execution and testing instructions |

---

## Tech Stack

| Component | Version | Source |
|-----------|---------|--------|
| Language | Kotlin 2.3.21 | `build.gradle.kts` |
| Framework | Spring Boot 3.5.14 | `build.gradle.kts` |
| Java/JDK | 21 (Temurin) | `build.gradle.kts` + CI |
| Database | MySQL 8.0 + Flyway | `build.gradle.kts` |
| Testing | JUnit 5, MockK 1.13.17, RestAssured 5.5.7, ArchUnit 1.4.2, Testcontainers 1.20.4 | `build.gradle.kts` |
| Linting | ktlint 14.2.0, detekt 2.0.0-alpha.2 | `build.gradle.kts` |
| Coverage | JaCoCo | `build.gradle.kts` |

---

## Architecture

The project follows **Hexagonal Architecture (Ports & Adapters)** combined with **Domain-Driven Design (DDD)**:

| Layer | Package | Description |
|-------|---------|-------------|
| **Domain** | `domain/` | Core business logic (entities, value objects, domain services). Zero Spring annotations. |
| **Application** | `application/` | Use case implementations (`UseCaseImpl`). Orchestrates domain logic. No framework dependencies. |
| **Infrastructure** | `infra/` | Framework adapters (persistence, external clients, web controllers). Depends on application. |

### Governance Rules

- **Strict Dependency Direction**: `infra/` → `application/` → `domain/`
- **Controller Boundary**: Web controllers (`infra/client/`) must invoke inbound ports or use cases only. Direct access to repositories or infrastructure adapters is strictly forbidden.
- **Use Case Ownership**: Use case implementations belong to the application layer.

---

## Quality Gates & CI

### CI Pipeline

The project uses GitHub Actions with the following workflow:

| Step | Command | Purpose |
|------|---------|---------|
| Linting | `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck` | Code style enforcement |
| Static Analysis | `./gradlew detekt` | Code quality checks |
| Tests + Coverage | `./gradlew test jacocoTestReport` | Unit tests with coverage report |

**CI Triggers**: Push to `main`, Pull Request to `main`

### Coverage Requirements

| Scope | Minimum | Source |
|-------|---------|--------|
| Overall | 90% | `build.gradle.kts` |
| Domain Package | 100% | `build.gradle.kts` |

---

## Project Structure

```
src/main/kotlin/com/charlesluxinger/estaparking/
├── domain/           # Core business logic (entities, value objects, ports)
├── application/      # Use cases (UseCaseImpl)
├── infra/            # Adapters (controllers, persistence, clients)
└── Application.kt    # Entry point
```

---

## Documentation

- [Run and Test Guide](docs/run-and-test-guide.md) - Local execution, API testing, quality gates
- [AGENTS.md](AGENTS.md) - Architecture governance and source of truth
- [Original Challenge](.sisyphus/plans/estapar-backend-test.md) - Challenge requirements
- [Implementation Specs](./.specs/) - Development history
