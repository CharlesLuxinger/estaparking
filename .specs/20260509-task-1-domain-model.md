# Task 1 — Domain Model

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Objective
Define core domain entities: `Parking`, `Spot`, `Vehicle`, `ParkingEvent` using hexagonal boundaries and domain-first TDD.

## Scope
- IN: entities/value objects/enums, domain invariants, domain unit tests.
- OUT: persistence mappings, controllers, external clients.

## Mandatory Decisions
- Dynamic pricing thresholds: upper-bound inclusive (`<=25`, `<=50`, `<=75`, `<=100`).
- Full occupancy ENTRY: reject logically (no allocation).
- Webhook idempotency assumption: idempotent by `license_plate + event_type`.

## TDD Cycle (strict)
1. RED: Write failing tests for entity invariants and state transitions.
2. GREEN: Implement minimum domain code.
3. REFACTOR: Improve names/structure without behavior changes.

## Implementation Checklist
- [x] Create one file per domain concept under `domain/`.
- [x] Model event types `ENTRY`, `PARKED`, `EXIT`.
- [x] Enforce spot occupancy and status transitions in domain methods.
- [x] Add domain-level error model for invalid transitions.
- [x] Add exhaustive unit tests for happy + failure paths.

## Acceptance Criteria
- [x] Domain package has zero Spring/framework annotations.
- [x] 100% domain coverage for newly added domain files.
- [x] Tests prove invalid transitions fail deterministically.
- [x] ArchUnit rules still pass.

## QA Scenarios
Scenario: valid lifecycle
- Tool: `./gradlew test --tests "*domain*"`
- Expected: ENTRY → PARKED → EXIT passes, billing preconditions satisfied.

Scenario: invalid lifecycle
- Tool: `./gradlew test --tests "*domain*"`
- Expected: EXIT before ENTRY fails with domain error.

## Git Workflow
- Branch: `feat(task-1)-domain-model`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-1): domain model`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 1 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-1-domain-model.md`
