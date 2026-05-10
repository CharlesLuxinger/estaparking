# Task 3 — Webhook & Synchronization

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `.\.sisyphus\plans\task-2-persistence.md`
- Previous: Task 2 prepared persistence and repository adapters; this task integrates simulator sync and webhook event intake.

## Objective
Implement startup synchronization (`GET /garage` simulator) and webhook handler (`/webhook`) for ENTRY/PARKED/EXIT events.

## Scope
- IN: simulator client (Ktor Client), sync use case, webhook endpoint, request validation.
- OUT: final billing/revenue calculations (delegated to later tasks).

## Mandatory Contract
- Webhook endpoint: `POST http://localhost:3003/webhook`.
- Must accept ENTRY/PARKED/EXIT payloads from challenge format.
- Response contract remains HTTP 200 (including logically rejected full-capacity ENTRY).
- Idempotency scope: per active parking session (resets after session EXIT/closure).

## TDD Cycle (strict)
1. RED: failing controller/use-case tests for payload handling and sync bootstrapping.
2. GREEN: minimal implementation.
3. REFACTOR: isolate adapters and mappers.

## Implementation Checklist
- [x] Implement Ktor Client adapter for simulator `/garage`.
- [x] Sync garage sectors/spots at app startup.
- [x] Implement webhook controller + DTO parsing.
- [x] Route events to application inbound ports only.
- [x] Add idempotency guard (`plate+event_type`) in application/domain flow.

## Acceptance Criteria
- [x] Startup sync stores garage and spots from simulator.
- [x] Webhook accepts all three event types with HTTP 200.
- [x] Duplicate event is safely ignored/idempotent.
- [x] Controllers do not import repositories/adapters directly.
- [x] Refactor webhook use-case to return explicit outcome instead of empty rejected-transition branch.
- [x] Refactor simulator client adapter: inject HttpClient bean and simplify exception mapping.
- [x] Inline simulator mapping into DTO/adapter and remove SimulatorGarageMapper.kt.
- [x] Refactor toSnapshot hadouken into flat helper-based flow.

## QA Scenarios
Scenario: simulator bootstrap
- Tool: Docker + `./gradlew test --tests "*Sync*"`
- Expected: garage config persisted after startup process.

Scenario: duplicate ENTRY webhook
- Tool: `./gradlew test --tests "*Webhook*"`
- Expected: second identical event has no duplicate side-effect.

## Git Workflow
- Branch: `feat(task-3)-webhook-and-sync`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-3): webhook-and-sync`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 3 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-3-webhook-and-sync.md`
