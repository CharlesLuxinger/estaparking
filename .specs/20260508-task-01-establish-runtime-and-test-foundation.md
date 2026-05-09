# Task 01 — Establish Runtime and Test Foundation

> **From**: `tech-challenge-backend.md`
> **Critical Path Position**: START — blocks all other tasks
> **Wave**: 1 (first, no dependencies)
> **Next file**: [`task-02-domain-model.md`](./task-02-domain-model.md) (unblock after this completes)

---

## Shared Context

### Must Have (project-wide)
- TDD cycle (RED → GREEN → REFACTOR) for each feature task.
- Domain free of Spring annotations (except `domain/config` if strictly needed).
- Strict dependency rule: Infrastructure → Application → Domain.
- Ports in domain or application, adapters in infrastructure.
- One commit per feature slice.
- MDC keys included in inbound and business transition logs.

### Must NOT Have (project-wide guardrails)
- No authentication/authorization features.
- No extra endpoints beyond challenge requirements.
- No event broker / async queue introduction.
- No business rule enforcement for `open_hour`, `close_hour`, `duration_limit_minutes` in this scope.
- No controller-to-repository direct calls (must go through inbound use-case ports).
- No JPA entity leakage into domain models.

### Architecture Rules
| Layer | Package | Rule |
|-------|---------|------|
| Domain | `domain/` | Core business logic. Zero Spring annotations. |
| Application | `application/` | Use case implementations (`UseCaseImpl`). No framework dependencies. |
| Infrastructure | `infra/` | Framework adapters. Depends on application. |

### CI Command Order (mandatory)
```bash
./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck
./gradlew detekt
./gradlew test jacocoTestReport
```

### Decisions Needed Before Execution
- [DECISION NEEDED: Idempotency] Exact deduplication key strategy for duplicate webhook events (payload currently lacks explicit event id).
- [DECISION NEEDED: Transition policy] Whether to strictly reject out-of-order transitions (e.g., EXIT without prior PARKED) with 409 vs tolerant no-op 200.
- [DECISION NEEDED: Money/time precision] Exact rounding (`HALF_UP`?) and timezone policy (UTC?) for billing/revenue date partitioning.
- [DECISION NEEDED: Startup resilience] Fail-fast vs degraded startup when simulator is unavailable.

---

## TODO

- [ ] 1. Establish runtime and test foundation

  **What to do**:
  - Add required dependencies/configuration: MySQL driver, Flyway, MockK, Testcontainers (`org.testcontainers:mysql`), kotlin-logging.
  - Add Rest Assured Kotlin DSL dependency (`io.rest-assured:kotlin-extensions`) to test scope.
  - Configure app to run on port `3003`.
  - Add baseline `application.yml` / profile config for local + test.
  - Add coverage rule targeting `domain/**` at 100% (line + branch) while preserving global gates.

  **Must NOT do**:
  - Do not weaken existing CI quality gates.
  - Do not add optional tooling unrelated to challenge scope.
  - Do not combine multiple classes, interfaces, or exceptions in a single `.kt` file — one top-level declaration per file.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: focused build/config changes.
  - Skills: [`kotlin-springboot`] - dependency and config correctness.
  - Omitted: [`kotlin-coroutines-flows`] - not needed.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: 2,3,5,11 | Blocked By: none

  **References**:
  - Pattern: `build.gradle.kts:1-98` - existing plugin/test/lint setup.
  - Pattern: `AGENTS.md:33-60` - mandatory CI/lint/test order.
  - API/Type: `src/main/kotlin/com/charlesluxinger/estaparking/Application.kt:1-11` - app bootstrap.
  - External: `https://java.testcontainers.org/modules/databases/mysql/` - Testcontainers MySQL Java module docs.
  - External: `https://github.com/rest-assured/rest-assured/wiki/GettingStarted` - Rest Assured getting started.
  - External: `https://github.com/rest-assured/rest-assured/wiki/Kotlin` - Rest Assured Kotlin DSL reference.

  **Acceptance Criteria**:
  - [ ] `./gradlew dependencies` resolves with new runtime/test dependencies.
  - [ ] `./gradlew dependencies` shows `org.testcontainers:mysql` and `io.rest-assured:kotlin-extensions` in test scope.
  - [ ] App starts listening on port 3003 under local profile.
  - [ ] Coverage verification fails if any `domain/**` class drops below 100%.

  **QA Scenarios**:
  ```
  Scenario: App boots on required webhook port
    Tool: Bash
    Steps: ./gradlew bootRun ; call http://localhost:3003/actuator/health (or startup log grep)
    Expected: process starts and binds port 3003
    Evidence: .sisyphus/evidence/task-1-foundation.log

  Scenario: Domain coverage gate enforcement
    Tool: Bash
    Steps: ./gradlew test jacocoTestCoverageVerification
    Expected: build fails on any domain class <100%
    Evidence: .sisyphus/evidence/task-1-foundation-coverage.log
  ```

  **Pre-Commit Checklist** (run in order — all must pass before committing):
  1. Run full CI suite in mandated order:
     ```bash
     ./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck
     ./gradlew detekt
     ./gradlew test jacocoTestReport
     ```
  2. Run `/graphify-windows` skill and confirm the knowledge graph is updated.
  3. Copy this task file to `.specs/` with date prefix:
     ```powershell
     Copy-Item ".sisyphus\plans\task-01-foundation.md" ".specs\YYYYMMDD-task-01-establish-runtime-and-test-foundation.md"
     ```
     Replace `YYYYMMDD` with today's date (e.g. `20260508`).

  **Commit**: YES
  Message: `feat(foundation): configure runtime, testing, and coverage gates`
  Files: `build.gradle.kts`, `src/main/resources/**`, `src/test/resources/**`, `.specs/YYYYMMDD-task-01-establish-runtime-and-test-foundation.md`
