# Task 7 — Hardening & Testing

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `.\.sisyphus\plans\task-6-revenue-api.md`
- Previous: Task 6 provided revenue endpoint behavior; this task hardens resilience and expands test depth.

## Objective
Add comprehensive test suites and resilience hardening while preserving simplicity.

## Scope
- IN: negative-path tests, architecture tests updates, concurrency checks, error handling.
- OUT: new business features.

## TDD Cycle (strict)
Use RED-GREEN-REFACTOR for each hardening rule added.

## Implementation Checklist
- [X] Expand domain/application edge-case test matrix.
- [X] Add webhook malformed payload tests.
- [X] Add concurrency scenario for near-full/full occupancy ENTRY handling.
- [X] Confirm ArchUnit checks cover new packages.
- [X] Validate logging correlation behavior tests where feasible.

## Acceptance Criteria
- [X] Core rules have happy + failure coverage.
- [X] Domain coverage remains 100%.
- [X] CI quality gates remain green.
- [X] No architectural boundary regressions.

## QA Scenarios
Scenario: concurrent ENTRY at limit
- Tool: integration test suite
- Expected: capacity never exceeds max; at-most-allowed entries accepted.

Scenario: malformed webhook payload
- Tool: Rest Assured tests
- Expected: controlled error path without state corruption.

## Git Workflow
- Branch: `feat(task-7)-hardening-and-testing`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-7): hardening-and-testing`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 7 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-7-hardening-and-testing.md`
