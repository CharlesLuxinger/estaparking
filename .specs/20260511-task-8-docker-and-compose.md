# Task 8 — Docker & Docker Compose

> Source of truth: `.\.sisyphus\plans\estapar-backend-test.md`

## Context
- Reference: `.\.sisyphus\plans\task-7-hardening-and-testing.md`
- Previous: Task 7 strengthened reliability and coverage; this task packages runtime dependencies for reproducible local execution.

## Objective
Provide containerized runtime setup for application + dependencies, including runnable script.

## Scope
- IN: `docker-compose.yml`, Dockerfile adjustments, run script, simulator dependency wiring.
- OUT: cloud deployment.

## TDD/Validation Style
Infrastructure test-first using executable smoke checks (service boots, dependencies reachable).

## Implementation Checklist
- [x] Create `docker-compose.yml` for app + MySQL + simulator dependency flow.
- [x] Provide `run.sh` (and/or Windows companion if needed) to boot stack.
- [x] Ensure app starts with required env vars and DB connectivity.
- [x] Add startup health checks.

## Acceptance Criteria
- [x] `docker compose up` starts required services.
- [x] `./run.sh` starts the stack successfully.
- [x] Webhook and revenue endpoints become reachable.

## QA Scenarios
Scenario: clean boot
- Tool: docker compose + script execution
- Expected: all containers healthy and app accepting requests.

Scenario: DB unavailable at startup
- Tool: compose with DB disabled
- Expected: deterministic failure/retry behavior documented.

## Git Workflow
- Branch: `feat(task-8)-docker-and-compose`
- PR: one PR for this feature.
- Mandatory PR description:
  - Must include a clear summary of what was done.
  - Must include a markdown table with code coverage for all modified files.
- Commit: `feat(task-8): docker-and-compose`

## Pre-Commit Checklist (ordered)
1. `./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck`
2. `./gradlew detekt`
3. `./gradlew test jacocoTestReport`
4. `graphify update .`
5. Oracle check: `Task 8 is complete and ready for commit. Please confirm.`
6. Copy plan file to `.specs/YYYYMMDD-task-8-docker-and-compose.md`