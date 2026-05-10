# Task 4 — Core Use Cases & Pricing

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `.\.sisyphus\plans\task-3-webhook-and-sync.md`
- Previous: Task 3 enabled sync and webhook ingestion; this task applies core business use cases and pricing policies.

## Objective
Implement ENTRY, PARKED, pricing rules, and occupancy checks as core application use cases.

## Scope
- IN: use-case implementations, domain policy application, capacity gating.
- OUT: billing posting and event publishing integration.

## Pricing Rules (approved)
- First 30 minutes free.
- After 30 minutes: fixed hourly tariff (basePrice), rounded up.
- Dynamic multiplier at ENTRY by occupancy bracket (upper-bound inclusive):
  - `<=25%`: -10%
  - `<=50%`: 0%
  - `<=75%`: +10%
  - `<=100%`: +25%

## TDD Cycle (strict)
1. RED: rule-based tests per pricing and occupancy branch.
2. GREEN: minimum rule engine implementation.
3. REFACTOR: extract policy/value objects.

## Implementation Checklist
- [ ] Implement `*UseCaseImpl` in `application/service/<feature>/` only.
- [ ] Encode pricing and occupancy rules as deterministic domain logic.
- [ ] Enforce full-capacity ENTRY rejection.
- [ ] Add test matrix for boundary minutes and occupancy percentages.

## Acceptance Criteria
- [ ] Boundary tests pass (30m, 31m, 60m, 61m).
- [ ] Occupancy bracket tests pass at exact boundaries 25/50/75/100.
- [ ] Full occupancy blocks new ENTRY.
- [ ] Domain coverage remains 100%.

## QA Scenarios
Scenario: occupancy boundary pricing
- Tool: `./gradlew test --tests "*Pricing*"`
- Expected: each exact boundary maps to approved multiplier.

Scenario: 100% occupancy ENTRY
- Tool: `./gradlew test --tests "*Entry*"`
- Expected: no spot allocated; logical rejection path executed.

## Git Workflow
- Branch: `feat(task-4)-core-use-cases-and-pricing`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-4): core-use-cases-and-pricing`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 4 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-4-core-use-cases-and-pricing.md`
