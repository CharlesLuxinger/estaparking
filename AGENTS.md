# ESTAPARKING BACKEND — KNOWLEDGE BASE

## OVERVIEW

Spring Boot 3.5 / Kotlin 2.3 / Java 25 REST API. Target architecture: Hexagonal (Ports & Adapters) + DDD.

**Package root:** `com.charlesluxinger.estaparking`  
**Entry point:** `src/main/kotlin/com/charlesluxinger/estaparking/Application.kt`

> **Status:** Early scaffold. Only `Application.kt` and one smoke test exist. No features are implemented yet.

## PLANNED ARCHITECTURE (enforce from day one)

| Layer | Package | Rule |
|-------|---------|------|
| Domain | `domain/` | Zero Spring annotations. No `@Service`, `@Component`, `@Transactional`. Exception: `domain/config/` |
| Infrastructure | `infra/` | Implements outbound ports. Depends on domain — never the other way |
| Controllers | `infra/client/<feature>/` | Named `<Feature>ControllerV1`. Increment suffix on breaking changes |
| Inbound ports | `domain/port/inbound/` | Interfaces: `<Feature>QueryPort`, `<Feature>CommandPort` |
| Outbound ports | `domain/port/outbound/` | Interfaces: `<Feature>RepositoryPort`, `<Feature>ClientPort` |
| Adapters | `infra/` | Named `<Feature><Tech>Adapter` (e.g. `ParkingQueryPortJPAAdapter`). **Never** `*PortImpl` |
| Use case impls | `domain/application/service/<feature>/` | Named `<UseCase>Impl` |
| Commands/Queries | `domain/port/inbound/<feature>/model/` | Named `<Action><Domain>Command` / `<Action><Domain>Query` |
| DTOs / view models | `domain/port/inbound/<feature>/model/` | **Never** inline in service files |

**Dependency direction:** `infra/` → `domain/`. Violations are build failures (detekt enforces this).

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

## MCP TOOLS: code-review-graph

This project has a knowledge graph. **Use graph tools before Grep/Glob/Read.**

| Tool | Use when |
|------|----------|
| `semantic_search_nodes` or `query_graph` | Exploring code instead of Grep |
| `get_impact_radius` | Understanding blast radius of a change |
| `detect_changes` + `get_review_context` | Code review (token-efficient) |
| `query_graph` callers_of/callees_of/imports_of/tests_for | Tracing relationships |
| `get_architecture_overview` + `list_communities` | Architecture questions |
| `refactor_tool` | Planning renames, finding dead code |

The graph auto-updates on file changes. Fall back to Grep/Glob/Read only when the graph doesn't cover the need.

## CI

Triggers on PR and push to `main`. Steps: `ktlintMainSourceSetCheck ktlintTestSourceSetCheck` → `detekt` → `test jacocoTestReport`. Coverage badges are auto-committed to `badges/` on push to `main` (skip CI commit). Coverage is posted as a PR summary comment.