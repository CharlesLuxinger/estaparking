# Graph Report - estaparking  (2026-05-09)

## Corpus Check
- 26 files · ~7,804 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 151 nodes · 210 edges · 17 communities (8 shown, 9 thin omitted)
- Extraction: 71% EXTRACTED · 29% INFERRED · 0% AMBIGUOUS · INFERRED: 60 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2e602865`
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
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]

## God Nodes (most connected - your core abstractions)
1. `Vehicle` - 23 edges
2. `SpotTest` - 16 edges
3. `ParkingTest` - 15 edges
4. `Spot` - 14 edges
5. `Parking` - 12 edges
6. `Coordinates` - 9 edges
7. `Garage` - 8 edges
8. `GarageTest` - 8 edges
9. `ParkingDomainErrorTest` - 7 edges
10. `Architecture Governance Checks` - 7 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities (17 total, 9 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.15
Nodes (4): Coordinates, Spot, SpotTest, Vehicle

### Community 1 - "Community 1"
Cohesion: 0.1
Nodes (5): ArchitectureScaffoldTest, ArchitectureTest, ControllerBoundaryTest, DependencyDirectionTest, UseCaseOwnershipTest

### Community 3 - "Community 3"
Cohesion: 0.14
Nodes (13): CI, CODE QUALITY GATES, code:bash (# 1. Lint (checks main + test source sets)), COMMANDS, graphify, Key Tools, MCP Tools: code-review-graph, NAMING (+5 more)

### Community 5 - "Community 5"
Cohesion: 0.2
Nodes (10): Architecture Governance Checks, code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (grep -r "import.*\.repository\|\.adapter" src/main/kotlin/co), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (# Check if UseCaseImpl exists outside application/service), code:powershell (Get-ChildItem -Path "src/main/kotlin" -Recurse -Filter "*Use), Governance Rules (+2 more)

### Community 6 - "Community 6"
Cohesion: 0.25
Nodes (7): ExitBeforeEntry, FullOccupancyEntryDenied, InvalidExitOrdering, InvalidParkedOrdering, ParkingDomainError, VehicleNotFoundForTransition, WrongVehicleTransitionAttempt

### Community 8 - "Community 8"
Cohesion: 0.33
Nodes (5): DomainResult, Err, Error, Ok, Success

## Knowledge Gaps
- **36 isolated node(s):** `Application`, `DomainResult`, `Success`, `Error`, `ParkingDomainError` (+31 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Vehicle` connect `Community 0` to `Community 9`, `Community 2`?**
  _High betweenness centrality (0.065) - this node is a cross-community bridge._
- **Why does `Spot` connect `Community 0` to `Community 2`?**
  _High betweenness centrality (0.033) - this node is a cross-community bridge._
- **Are the 22 inferred relationships involving `Vehicle` (e.g. with `.`apply ENTRY PARKED EXIT succeeds and returns spot to AVAILABLE`()` and `.`apply ENTRY when parking is full returns FullOccupancyEntryDenied`()`) actually correct?**
  _`Vehicle` has 22 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `Spot` (e.g. with `.createSpot()` and `.`constructor with blank id throws exception`()`) actually correct?**
  _`Spot` has 8 INFERRED edges - model-reasoned connections that need verification._
- **Are the 6 inferred relationships involving `Parking` (e.g. with `.`constructor with valid id and name succeeds`()` and `.`constructor with blank id throws exception`()`) actually correct?**
  _`Parking` has 6 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Application`, `DomainResult`, `Success` to the rest of the system?**
  _36 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._