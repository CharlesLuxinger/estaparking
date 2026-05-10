# Graph Report - estaparking  (2026-05-09)

## Corpus Check
- 47 files · ~9,813 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 233 nodes · 304 edges · 30 communities (10 shown, 20 thin omitted)
- Extraction: 72% EXTRACTED · 28% INFERRED · 0% AMBIGUOUS · INFERRED: 85 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ba498a10`
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
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]

## God Nodes (most connected - your core abstractions)
1. `Vehicle` - 29 edges
2. `Spot` - 18 edges
3. `SpotTest` - 17 edges
4. `Parking` - 15 edges
5. `Coordinates` - 15 edges
6. `ParkingTest` - 15 edges
7. `Garage` - 9 edges
8. `VehicleTest` - 9 edges
9. `GarageTest` - 8 edges
10. `ParkingDomainErrorTest` - 7 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities (30 total, 20 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.12
Nodes (4): Coordinates, CoordinatesTest, Spot, SpotTest

### Community 1 - "Community 1"
Cohesion: 0.1
Nodes (5): ArchitectureScaffoldTest, ArchitectureTest, ControllerBoundaryTest, DependencyDirectionTest, UseCaseOwnershipTest

### Community 2 - "Community 2"
Cohesion: 0.17
Nodes (3): ParkingTest, Vehicle, VehicleTest

### Community 3 - "Community 3"
Cohesion: 0.08
Nodes (23): Architecture Governance Checks, CI, CODE QUALITY GATES, code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (grep -r "import.*\.repository\|\.adapter" src/main/kotlin/co), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (# Check if UseCaseImpl exists outside application/service) (+15 more)

### Community 4 - "Community 4"
Cohesion: 0.12
Nodes (9): DomainResult, Err, Error, Ok, Success, Parking, fromDomain(), ParkingSessionEntity (+1 more)

### Community 5 - "Community 5"
Cohesion: 0.2
Nodes (4): fromDomain(), GarageEntity, Garage, GarageTest

### Community 6 - "Community 6"
Cohesion: 0.2
Nodes (4): fromDomain(), ParkingEventEntity, StoredParkingEvent, PersistenceRepositoryIntegrationTest

### Community 7 - "Community 7"
Cohesion: 0.25
Nodes (7): ExitBeforeEntry, FullOccupancyEntryDenied, InvalidExitOrdering, InvalidParkedOrdering, ParkingDomainError, VehicleNotFoundForTransition, WrongVehicleTransitionAttempt

## Knowledge Gaps
- **35 isolated node(s):** `Application`, `DomainResult`, `Success`, `ParkingDomainError`, `FullOccupancyEntryDenied` (+30 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **20 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Vehicle` connect `Community 2` to `Community 0`, `Community 4`, `Community 6`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Why does `Parking` connect `Community 4` to `Community 2`, `Community 6`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Why does `Spot` connect `Community 0` to `Community 16`, `Community 2`, `Community 4`, `Community 6`?**
  _High betweenness centrality (0.035) - this node is a cross-community bridge._
- **Are the 28 inferred relationships involving `Vehicle` (e.g. with `.`apply ENTRY PARKED EXIT succeeds and returns spot to AVAILABLE`()` and `.`apply ENTRY when parking is full returns FullOccupancyEntryDenied`()`) actually correct?**
  _`Vehicle` has 28 INFERRED edges - model-reasoned connections that need verification._
- **Are the 12 inferred relationships involving `Spot` (e.g. with `.toDomain()` and `.toDomain()`) actually correct?**
  _`Spot` has 12 INFERRED edges - model-reasoned connections that need verification._
- **Are the 9 inferred relationships involving `Parking` (e.g. with `.toDomain()` and `.`constructor with valid id and name succeeds`()`) actually correct?**
  _`Parking` has 9 INFERRED edges - model-reasoned connections that need verification._
- **Are the 14 inferred relationships involving `Coordinates` (e.g. with `.toDomain()` and `.toDomain()`) actually correct?**
  _`Coordinates` has 14 INFERRED edges - model-reasoned connections that need verification._