# ESTAPARKING BACKEND — KNOWLEDGE BASE

## OVERVIEW

Spring Boot 3.5 / Kotlin 2.3 / Java 25 REST API. Target architecture: Hexagonal (Ports & Adapters) + DDD.

**Package root:** `com.charlesluxinger.estaparking`
**Entry point:** `src/main/kotlin/com/charlesluxinger/estaparking/Application.kt`

> **Status:** Early scaffold. Only `Application.kt` and one smoke test exist. No features are implemented yet.

## PLANNED ARCHITECTURE (enforce from day one)

| Layer | Package | Rule |
|-------|---------|------|
| Domain | `domain/` | Core business logic (entities, value objects, domain services). Zero Spring annotations. |
| Application | `application/` | Use case implementations (`UseCaseImpl`). Orchestrates domain logic. No framework dependencies. |
| Infrastructure | `infra/` | Framework adapters (persistence, external clients, web controllers). Depends on application. |

### Governance Rules

1. **Strict Dependency Direction:** `infra/` → `application/` → `domain/`.
2. **Controller Boundary:** Web controllers (`infra/client/`) must invoke inbound ports or use cases only. Direct access to repositories or infrastructure adapters is strictly forbidden.
3. **Use Case Ownership:** Use case implementations belong to the application layer. They coordinate domain objects but do not contain core business rules (which belong in the domain).

### Architecture Governance Checks

Use these commands to verify architectural integrity. A match (output) usually indicates a violation unless specified.

1. **Strict Dependency Rule:** Domain must NOT import Application or Infra.
   - **Bash (CI/Linux only):**
     ```bash
     grep -r "import.*\.application\|\.infra" src/main/kotlin/com/charlesluxinger/estaparking/domain
     ```
   - **PowerShell (Windows/local authoritative):**
     ```powershell
     if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparking/domain") { Get-ChildItem -Path "src/main/kotlin/com/charlesluxinger/estaparking/domain" -Recurse -Filter "*.kt" | Select-String -Pattern "import.*\.application", "import.*\.infra" }
     ```

2. **Controller Boundary:** Controllers must NOT import repositories or adapters.
   - **Bash (CI/Linux only):**
     ```bash
     grep -r "import.*\.repository\|\.adapter" src/main/kotlin/com/charlesluxinger/estaparking/infra/client
     ```
   - **PowerShell (Windows/local authoritative):**
     ```powershell
     if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparking/infra/client") { Get-ChildItem -Path "src/main/kotlin/com/charlesluxinger/estaparking/infra/client" -Recurse -Filter "*.kt" | Select-String -Pattern "import.*\.repository", "import.*\.adapter" }
     ```

3. **Use-Case Ownership:** Use cases must be in the application layer.
   - **Bash (CI/Linux only):**
     ```bash
     # Check if UseCaseImpl exists outside application/service
     find src/main/kotlin -name "*UseCaseImpl.kt" | grep -v "application/service"
     ```
   - **PowerShell (Windows/local authoritative):**
     ```powershell
     Get-ChildItem -Path "src/main/kotlin" -Recurse -Filter "*UseCaseImpl.kt" | Where-Object { $_.FullName -notmatch "application\\service" }
     ```

### Package Details

| Component | Package | Rule |
|-----------|---------|------|
| Controllers | `infra/client/<feature>/` | Named `<Feature>ControllerV1`. Only calls application ports. |
| Inbound ports | `domain/port/inbound/` | Interfaces: `<Feature>QueryPort`, `<Feature>CommandPort`. |
| Outbound ports | `domain/port/outbound/` | Interfaces: `<Feature>RepositoryPort`, `<Feature>ClientPort`. |
| Adapters | `infra/` | Named `<Feature><Tech>Adapter` (e.g. `ParkingQueryPortJPAAdapter`). **Never** `*PortImpl`. |
| Use case impls | `application/service/<feature>/` | Named `<UseCase>Impl`. Framework-agnostic orchestration. |
| Commands/Queries | `domain/port/inbound/<feature>/model/` | Named `<Action><Domain>Command` / `<Action><Domain>Query`. |
| DTOs / view models | `domain/port/inbound/<feature>/model/` | **Never** inline in service files. |

## NAMING

- Test files: `*Test.kt` (unit) | `*IT.kt` (integration)
- Specs: `.spec/<YYYYMMDD-title.md>`

## COMMANDS

CI runs these steps in this order — run them locally in the same order:

```bash
# 1. Lint (checks main + test source sets)
./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck

# 2. Static analysis
./gradlew detekt

# 3. Tests + coverage (jacocoTestReport runs automatically after test)
./gradlew test jacocoTestReport

# Full build shortcut (runs all of the above)
./gradlew build
```

