# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy build files first for better caching
COPY gradle gradle
COPY gradlew ./
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY config config

# Copy source code
COPY src src

# Build the application
RUN chmod +x gradlew && \
    ./gradlew build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built artifact
COPY --from=build /app/build/libs/*.jar app.jar

# Create non-root user for security
RUN groupadd -g 1000 estaparking && \
    useradd -r -u 1000 -g estaparking estaparking
RUN chown -R estaparking:estaparking /app
USER estaparking

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/revenue || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
