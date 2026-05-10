# Task 5 — Billing & Events

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `.\.sisyphus\plans\task-4-core-use-cases-and-pricing.md`
- Previous: Task 4 implemented entry/parked/pricing logic; this task finalizes billing and lifecycle event publication.

## Objective
Implement billing completion at EXIT and publish domain/application events for relevant lifecycle transitions.

## Scope
- IN: billing record creation, event publishing port + adapter, EXIT flow integration.
- OUT: revenue API exposure.

## TDD Cycle (strict)
1. RED: failing tests for billing amount and event emission.
2. GREEN: minimum billing + publisher implementation.
3. REFACTOR: split billing and event concerns cleanly.

## Implementation Checklist
- [X] Define event publishing outbound port.
- [X] Implement adapter (log-based or broker-stub per challenge scope).
- [X] Persist billing result on EXIT.
- [X] Add duplicate EXIT/idempotency behavior tests.

## Acceptance Criteria
- [X] EXIT creates exactly one billing record.
- [X] Billing uses pricing snapshot derived at ENTRY policy.
- [X] Event publication occurs once per accepted lifecycle transition.
- [X] Duplicate EXIT does not double-charge.

## QA Scenarios
Scenario: normal EXIT billing
- Tool: `./gradlew test --tests "*Billing*"`
- Expected: billed amount matches rule; one event emitted.

Scenario: duplicate EXIT
- Tool: `./gradlew test --tests "*Billing*"`
- Expected: no second billing/event side effect.

## Git Workflow
- Branch: `feat(task-5)-billing-and-events`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-5): billing-and-events`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 5 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-5-billing-and-events.md`
