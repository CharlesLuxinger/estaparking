# Graph Report - estaparking  (2026-05-10)

## Corpus Check
- 70 files · ~12,583 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 366 nodes · 505 edges · 40 communities (16 shown, 24 thin omitted)
- Extraction: 70% EXTRACTED · 30% INFERRED · 0% AMBIGUOUS · INFERRED: 150 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `fd93e5ef`
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
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]

## God Nodes (most connected - your core abstractions)
1. `Vehicle` - 31 edges
2. `Spot` - 23 edges
3. `Coordinates` - 20 edges
4. `Parking` - 17 edges
5. `SpotTest` - 17 edges
6. `ParkingTest` - 15 edges
7. `Garage` - 13 edges
8. `HandleWebhookEventUseCaseImpl` - 9 edges
9. `VehicleTest` - 9 edges
10. `WebhookEventCommand` - 8 edges

## Surprising Connections (you probably didn't know these)
- `toDomainGarages()` --calls--> `Garage`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/dto/SimulatorGarageItem.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/garage/Garage.kt
- `toDomainGarages()` --calls--> `Garage`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/SimulatorGarageMapper.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/garage/Garage.kt
- `toCommand()` --calls--> `WebhookEventCommand`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/webhook/WebhookEventRequest.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/port/inbound/webhook/model/WebhookEventCommand.kt
- `toDomainSpots()` --calls--> `Coordinates`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/SimulatorGarageMapper.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/spot/Coordinates.kt
- `toDomainSpots()` --calls--> `Spot`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/SimulatorGarageMapper.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/spot/Spot.kt

## Communities (40 total, 24 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (8): SimulatorSpotItem, toDomainSpots(), PersistenceRepositoryIntegrationTest, Coordinates, Spot, SpotTest, Vehicle, VehicleTest

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (20): SimulatorGarageResponse, SimulatorGarageSnapshot, toDomainGarages(), toDomainSpots(), toSnapshot(), PayloadMappingFailure, SimulatorGarageClientError, SimulatorGarageClientPort (+12 more)

### Community 2 - "Community 2"
Cohesion: 0.11
Nodes (9): DomainResult, Err, Error, flatMap(), map(), Ok, Success, Parking (+1 more)

### Community 3 - "Community 3"
Cohesion: 0.1
Nodes (5): ArchitectureScaffoldTest, ArchitectureTest, ControllerBoundaryTest, DependencyDirectionTest, UseCaseOwnershipTest

### Community 4 - "Community 4"
Cohesion: 0.08
Nodes (23): Architecture Governance Checks, CI, CODE QUALITY GATES, code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (grep -r "import.*\.repository\|\.adapter" src/main/kotlin/co), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (# Check if UseCaseImpl exists outside application/service) (+15 more)

### Community 5 - "Community 5"
Cohesion: 0.15
Nodes (7): fromDomain(), ParkingEventEntity, StoredParkingEvent, WebhookEventCommand, HandleWebhookEventUseCaseImpl, HandleWebhookEventUseCaseImplTest, WebhookConfig

### Community 6 - "Community 6"
Cohesion: 0.2
Nodes (6): SimulatorGarageKtorAdapter, SimulatorGarageKtorAdapterTest, PayloadMappingFailure, SimulatorGarageClientError, TransportFailure, UnexpectedStatus

### Community 7 - "Community 7"
Cohesion: 0.16
Nodes (6): fromDomain(), GarageEntity, SimulatorGarageItem, toDomainGarages(), Garage, GarageTest

### Community 8 - "Community 8"
Cohesion: 0.24
Nodes (3): RecordingWebhookEventCommandPort, TestConfig, WebhookControllerV1IT

### Community 9 - "Community 9"
Cohesion: 0.27
Nodes (4): toCommand(), toEventType(), WebhookEventRequest, WebhookEventRequestTest

### Community 10 - "Community 10"
Cohesion: 0.25
Nodes (7): ExitBeforeEntry, FullOccupancyEntryDenied, InvalidExitOrdering, InvalidParkedOrdering, ParkingDomainError, VehicleNotFoundForTransition, WrongVehicleTransitionAttempt

### Community 12 - "Community 12"
Cohesion: 0.33
Nodes (5): IgnoredDuplicate, NotFound, Processed, RejectedTransition, WebhookEventOutcome

### Community 17 - "Community 17"
Cohesion: 0.6
Nodes (3): fromDomain(), ParkingSessionEntity, ParkingSpotSnapshotEntity

### Community 23 - "Community 23"
Cohesion: 0.5
Nodes (3): SimulatorGarageItem, SimulatorGarageResponse, SimulatorSpotItem

## Knowledge Gaps
- **52 isolated node(s):** `Application`, `DomainResult`, `ParkingDomainError`, `FullOccupancyEntryDenied`, `VehicleNotFoundForTransition` (+47 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **24 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Spot` connect `Community 0` to `Community 1`, `Community 2`, `Community 5`?**
  _High betweenness centrality (0.095) - this node is a cross-community bridge._
- **Why does `Coordinates` connect `Community 0` to `Community 1`, `Community 2`, `Community 5`, `Community 33`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **Are the 30 inferred relationships involving `Vehicle` (e.g. with `.handle()` and `.parkingWithEntryRegistered()`) actually correct?**
  _`Vehicle` has 30 INFERRED edges - model-reasoned connections that need verification._
- **Are the 17 inferred relationships involving `Spot` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Spot` has 17 INFERRED edges - model-reasoned connections that need verification._
- **Are the 19 inferred relationships involving `Coordinates` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Coordinates` has 19 INFERRED edges - model-reasoned connections that need verification._
- **Are the 11 inferred relationships involving `Parking` (e.g. with `.toDomain()` and `.parkingWithAvailableSpot()`) actually correct?**
  _`Parking` has 11 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Application`, `DomainResult`, `ParkingDomainError` to the rest of the system?**
  _52 weakly-connected nodes found - possible documentation gaps or missing edges._