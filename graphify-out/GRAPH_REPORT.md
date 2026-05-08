# Graph Report - C:/Users/charl/Projetos/estaparking  (2026-05-08)

## Corpus Check
- Corpus is ~3,957 words - fits in a single context window. You may not need a graph.

## Summary
- 17 nodes · 14 edges · 7 communities (5 shown, 2 thin omitted)
- Extraction: 64% EXTRACTED · 36% INFERRED · 0% AMBIGUOUS · INFERRED: 5 edges (avg confidence: 0.83)
- Token cost: 3,200 input · 850 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Spring Boot App Core|Spring Boot App Core]]
- [[_COMMUNITY_Test Suite|Test Suite]]
- [[_COMMUNITY_Runtime Configuration|Runtime Configuration]]
- [[_COMMUNITY_Build and Quality Gates|Build and Quality Gates]]
- [[_COMMUNITY_Architecture and DDD Patterns|Architecture and DDD Patterns]]

## God Nodes (most connected - your core abstractions)
1. `Application (Spring Boot Entry Point)` - 3 edges
2. `build.gradle.kts (Build Configuration)` - 3 edges
3. `AGENTS.md (Architecture Knowledge Base)` - 3 edges
4. `Hexagonal Architecture + DDD Pattern` - 3 edges
5. `ApplicationTests` - 2 edges
6. `detekt.yml (Static Analysis Config)` - 2 edges
7. `Code Quality Gates (ktlint + detekt + jacoco)` - 2 edges
8. `Naming Conventions (Adapters, Ports, Controllers)` - 2 edges
9. `Application` - 1 edges
10. `ApplicationTests (Smoke Test)` - 1 edges

## Surprising Connections (you probably didn't know these)
- `build.gradle.kts (Build Configuration)` --conceptually_related_to--> `Code Quality Gates (ktlint + detekt + jacoco)`  [INFERRED]
  build.gradle.kts → AGENTS.md
- `build.gradle.kts (Build Configuration)` --references--> `Application (Spring Boot Entry Point)`  [INFERRED]
  build.gradle.kts → src/main/kotlin/com/charlesluxinger/estaparking/Application.kt
- `build.gradle.kts (Build Configuration)` --references--> `detekt.yml (Static Analysis Config)`  [EXTRACTED]
  build.gradle.kts → config/detekt/detekt.yml
- `detekt.yml (Static Analysis Config)` --conceptually_related_to--> `Hexagonal Architecture + DDD Pattern`  [INFERRED]
  config/detekt/detekt.yml → AGENTS.md
- `Application (Spring Boot Entry Point)` --references--> `application.yaml (Spring App Config)`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/Application.kt → src/main/resources/application.yaml

## Hyperedges (group relationships)
- **CI Quality Pipeline (ktlint â†’ detekt â†’ test/jacoco)** — build_gradle_BuildConfig, detekt_yml_DetektConfig, rationale_CodeQualityGates [EXTRACTED 0.95]
- **Hexagonal Architecture Enforcement (AGENTS.md + detekt + naming)** — agents_md_ArchitectureKnowledgeBase, detekt_yml_DetektConfig, rationale_HexagonalDDD, rationale_NamingConventions [INFERRED 0.85]

## Communities (7 total, 2 thin omitted)

### Community 2 - "Runtime Configuration"
Cohesion: 0.67
Nodes (3): Application (Spring Boot Entry Point), application.yaml (Spring App Config), ApplicationTests (Smoke Test)

### Community 3 - "Build and Quality Gates"
Cohesion: 0.67
Nodes (3): build.gradle.kts (Build Configuration), detekt.yml (Static Analysis Config), Code Quality Gates (ktlint + detekt + jacoco)

### Community 4 - "Architecture and DDD Patterns"
Cohesion: 1.0
Nodes (3): AGENTS.md (Architecture Knowledge Base), Hexagonal Architecture + DDD Pattern, Naming Conventions (Adapters, Ports, Controllers)

## Knowledge Gaps
- **3 isolated node(s):** `Application`, `ApplicationTests (Smoke Test)`, `application.yaml (Spring App Config)`
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `build.gradle.kts (Build Configuration)` connect `Build and Quality Gates` to `Runtime Configuration`?**
  _High betweenness centrality (0.133) - this node is a cross-community bridge._
- **Why does `Application (Spring Boot Entry Point)` connect `Runtime Configuration` to `Build and Quality Gates`?**
  _High betweenness centrality (0.108) - this node is a cross-community bridge._
- **Why does `detekt.yml (Static Analysis Config)` connect `Build and Quality Gates` to `Architecture and DDD Patterns`?**
  _High betweenness centrality (0.050) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `Application (Spring Boot Entry Point)` (e.g. with `build.gradle.kts (Build Configuration)` and `application.yaml (Spring App Config)`) actually correct?**
  _`Application (Spring Boot Entry Point)` has 2 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `build.gradle.kts (Build Configuration)` (e.g. with `Application (Spring Boot Entry Point)` and `Code Quality Gates (ktlint + detekt + jacoco)`) actually correct?**
  _`build.gradle.kts (Build Configuration)` has 2 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `Hexagonal Architecture + DDD Pattern` (e.g. with `detekt.yml (Static Analysis Config)` and `Naming Conventions (Adapters, Ports, Controllers)`) actually correct?**
  _`Hexagonal Architecture + DDD Pattern` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Application`, `ApplicationTests (Smoke Test)`, `application.yaml (Spring App Config)` to the rest of the system?**
  _3 weakly-connected nodes found - possible documentation gaps or missing edges._