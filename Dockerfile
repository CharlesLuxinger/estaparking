FROM eclipse-temurin:21-jre
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the pre-built artifact from host
COPY build/libs/estaparking-0.0.1-SNAPSHOT.jar app.jar

# Create non-root user for security (using GID/UID 2000 to avoid conflicts)
RUN groupadd -g 2000 estaparking && \
    useradd -r -u 2000 -g estaparking estaparking
RUN chown -R estaparking:estaparking /app
USER estaparking

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/revenue || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
