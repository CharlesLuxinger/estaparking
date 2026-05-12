# Run and Test Guide

> Comprehensive execution guide for local run, simulator integration, and test execution.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Application Startup](#application-startup)
4. [Testing the API](#testing-the-api)
5. [Quality Gates (CI-equivalent)](#quality-gates-ci-equivalent)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Tools

| Tool | Version | Purpose |
|------|---------|---------|
| JDK | 21+ | Application runtime |
| Docker | 20.10+ | Container runtime for MySQL and simulator |
| Docker Compose | 2.0+ | Orchestrate multi-container setup |
| Gradle | (via wrapper) | Build and test automation |

### Port Requirements

| Port | Service | Notes |
|------|---------|-------|
| 8080 | Application | Spring Boot REST API |
| 3000 | Simulator | Garage simulator API (GET /garage) |
| 3003 | Webhook | Simulator sends events here |

### Verify Prerequisites

```bash
# Check Java version
java -version
# Expected: openjdk version "21.x.x" or higher

# Check Docker
docker --version
# Expected: Docker version 20.10.x or higher

# Check Docker Compose
docker compose version
# Expected: Docker Compose version v2.x.x or higher
```

---

## Environment Setup

### Option A: Using Docker Compose (Recommended)

Docker Compose starts all services automatically with proper networking.

```bash
# Start the entire stack (MySQL + Simulator + App)
docker compose up -d

# Verify all containers are running
docker compose ps
```

Expected output:
```
NAME                    IMAGE                          STATUS
estaparking-mysql       mysql:8.0                      healthy
estaparking-simulator   cfontes0estapar/garage-sim:1.0.0   running
estaparking-app         estaparking-app                healthy
```

### Option B: Local Development with External Dependencies

If you want to run the app locally while connecting to external services:

```bash
# Start MySQL (example using Docker)
docker run -d \
  --name estaparking-mysql \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=estaparking \
  -e MYSQL_USER=estaparking \
  -e MYSQL_PASSWORD=estaparking \
  -p 3306:3306 \
  mysql:8.0

# Start Simulator (host networking required)
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0

# Run application with environment variables
export DB_URL=jdbc:mysql://localhost:3306/estaparking
export DB_USERNAME=estaparking
export DB_PASSWORD=estaparking
./gradlew bootRun
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/estaparking` | JDBC connection URL |
| `DB_USERNAME` | `estaparking` | Database username |
| `DB_PASSWORD` | `estaparking` | Database password |
| `SIMULATOR_BASE_URL` | `http://localhost:3000` | Simulator endpoint |

---

## Application Startup

### Docker Compose (Recommended)

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f app

# Stop all services
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v
```

### Gradle Commands

```bash
# Build without tests
./gradlew build -x test

# Run application
./gradlew bootRun

# Build and run JAR directly
./gradlew bootJar
java -jar build/libs/estaparking-*.jar
```

### Startup Sequence (Docker Compose)

1. **MySQL** starts first and becomes healthy (30s startup)
2. **Flyway** runs migrations automatically on app startup
3. **App** fetches garage configuration from simulator
4. **Health check** verifies `/revenue` endpoint is accessible

### Verify Application is Running

```bash
# Health check (should return 200 OK with empty or valid JSON)
curl http://localhost:8080/revenue -X POST -H "Content-Type: application/json" -d '{}'
```

---

## Testing the API

### Fetch Garage Configuration

The app automatically fetches garage configuration on startup. To verify manually:

```bash
# Direct simulator call (from host)
curl http://localhost:3000/garage

# Or via the app (if endpoint exposed)
curl http://localhost:8080/garage
```

### Webhook Event Flow

The simulator sends events to `http://localhost:3003/webhook`.

#### ENTRY Event

```bash
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "license_plate": "ABC1234",
    "entry_time": "2025-01-01T10:00:00.000Z",
    "event_type": "ENTRY"
  }'
```

**Expected Response**: `HTTP 200 OK`

#### PARKED Event

```bash
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "license_plate": "ABC1234",
    "lat": -23.561684,
    "lng": -46.655981,
    "event_type": "PARKED"
  }'
```

**Expected Response**: `HTTP 200 OK`

#### EXIT Event

```bash
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "license_plate": "ABC1234",
    "exit_time": "2025-01-01T12:30:00.000Z",
    "event_type": "EXIT"
  }'
```

**Expected Response**: `HTTP 200 OK`

### Revenue Query

Query revenue total by sector and date:

```bash
# Revenue for a specific sector and date
curl -X POST http://localhost:8080/revenue \
  -H "Content-Type: application/json" \
  -d '{
    "sector": "A1",
    "date": "2025-01-01"
  }'
```

**Example Response**:
```json
{
  "amount": 150.00,
  "currency": "BRL",
  "timestamp": "2025-01-01T12:00:00"
}
```

### Complete Flow Test

```bash
# 1. Entry
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{"license_plate": "TEST001", "entry_time": "2025-01-01T08:00:00.000Z", "event_type": "ENTRY"}'

# 2. Park
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{"license_plate": "TEST001", "lat": -23.561684, "lng": -46.655981, "event_type": "PARKED"}'

# 3. Exit after 90 minutes (should charge 2 hours)
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{"license_plate": "TEST001", "exit_time": "2025-01-01T09:30:00.000Z", "event_type": "EXIT"}'

# 4. Query revenue
curl -X POST http://localhost:8080/revenue \
  -H "Content-Type: application/json" \
  -d '{"sector": "A1", "date": "2025-01-01"}'
```

---

## Quality Gates (CI-equivalent)

Run these commands before committing. They match the CI pipeline exactly.

### 1. Format and Lint Check

```bash
./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck
```

**What it checks**:
- Code formatting (4-space indent, 120-char lines)
- ktlint_official style compliance
- No unused imports

**Success criteria**: `BUILD SUCCESSFUL` with no violations.

### 2. Static Analysis

```bash
./gradlew detekt
```

**What it checks**:
- Cyclomatic complexity (< 14 per method)
- Method length (< 60 lines)
- Return count (< 2 per method)
- Long parameter lists (> 5 params = warning)

**Success criteria**: `BUILD SUCCESSFUL` with no violations.

### 3. Tests with Coverage

```bash
./gradlew test jacocoTestReport
```

**What it checks**:
- All unit and integration tests pass
- Code coverage meets thresholds (90% overall, 90% changed files)

**Success criteria**:
- `BUILD SUCCESSFUL`
- Coverage report at `build/reports/jacoco/test/html/index.html`

### Complete Quality Gate Sequence

```bash
# Run all gates in order (matches CI)
./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck && \
./gradlew detekt && \
./gradlew test jacocoTestReport

# Shortcut: ./gradlew build runs all gates
./gradlew build
```

**Note on Windows**: Use `gradlew.bat` instead of `./gradlew`:
```powershell
.\gradlew.bat ktlintMainSourceSetCheck ktlintTestSourceSetCheck
.\gradlew.bat detekt
.\gradlew.bat test jacocoTestReport
```

---

## Troubleshooting

### Port Already in Use

```bash
# Find what's using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID)
taskkill /PID <PID> /F

# Or use Docker to check
docker ps --format "{{.Names}}\t{{.Ports}}"
```

### Database Connection Failed

**Symptom**: `Communications link failure` or `Unable to acquire JDBC Connection`

**Solutions**:
1. Verify MySQL is running:
   ```bash
   docker compose ps mysql
   ```

2. Check MySQL logs:
   ```bash
   docker compose logs mysql
   ```

3. Verify credentials match:
   - Check `application.yaml` for DB URL, username, password
   - Ensure Docker environment variables match

4. Wait for MySQL health check:
   - MySQL takes ~30s to become healthy
   - App depends on `mysql: condition: service_healthy`

### Simulator Not Reachable

**Symptom**: `Connection refused` when fetching garage config

**Solutions**:
1. Verify simulator container is running:
   ```bash
   docker compose ps simulator
   ```

2. Check simulator logs:
   ```bash
   docker compose logs simulator
   ```

3. Test simulator directly:
   ```bash
   curl http://localhost:3000/garage
   ```

4. For Docker networking issues:
   - Ensure `--network="host"` is used for simulator
   - On Windows/Mac, use `host.docker.internal` instead of `localhost`

### Tests Failing

**Common causes**:

1. **Testcontainers not starting**:
   ```bash
   docker ps  # Verify Docker is running
   ```

2. **Port conflicts**:
   ```bash
   # Kill any leftover processes
   docker compose down
   ```

3. **Database not cleaned**:
   ```bash
   # Clean volumes between runs
   docker compose down -v
   ```

4. **Missing environment**:
   ```bash
   # Export test variables
   export DB_TEST_URL=jdbc:mysql://localhost:3306/estaparking_test
   ```

### Build Failures

**Gradle cache corruption**:
```bash
./gradlew clean --refresh-dependencies
```

**Kotlin compiler issues**:
```bash
./gradlew clean build -x test --info  # More verbose output
```

### App Container Health Check Fails

**Symptom**: `healthcheck: unhealthy`

**Solutions**:
1. Check app logs:
   ```bash
   docker compose logs app
   ```

2. Verify database URL (inside container):
   ```bash
   docker compose exec app printenv SPRING_DATASOURCE_URL
   ```

3. Increase health check timeouts in `docker-compose.yml`:
   ```yaml
   healthcheck:
     start_period: 120s  # Increase from 90s
     retries: 10          # Increase from 5
   ```

---

## Quick Reference

### Common Commands

```bash
# Start everything
docker compose up -d

# View logs
docker compose logs -f

# Stop cleanly
docker compose down

# Full reset (remove volumes)
docker compose down -v

# Run tests
./gradlew test

# Full build
./gradlew build

# Clean build
./gradlew clean build
```

### File Locations

| Purpose | Path |
|---------|------|
| Application config | `src/main/resources/application.yaml` |
| Test config | `src/main/resources/application-test.yaml` |
| DB migrations | `src/main/resources/db/migration/` |
| Coverage report | `build/reports/jacoco/test/html/index.html` |
| Test results | `build/reports/tests/test/index.html` |

### API Endpoints Summary

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| POST | `http://localhost:3003/webhook` | Event JSON | Receive simulator events |
| POST | `http://localhost:8080/revenue` | JSON body | Query revenue by sector/date |
| GET | `http://localhost:3000/garage` | - | Fetch garage config (simulator) |

---

*Last updated: 2026-05-11*