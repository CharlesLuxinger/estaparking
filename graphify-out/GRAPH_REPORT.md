# Graph Report - estaparking  (2026-05-11)

## Corpus Check
- 118 files · ~20,068 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 666 nodes · 1042 edges · 66 communities (19 shown, 47 thin omitted)
- Extraction: 68% EXTRACTED · 32% INFERRED · 0% AMBIGUOUS · INFERRED: 337 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6d9a242a`
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
- [[_COMMUNITY_Community 50|Community 50]]
- [[_COMMUNITY_Community 51|Community 51]]
- [[_COMMUNITY_Community 52|Community 52]]
- [[_COMMUNITY_Community 53|Community 53]]
- [[_COMMUNITY_Community 54|Community 54]]
- [[_COMMUNITY_Community 55|Community 55]]
- [[_COMMUNITY_Community 56|Community 56]]
- [[_COMMUNITY_Community 57|Community 57]]
- [[_COMMUNITY_Community 58|Community 58]]
- [[_COMMUNITY_Community 59|Community 59]]
- [[_COMMUNITY_Community 60|Community 60]]
- [[_COMMUNITY_Community 61|Community 61]]
- [[_COMMUNITY_Community 62|Community 62]]
- [[_COMMUNITY_Community 63|Community 63]]

## God Nodes (most connected - your core abstractions)
1. `Vehicle` - 81 edges
2. `Spot` - 34 edges
3. `Parking` - 33 edges
4. `Coordinates` - 31 edges
5. `WebhookControllerV1IT` - 30 edges
6. `ParkingTest` - 20 edges
7. `SpotTest` - 18 edges
8. `HandleWebhookEventUseCaseImplTest` - 16 edges
9. `PricingPolicyTest` - 16 edges
10. `HandleWebhookEventUseCaseImpl` - 15 edges

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

## Communities (66 total, 47 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.06
Nodes (16): BillingRecord, BillingRecordEntity, fromDomain(), BillingRecordTest, DomainResult, Err, Error, flatMap() (+8 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (14): SimulatorSpotItem, toDomainSpots(), EntryUseCaseImplTest, ParkedUseCaseImplTest, fromDomain(), ParkingSessionEntity, ParkingSpotSnapshotEntity, PersistenceRepositoryIntegrationTest (+6 more)

### Community 2 - "Community 2"
Cohesion: 0.11
Nodes (4): RecordingWebhookEventCommandPort, SpyWebhookEventCommandPort, TestConfig, WebhookControllerV1IT

### Community 3 - "Community 3"
Cohesion: 0.06
Nodes (19): fromDomain(), GarageEntity, SimulatorGarageItem, toDomainGarages(), SimulatorGarageResponse, SimulatorGarageSnapshot, Garage, GarageTest (+11 more)

### Community 4 - "Community 4"
Cohesion: 0.11
Nodes (8): fromDomain(), ParkingEventEntity, ParkingEventTest, StoredParkingEvent, WebhookEventCommand, HandleWebhookEventUseCaseImpl, HandleWebhookEventUseCaseImplTest, MockPorts

### Community 5 - "Community 5"
Cohesion: 0.08
Nodes (20): ExitBeforeEntry, FullOccupancyEntryDenied, InvalidExitOrdering, InvalidParkedOrdering, ParkingDomainError, VehicleNotFoundForTransition, WrongVehicleTransitionAttempt, DomainResult (+12 more)

### Community 6 - "Community 6"
Cohesion: 0.12
Nodes (8): SimulatorGarageKtorAdapter, SimulatorGarageKtorAdapterTest, SimulatorGarageRestTemplateAdapter, SimulatorGarageRestTemplateAdapterTest, PayloadMappingFailure, SimulatorGarageClientError, TransportFailure, UnexpectedStatus

### Community 7 - "Community 7"
Cohesion: 0.1
Nodes (7): RevenueQueryResponse, RevenueQueryResponseTest, GetRevenueQueryUseCaseImpl, RevenueConfig, RecordingRevenueQueryPort, RevenueControllerV1IT, TestConfig

### Community 8 - "Community 8"
Cohesion: 0.09
Nodes (25): Architecture Governance Checks, CI, CODE QUALITY GATES, code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (grep -r "import.*\.repository\|\.adapter" src/main/kotlin/co), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (# Check if UseCaseImpl exists outside application/service) (+17 more)

### Community 9 - "Community 9"
Cohesion: 0.1
Nodes (5): ArchitectureScaffoldTest, ArchitectureTest, ControllerBoundaryTest, DependencyDirectionTest, UseCaseOwnershipTest

### Community 10 - "Community 10"
Cohesion: 0.18
Nodes (4): PricingSnapshot, fromDomain(), PricingSnapshotEntity, PricingSnapshotTest

### Community 12 - "Community 12"
Cohesion: 0.22
Nodes (4): BillingTransaction, BillingTransactionEntity, fromDomain(), BillingTransactionTest

### Community 13 - "Community 13"
Cohesion: 0.18
Nodes (3): EntryUseCaseImpl, ParkedUseCaseImpl, WebhookConfig

### Community 15 - "Community 15"
Cohesion: 0.27
Nodes (4): toCommand(), toEventType(), WebhookEventRequest, WebhookEventRequestTest

### Community 17 - "Community 17"
Cohesion: 0.25
Nodes (3): RevenueQueryRequest, RevenueQueryRequestTest, RevenueRequest

### Community 18 - "Community 18"
Cohesion: 0.33
Nodes (5): IgnoredDuplicate, NotFound, Processed, RejectedTransition, WebhookEventOutcome

### Community 40 - "Community 40"
Cohesion: 0.5
Nodes (3): SimulatorGarageItem, SimulatorGarageResponse, SimulatorSpotItem

## Knowledge Gaps
- **49 isolated node(s):** `Application`, `Currency`, `ParkingDomainError`, `FullOccupancyEntryDenied`, `VehicleNotFoundForTransition` (+44 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **47 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Vehicle` connect `Community 0` to `Community 1`, `Community 4`, `Community 7`, `Community 10`, `Community 12`, `Community 15`, `Community 19`?**
  _High betweenness centrality (0.171) - this node is a cross-community bridge._
- **Why does `Error` connect `Community 5` to `Community 0`, `Community 3`, `Community 6`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Why does `Spot` connect `Community 1` to `Community 0`, `Community 3`, `Community 4`, `Community 5`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Are the 80 inferred relationships involving `Vehicle` (e.g. with `.toCommand()` and `.toDomain()`) actually correct?**
  _`Vehicle` has 80 INFERRED edges - model-reasoned connections that need verification._
- **Are the 28 inferred relationships involving `Spot` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Spot` has 28 INFERRED edges - model-reasoned connections that need verification._
- **Are the 26 inferred relationships involving `Parking` (e.g. with `.toDomain()` and `.`execute returns full occupancy error when no entry is possible`()`) actually correct?**
  _`Parking` has 26 INFERRED edges - model-reasoned connections that need verification._
- **Are the 30 inferred relationships involving `Coordinates` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Coordinates` has 30 INFERRED edges - model-reasoned connections that need verification._