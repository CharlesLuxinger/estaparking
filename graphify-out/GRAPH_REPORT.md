# Graph Report - estaparking  (2026-05-11)

## Corpus Check
- 121 files · ~22,136 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 762 nodes · 1163 edges · 76 communities (28 shown, 48 thin omitted)
- Extraction: 69% EXTRACTED · 31% INFERRED · 0% AMBIGUOUS · INFERRED: 366 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ad8c30b3`
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
- [[_COMMUNITY_Community 64|Community 64]]
- [[_COMMUNITY_Community 65|Community 65]]
- [[_COMMUNITY_Community 66|Community 66]]
- [[_COMMUNITY_Community 67|Community 67]]
- [[_COMMUNITY_Community 68|Community 68]]
- [[_COMMUNITY_Community 69|Community 69]]
- [[_COMMUNITY_Community 70|Community 70]]
- [[_COMMUNITY_Community 71|Community 71]]
- [[_COMMUNITY_Community 72|Community 72]]
- [[_COMMUNITY_Community 73|Community 73]]

## God Nodes (most connected - your core abstractions)
1. `Vehicle` - 83 edges
2. `Spot` - 38 edges
3. `Parking` - 35 edges
4. `Coordinates` - 35 edges
5. `WebhookControllerV1IT` - 30 edges
6. `ParkingTest` - 20 edges
7. `HandleWebhookEventUseCaseImplTest` - 18 edges
8. `SpotTest` - 18 edges
9. `HandleWebhookEventUseCaseImpl` - 17 edges
10. `Garage` - 16 edges

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

## Communities (76 total, 48 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.06
Nodes (15): BillingTransaction, BillingTransactionEntity, fromDomain(), BillingTransactionTest, fromDomain(), ParkingEventEntity, ParkingEventTest, StoredParkingEvent (+7 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (27): DomainResult, Err, Error, flatMap(), map(), Ok, Success, ExitBeforeEntry (+19 more)

### Community 2 - "Community 2"
Cohesion: 0.07
Nodes (12): SimulatorSpotItem, toDomainSpots(), EntryUseCaseImplTest, ParkedUseCaseImplTest, fromDomain(), ParkingSessionEntity, ParkingSpotSnapshotEntity, PersistenceRepositoryIntegrationTest (+4 more)

### Community 3 - "Community 3"
Cohesion: 0.06
Nodes (20): fromDomain(), GarageEntity, SimulatorGarageItem, toDomainGarages(), SimulatorGarageResponse, SimulatorGarageSnapshot, SimulatorGarageSnapshotTest, Garage (+12 more)

### Community 4 - "Community 4"
Cohesion: 0.11
Nodes (4): RecordingWebhookEventCommandPort, SpyWebhookEventCommandPort, TestConfig, WebhookControllerV1IT

### Community 5 - "Community 5"
Cohesion: 0.11
Nodes (8): SimulatorGarageKtorAdapter, SimulatorGarageKtorAdapterTest, SimulatorGarageRestTemplateAdapter, SimulatorGarageRestTemplateAdapterTest, PayloadMappingFailure, SimulatorGarageClientError, TransportFailure, UnexpectedStatus

### Community 6 - "Community 6"
Cohesion: 0.1
Nodes (7): RevenueQueryResponse, RevenueQueryResponseTest, GetRevenueQueryUseCaseImpl, RevenueConfig, RecordingRevenueQueryPort, RevenueControllerV1IT, TestConfig

### Community 7 - "Community 7"
Cohesion: 0.09
Nodes (25): Architecture Governance Checks, CI, CODE QUALITY GATES, code:bash (grep -r "import.*\.application\|\.infra" src/main/kotlin/com), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (grep -r "import.*\.repository\|\.adapter" src/main/kotlin/co), code:powershell (if (Test-Path "src/main/kotlin/com/charlesluxinger/estaparki), code:bash (# Check if UseCaseImpl exists outside application/service) (+17 more)

### Community 8 - "Community 8"
Cohesion: 0.1
Nodes (5): ArchitectureScaffoldTest, ArchitectureTest, ControllerBoundaryTest, DependencyDirectionTest, UseCaseOwnershipTest

### Community 10 - "Community 10"
Cohesion: 0.18
Nodes (4): PricingSnapshot, fromDomain(), PricingSnapshotEntity, PricingSnapshotTest

### Community 12 - "Community 12"
Cohesion: 0.19
Nodes (4): BillingRecord, BillingRecordEntity, fromDomain(), BillingRecordTest

### Community 13 - "Community 13"
Cohesion: 0.13
Nodes (15): code:bash (curl -X POST http://localhost:3003/webhook \), code:bash (curl -X POST http://localhost:3003/webhook \), code:bash (# Revenue for a specific sector and date range), code:json ({), code:bash (# 1. Entry), code:bash (# Direct simulator call (from host)), code:bash (curl -X POST http://localhost:3003/webhook \), Complete Flow Test (+7 more)

### Community 14 - "Community 14"
Cohesion: 0.18
Nodes (4): fromDomain(), SpotEntity, SpotJpaAdapter, SpotJpaAdapterTest

### Community 15 - "Community 15"
Cohesion: 0.15
Nodes (13): Build Failures, code:bash (# Find what's using port 8080), code:bash (docker compose ps mysql), code:bash (docker compose logs mysql), code:bash (docker compose ps simulator), code:bash (docker compose logs simulator), code:bash (curl http://localhost:3000/garage), code:bash (./gradlew clean --refresh-dependencies) (+5 more)

### Community 16 - "Community 16"
Cohesion: 0.18
Nodes (3): EntryUseCaseImpl, ParkedUseCaseImpl, WebhookConfig

### Community 18 - "Community 18"
Cohesion: 0.2
Nodes (10): 1. Format and Lint Check, 2. Static Analysis, 3. Tests with Coverage, code:bash (./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck), code:bash (./gradlew detekt), code:bash (./gradlew test jacocoTestReport), code:bash (# Run all gates in order (matches CI)), code:powershell (.\gradlew.bat ktlintMainSourceSetCheck ktlintTestSourceSetCh) (+2 more)

### Community 20 - "Community 20"
Cohesion: 0.25
Nodes (3): RevenueQueryRequest, RevenueQueryRequestTest, RevenueRequest

### Community 21 - "Community 21"
Cohesion: 0.31
Nodes (4): toCommand(), toEventType(), WebhookEventRequest, WebhookEventRequestTest

### Community 22 - "Community 22"
Cohesion: 0.25
Nodes (8): Application Startup, code:bash (# Start all services), code:bash (# Build without tests), code:bash (# Health check (should return 200 OK with empty or valid JSO), Docker Compose (Recommended), Gradle Commands, Startup Sequence (Docker Compose), Verify Application is Running

### Community 23 - "Community 23"
Cohesion: 0.25
Nodes (7): code:bash (# Check Java version), Port Requirements, Prerequisites, Required Tools, Run and Test Guide, Table of Contents, Verify Prerequisites

### Community 24 - "Community 24"
Cohesion: 0.29
Nodes (7): code:bash (# Start the entire stack (MySQL + Simulator + App)), code:block3 (NAME                    IMAGE                          STATU), code:bash (# Start MySQL (example using Docker)), Environment Setup, Environment Variables, Option A: Using Docker Compose (Recommended), Option B: Local Development with External Dependencies

### Community 26 - "Community 26"
Cohesion: 0.33
Nodes (5): IgnoredDuplicate, NotFound, Processed, RejectedTransition, WebhookEventOutcome

### Community 33 - "Community 33"
Cohesion: 0.4
Nodes (5): code:bash (docker ps  # Verify Docker is running), code:bash (# Kill any leftover processes), code:bash (# Clean volumes between runs), code:bash (# Export test variables), Tests Failing

### Community 34 - "Community 34"
Cohesion: 0.4
Nodes (5): API Endpoints Summary, code:bash (# Start everything), Common Commands, File Locations, Quick Reference

### Community 48 - "Community 48"
Cohesion: 0.5
Nodes (4): App Container Health Check Fails, code:bash (docker compose logs app), code:bash (docker compose exec app printenv SPRING_DATASOURCE_URL), code:yaml (healthcheck:)

### Community 50 - "Community 50"
Cohesion: 0.5
Nodes (3): SimulatorGarageItem, SimulatorGarageResponse, SimulatorSpotItem

## Knowledge Gaps
- **91 isolated node(s):** `Application`, `Currency`, `ParkingDomainError`, `FullOccupancyEntryDenied`, `VehicleNotFoundForTransition` (+86 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **48 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Vehicle` connect `Community 0` to `Community 2`, `Community 6`, `Community 9`, `Community 10`, `Community 12`, `Community 25`?**
  _High betweenness centrality (0.138) - this node is a cross-community bridge._
- **Why does `Error` connect `Community 1` to `Community 9`, `Community 3`, `Community 5`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Why does `Spot` connect `Community 2` to `Community 0`, `Community 1`, `Community 3`, `Community 9`, `Community 14`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Are the 82 inferred relationships involving `Vehicle` (e.g. with `.toCommand()` and `.toDomain()`) actually correct?**
  _`Vehicle` has 82 INFERRED edges - model-reasoned connections that need verification._
- **Are the 32 inferred relationships involving `Spot` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Spot` has 32 INFERRED edges - model-reasoned connections that need verification._
- **Are the 28 inferred relationships involving `Parking` (e.g. with `.toDomain()` and `.`execute returns full occupancy error when no entry is possible`()`) actually correct?**
  _`Parking` has 28 INFERRED edges - model-reasoned connections that need verification._
- **Are the 34 inferred relationships involving `Coordinates` (e.g. with `toDomainSpots()` and `.toDomain()`) actually correct?**
  _`Coordinates` has 34 INFERRED edges - model-reasoned connections that need verification._