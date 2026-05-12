# Estaparking Stack Runner for Windows

param(
    [Parameter(Position=0)]
    [ValidateSet("start", "stop", "restart")]
    [string]$Action = "start"
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Starting Estaparking Stack" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
try {
    $null = docker info 2>$null
} catch {
    Write-Host "Error: Docker is not running. Please start Docker first." -ForegroundColor Red
    exit 1
}

# Function to start the stack
function Start-Stack {
    Write-Host "Building and starting containers..." -ForegroundColor Yellow

    docker compose build --no-cache
    docker compose up -d

    Write-Host ""
    Write-Host "Waiting for services to be healthy..." -ForegroundColor Yellow
    Write-Host ""

    # Wait for MySQL
    Write-Host "Waiting for MySQL..." -ForegroundColor Yellow
    $mysqlReady = $false
    for ($i = 0; $i -lt 30; $i++) {
        try {
            $result = docker compose exec -T mysql mysqladmin ping -h localhost -u root -prootpass 2>$null
            if ($LASTEXITCODE -eq 0) {
                Write-Host "MySQL is ready!" -ForegroundColor Green
                $mysqlReady = $true
                break
            }
        } catch { }
        Start-Sleep -Seconds 1
    }

    if (-not $mysqlReady) {
        Write-Host "Error: MySQL failed to start within 30 seconds" -ForegroundColor Red
        docker compose logs mysql
        exit 1
    }

    # Wait for app health
    Write-Host "Waiting for application..." -ForegroundColor Yellow
    for ($i = 0; $i -lt 60; $i++) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/revenue" -Method GET -TimeoutSec 5 -UseBasicParsing -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Write-Host "Application is ready!" -ForegroundColor Green
                break
            }
        } catch { }
        Start-Sleep -Seconds 2
    }

    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "Stack is running!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Services:" -ForegroundColor White
    Write-Host "  - Application: http://localhost:8080" -ForegroundColor White
    Write-Host "  - Revenue API: http://localhost:8080/revenue" -ForegroundColor White
    Write-Host "  - Webhook: http://localhost:3003/webhook (via simulator)" -ForegroundColor White
    Write-Host "  - MySQL: localhost:3306" -ForegroundColor White
    Write-Host ""
    Write-Host "To view logs: docker compose logs -f" -ForegroundColor White
    Write-Host "To stop: .\run.ps1 stop" -ForegroundColor White
    Write-Host "==========================================" -ForegroundColor Cyan
}

# Function to stop the stack
function Stop-Stack {
    Write-Host "Stopping stack..." -ForegroundColor Yellow
    docker compose down
    Write-Host "Stack stopped." -ForegroundColor Green
}

# Function to restart the stack
function Restart-Stack {
    Write-Host "Restarting stack..." -ForegroundColor Yellow
    docker compose down
    Start-Stack
}

# Execute based on action
switch ($Action) {
    "start" { Start-Stack }
    "stop" { Stop-Stack }
    "restart" { Restart-Stack }
}