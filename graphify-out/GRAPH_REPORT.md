# Graph Report - estaparking  (2026-05-11)

## Corpus Check
- 93 files · ~14,930 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 485 nodes · 697 edges · 52 communities (18 shown, 34 thin omitted)
- Extraction: 68% EXTRACTED · 32% INFERRED · 0% AMBIGUOUS · INFERRED: 226 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `567a2e3a`
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
- [[_COMMUNITY_Community 38|Community 38]]
- [[_COMMUNITY_Community 39|Community 39]]
- [[_COMMUNITY_Community 40|Community 40]]
- [[_COMMUNITY_Community 41|Community 41]]
- [[_COMMUNITY_Community 42|Community 42]]
- [[_COMMUNITY_Community 43|Community 43]]
- [[_COMMUNITY_Community 44|Community 44]]
- [[_COMMUNITY_Community 45|Community 45]]
- [[_COMMUNITY_Community 46|Community 46]]
- [[_COMMUNITY_Community 47|Community 47]]
- [[_COMMUNITY_Community 48|Community 48]]
- [[_COMMUNITY_Community 49|Community 49]]

## God Nodes (most connected - your core abstractions)
1. `Vehicle` - 38 edges
2. `Spot` - 28 edges
3. `Coordinates` - 25 edges
4. `Parking` - 21 edges
5. `SpotTest` - 18 edges
6. `Error` - 15 edges
7. `ParkingTest` - 15 edges
8. `HandleWebhookEventUseCaseImpl` - 14 edges
9. `Garage` - 13 edges
10. `Success` - 13 edges

## Surprising Connections (you probably didn't know these)
- `toCommand()` --calls--> `WebhookEventCommand`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/webhook/WebhookEventRequest.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/port/inbound/webhook/model/WebhookEventCommand.kt
- `toDomainGarages()` --calls--> `Success`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/dto/SimulatorGarageItem.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/result/DomainResult.kt
- `toDomainSpots()` --calls--> `Success`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/dto/SimulatorSpotItem.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/result/DomainResult.kt
- `toDomainSpots()` --calls--> `Coordinates`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/SimulatorGarageMapper.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/spot/Coordinates.kt
- `toDomainSpots()` --calls--> `Spot`  [INFERRED]
  src/main/kotlin/com/charlesluxinger/estaparking/infra/client/simulator/garage/SimulatorGarageMapper.kt → src/main/kotlin/com/charlesluxinger/estaparking/domain/spot/Spot.kt

## Communities (52 total, 34 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.06
Nodes (27): DomainResult, Err, Error, flatMap(), map(), Ok, Success, ExitBeforeEntry (+19 more)

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (14): SimulatorSpotItem, toDomainSpots(), EntryUseCaseImplTest, ParkedUseCaseImplTest, fromDomain(), ParkingSessionEntity, ParkingSpotSnapshotEntity, PersistenceRepositoryIntegrationTest (+6 more)

### Community 2 - "Community 2"
Cohesion: 0.08
Nodes (14): fromDomain(), GarageEntity, SimulatorGarageItem, toDomainGarages(), SimulatorGarageResponse, SimulatorGarageSnapshot, Garage, GarageTest (+6 more)

### Community 3 - "Community 3"
Cohesion: 0.13
Nodes (7): fromDomain(), ParkingEventEntity, StoredParkingEvent, WebhookEventCommand, HandleWebhookEventUseCaseImpl, HandleWebhookEventUseCaseImplTest, MockPorts

### Community 4 - "Community 4"
Cohesion: 0.1
Nodes (5): ArchitectureScaffoldTest, ArchitectureTest, ControllerBoundaryTest, DependencyDirectionTest, UseCaseOwnershipTest

### Community 5 - "Community 5"
Cohesion: 0.17
Nodes (3): ParkingTest, Vehicle, VehicleTest

### Community 6 - "Community 6"
Cohesion: 0.08
Nodes (23): Architecture Governance Checks, CI, CODE QUALITY GATES, code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (grep -r "import.*\.repository\|\.adapter" src/main/kotlin/co), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (# Check if UseCaseImpl exists outside application/service) (+15 more)

### Community 7 - "Community 7"
Cohesion: 0.12
Nodes (6): RevenueQueryResponse, GetRevenueQueryUseCaseImpl, RevenueConfig, RecordingRevenueQueryPort, RevenueControllerV1IT, TestConfig

### Community 8 - "Community 8"
Cohesion: 0.19
Nodes (6): SimulatorGarageRestTemplateAdapter, SimulatorGarageRestTemplateAdapterTest, PayloadMappingFailure, SimulatorGarageClientError, TransportFailure, UnexpectedStatus

### Community 9 - "Community 9"
Cohesion: 0.22
Nodes (3): RecordingWebhookEventCommandPort, TestConfig, WebhookControllerV1IT

### Community 10 - "Community 10"
Cohesion: 0.18
Nodes (3): EntryUseCaseImpl, ParkedUseCaseImpl, WebhookConfig

### Community 12 - "Community 12"
Cohesion: 0.27
Nodes (4): toCommand(), toEventType(), WebhookEventRequest, WebhookEventRequestTest

### Community 15 - "Community 15"
Cohesion: 0.29
Nodes (5): PayloadMappingFailure, SimulatorGarageClientError, SimulatorGarageClientPort, TransportFailure, UnexpectedStatus

### Community 16 - "Community 16"
Cohesion: 0.33
Nodes (5): IgnoredDuplicate, NotFound, Processed, RejectedTransition, WebhookEventOutcome

### Community 17 - "Community 17"
Cohesion: 0.4
Nodes (3): BillingTransaction, BillingTransactionEntity, fromDomain()

### Community 33 - "Community 33"
Cohesion: 0.5
Nodes (3): SimulatorGarageItem, SimulatorGarageResponse, SimulatorSpotItem

## Knowledge Gaps
- **48 isolated node(s):** `Application`, `ParkingDomainError`, `FullOccupancyEntryDenied`, `VehicleNotFoundForTransition`, `EventType` (+43 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **34 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Vehicle` connect `Community 5` to `Community 0`, `Community 1`, `Community 3`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `Error` connect `Community 0` to `Community 8`, `Community 2`?**
  _High betweenness centrality (0.073) - this node is a cross-community bridge._
- **Why does `Spot` connect `Community 1` to `Community 0`, `Community 2`, `Community 3`, `Community 5`?**
  _High betweenness centrality (0.066) - this node is a cross-community bridge._
- **Are the 37 inferred relationships involving `Vehicle` (e.g. with `.handle()` and `.`execute returns full occupancy error when no entry is possible`()`) actually correct?**
  _`Vehicle` has 37 INFERRED edges - model-reasoned connections that need verification._
- **Are the 22 inferred relationships involving `Spot` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Spot` has 22 INFERRED edges - model-reasoned connections that need verification._
- **Are the 24 inferred relationships involving `Coordinates` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Coordinates` has 24 INFERRED edges - model-reasoned connections that need verification._
- **Are the 15 inferred relationships involving `Parking` (e.g. with `.toDomain()` and `.`execute returns full occupancy error when no entry is possible`()`) actually correct?**
  _`Parking` has 15 INFERRED edges - model-reasoned connections that need verification._