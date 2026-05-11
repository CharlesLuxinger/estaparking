# Task 6 — Revenue API

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `.\.sisyphus\plans\task-5-billing-and-events.md`
- Previous: Task 5 delivered billing output and events; this task exposes revenue aggregation through the API contract.

## Objective
Implement revenue calculation and expose `GET /revenue` following challenge contract.

## Contract (approved)
- Method/path: `GET /revenue`
- Input: JSON body containing `date` and `sector`.
- Output: `{ amount, currency, timestamp }` (BRL).
- Validation: missing/invalid body or required fields returns HTTP 400.

## TDD Cycle (strict)
1. RED: contract tests for valid/invalid payloads and no-data behavior.
2. GREEN: minimum query + response mapping.
3. REFACTOR: isolate query model and mapper.

## Implementation Checklist
- [X] Implement revenue query inbound port and use case.
- [X] Aggregate by sector/date from billing data.
- [X] Implement REST adapter with exact challenge response shape.
- [X] Add integration tests (Rest Assured Kotlin DSL).

## Acceptance Criteria
- [X] Endpoint accepts GET with JSON body.
- [X] Returns `amount=0.00` when no matching revenue exists.
- [X] Currency fixed as `BRL`.
- [X] Endpoint behavior covered with automated tests.

## QA Scenarios
Scenario: revenue found
- Tool: `./gradlew test --tests "*Revenue*"`
- Expected: response amount equals expected aggregate.

Scenario: revenue not found
- Tool: `./gradlew test --tests "*Revenue*"`
- Expected: HTTP success + zero amount response.

## Git Workflow
- Branch: `feat(task-6)-revenue-api`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-6): revenue-api`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 6 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-6-revenue-api.md`
