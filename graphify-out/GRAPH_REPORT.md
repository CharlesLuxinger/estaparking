# Graph Report - estaparking  (2026-05-09)

## Corpus Check
- 11 files · ~5,168 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 55 nodes · 54 edges · 10 communities (5 shown, 5 thin omitted)
- Extraction: 83% EXTRACTED · 17% INFERRED · 0% AMBIGUOUS · INFERRED: 9 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `62ed170a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]

## God Nodes (most connected - your core abstractions)
1. `Architecture Governance Checks` - 7 edges
2. `ArchitectureScaffoldTest` - 5 edges
3. `DependencyDirectionTest` - 4 edges
4. `UseCaseOwnershipTest` - 4 edges
5. `PLANNED ARCHITECTURE (enforce from day one)` - 4 edges
6. `MCP Tools: code-review-graph` - 4 edges
7. `ArchitectureTest` - 3 edges
8. `ControllerBoundaryTest` - 3 edges
9. `COMMANDS` - 2 edges
10. `Application` - 1 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities (10 total, 5 thin omitted)

### Community 1 - "Community 1"
Cohesion: 0.2
Nodes (10): Architecture Governance Checks, code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (grep -r "import.*\.repository\|\.adapter" src/main/kotlin/co), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (# Check if UseCaseImpl exists outside application/service), code:powershell (Get-ChildItem -Path "src/main/kotlin" -Recurse -Filter "*Use), Governance Rules (+2 more)

### Community 2 - "Community 2"
Cohesion: 0.2
Nodes (9): CI, CODE QUALITY GATES, code:bash (# 1. Lint (checks main + test source sets)), COMMANDS, graphify, NAMING, OVERVIEW, SKILLS (+1 more)

### Community 5 - "Community 5"
Cohesion: 0.5
Nodes (4): Key Tools, MCP Tools: code-review-graph, When to use graph tools FIRST, Workflow

## Knowledge Gaps
- **21 isolated node(s):** `Application`, `OVERVIEW`, `Governance Rules`, `code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com)`, `code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki)` (+16 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `PLANNED ARCHITECTURE (enforce from day one)` connect `Community 1` to `Community 2`?**
  _High betweenness centrality (0.099) - this node is a cross-community bridge._
- **Why does `ArchitectureTest` connect `Community 3` to `Community 0`?**
  _High betweenness centrality (0.071) - this node is a cross-community bridge._
- **What connects `Application`, `OVERVIEW`, `Governance Rules` to the rest of the system?**
  _21 weakly-connected nodes found - possible documentation gaps or missing edges._