> `./gradlew ktlintCheck` also works locally and checks all source sets at once.
> `jacocoTestReport` is wired as a `finalizedBy` on the `test` task — running `test` alone already generates the report.

## CODE QUALITY GATES

| Tool | Config | Hard limit |
|------|--------|-----------|
| ktlint | `.editorconfig` — `ktlint_official` style, 4-space indent, 120-char lines | `ignoreFailures = false` |
| detekt | `config/detekt/detekt.yml` | `ignoreFailures = false` |
| jacoco | `build.gradle.kts` | 90% overall + 90% on changed files (CI enforces both) |

**Detekt limits to know** (non-default values in `detekt.yml`):
- `CyclomaticComplexMethod.allowedComplexity` = 14
- `LongMethod.allowedLines` = 60
- `ReturnCount.max` = 2 (excludes `equals`, excludes return-from-lambda)
- `LongParameterList`: 5 function params, 6 constructor params

**Jacoco exclusions:** `**/config/**`, `**/health/**` are excluded from coverage measurement.

**Detekt baseline:** `config/detekt/baseline.xml` tracks accepted violations. Update it when introducing a justified new violation (`./gradlew detektBaseline`).

## STYLE

Enforced by `.editorconfig` (ktlint reads it automatically):
- `ktlint_official` code style
- `ktlint_standard_package-name` = disabled (package name rule off)
- 120-char max line, 4-space indent, LF line endings

## SKILLS

Load these skills when working on the relevant area:

| Task | Skill to load |
|------|--------------|
| Domain modeling, ports, adapters, use cases, DDD | `clean-ddd-hexagonal` |
| Kotlin idioms, null safety, sealed classes, DSL | `kotlin-patterns` |
| Coroutines, Flow, StateFlow, async patterns | `kotlin-coroutines-flows` |
| Full Kotlin (coroutines + KMP + Compose + Ktor) | `kotlin-specialist` |
| Spring Boot + Kotlin wiring, DI, transactions | `kotlin-springboot` |

Skills live in `.agents/skills/<name>/SKILL.md`. The `clean-ddd-hexagonal` skill includes reference files in `.agents/skills/clean-ddd-hexagonal/references/` covering hexagonal, layers, DDD tactical/strategic, CQRS, and testing patterns.
## CI

Triggers on PR and push to `main`. Steps: `ktlintMainSourceSetCheck ktlintTestSourceSetCheck` → `detekt` → `test jacocoTestReport`. Coverage badges are auto-committed to `badges/` on push to `main` (skip CI commit). Coverage is posted as a PR summary comment.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

Rules:
- ALWAYS read graphify-out/GRAPH_REPORT.md before reading any source files, running grep/glob searches, or answering codebase questions. The graph is your primary map of the codebase.
- IF graphify-out/wiki/index.md EXISTS, navigate it instead of reading raw files
- For cross-module "how does X relate to Y" questions, prefer `graphify query "<question>"`, `graphify path "<A>" "<B>"`, or `graphify explain "<concept>"` over grep — these traverse the graph's EXTRACTED + INFERRED edges instead of scanning files
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).

<!-- code-review-graph MCP tools -->

## MCP Tools: code-review-graph

**IMPORTANT: This project has a knowledge graph. ALWAYS use the
code-review-graph MCP tools BEFORE using Grep/Glob/Read to explore
the codebase.** The graph is faster, cheaper (fewer tokens), and gives
you structural context (callers, dependents, test coverage) that file
scanning cannot.

### When to use graph tools FIRST

- **Exploring code**: `semantic_search_nodes` or `query_graph` instead of Grep
- **Understanding impact**: `get_impact_radius` instead of manually tracing imports
- **Code review**: `detect_changes` + `get_review_context` instead of reading entire files
- **Finding relationships**: `query_graph` with callers_of/callees_of/imports_of/tests_for
- **Architecture questions**: `get_architecture_overview` + `list_communities`

Fall back to Grep/Glob/Read **only** when the graph doesn't cover what you need.

### Key Tools

| Tool | Use when |
| ------ | ---------- |
| `detect_changes` | Reviewing code changes — gives risk-scored analysis |
| `get_review_context` | Need source snippets for review — token-efficient |
| `get_impact_radius` | Understanding blast radius of a change |
| `get_affected_flows` | Finding which execution paths are impacted |
| `query_graph` | Tracing callers, callees, imports, tests, dependencies |
| `semantic_search_nodes` | Finding functions/classes by name or keyword |
| `get_architecture_overview` | Understanding high-level codebase structure |
| `refactor_tool` | Planning renames, finding dead code |

### Workflow

1. The graph auto-updates on file changes (via hooks).
2. Use `detect_changes` for code review.
3. Use `get_affected_flows` to understand impact.
4. Use `query_graph` pattern="tests_for" to check coverage.
