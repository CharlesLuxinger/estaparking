# Task 2 — Persistence

> Source of truth: `\.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `\.\.sisyphus\plans\task-1-domain-model.md`
- Previous: Task 1 established domain entities and invariants with TDD; this task persists those concepts through ports/adapters.

## Objective
Implement repositories/adapters and JPA configuration aligned to domain ports and hexagonal architecture.

## Scope
- IN: outbound ports, JPA entities/mappers, repository adapters, migrations/schema.
- OUT: webhook/controller logic.

## TDD Cycle (strict)
1. RED: failing repository adapter tests (Testcontainers MySQL).
2. GREEN: minimum JPA adapter implementation.
3. REFACTOR: mapping cleanup and naming alignment.

## Implementation Checklist
- [x] Define outbound repository ports in `domain/port/outbound`.
- [x] Implement adapters in `infra/` using naming `<Feature><Tech>Adapter`.
- [x] Create persistence models/mappers (one file per context).
- [x] Configure JPA for spot, parking session, events, billing data.
- [x] Add integration tests with Testcontainers.

## Acceptance Criteria
- [x] No domain imports from infra/application.
- [x] Repository adapters satisfy port contracts.
- [x] Integration tests pass against MySQL container.
- [x] Coverage and quality gates remain green.

## QA Scenarios
Scenario: persist and retrieve session
- Tool: `./gradlew test --tests "*Repository*"`
- Expected: saved session and events reloaded exactly.

Scenario: mapping constraint violation
- Tool: `./gradlew test --tests "*Repository*"`
- Expected: invalid required field fails with controlled exception.

## Git Workflow
- Branch: `feat(task-2)-persistence`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-2): persistence`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 2 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-2-persistence.md`
