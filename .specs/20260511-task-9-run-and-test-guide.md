# Task 9 — How to Run and Test

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `.\.sisyphus\plans\task-8-docker-and-compose.md`
- Previous: Task 8 added compose/script runtime setup; this task documents execution and validation steps.

## Objective
Create a comprehensive execution guide for local run, simulator integration, and test execution.

## Scope
- IN: step-by-step commands, prerequisites, common troubleshooting.
- OUT: deep architecture narrative (handled in docs task).

## Implementation Checklist
- [x] Document prerequisites (JDK, Docker, ports, Gradle).
- [x] Document startup flow with simulator and compose/script.
- [x] Document webhook testing flow and revenue query examples.
- [x] Document CI-equivalent local validation command sequence.

## Acceptance Criteria
- [x] A new contributor can run app + tests without ad-hoc guidance.
- [x] Commands match actual project scripts/config.
- [x] Includes expected outputs and failure recovery hints.

## QA Scenarios
Scenario: fresh machine path
- Tool: follow guide exactly
- Expected: app runs and tests pass.

Scenario: wrong env config
- Tool: intentional misconfiguration
- Expected: guide’s troubleshooting resolves issue.

## Git Workflow
- Branch: `feat(task-9)-run-and-test-guide`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-9): run-and-test-guide`

## Pre-Commit Checklist (ordered)
1. ✅ `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. ✅ `./gradlew detekt`
3. ✅ `./gradlew test jacocoTestReport`
4. ✅ `graphify update .`
5. ✅ Oracle check: `Task 9 is complete and ready for commit.`
6. ✅ Copy plan file to `.specs/YYYYMMDD-task-9-run-and-test-guide.md`
