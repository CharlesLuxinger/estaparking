#!/bin/bash
set -e

echo "=========================================="
echo "Starting Estaparking Stack"
echo "=========================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "Error: docker-compose is not installed."
    exit 1
fi

# Build and start the stack
echo "Building and starting containers..."
docker-compose build --no-cache
docker-compose up -d

echo ""
echo "Waiting for services to be healthy..."
echo ""

# Wait for MySQL
echo "Waiting for MySQL..."
for i in {1..30}; do
    if docker-compose exec -T mysql mysqladmin ping -h localhost -u root -prootpass &> /dev/null; then
        echo "MySQL is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Error: MySQL failed to start within 30 seconds"
        docker-compose logs mysql
        exit 1
    fi
    sleep 1
done

# Wait for app health
echo "Waiting for application..."
for i in {1..60}; do
    if curl -sf http://localhost:8080/revenue > /dev/null 2>&1; then
        echo "Application is ready!"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "Warning: Application may not be fully ready yet"
    fi
    sleep 2
done

echo ""
echo "=========================================="
echo "Stack is running!"
echo "=========================================="
echo ""
echo "Services:"
echo "  - Application: http://localhost:8080"
echo "  - Revenue API: http://localhost:8080/revenue"
echo "  - Webhook: http://localhost:3003/webhook (via simulator)"
echo "  - MySQL: localhost:3306"
echo ""
echo "To view logs: docker-compose logs -f"
echo "To stop: ./run.sh stop"
echo "=========================================="

# Handle stop command
if [ "$1" == "stop" ]; then
    echo "Stopping stack..."
    docker-compose down
    echo "Stack stopped."
    exit 0
fi