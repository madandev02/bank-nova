# Multi-stage build for Spring Boot application
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Ensure wrapper is executable and Unix-compatible in Linux build environments
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Needed for container healthcheck command
RUN apk add --no-cache curl

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to the non-root user
RUN chown spring:spring app.jar

# Switch to the non-root user
USER spring

# Expose port
EXPOSE 8080

# Health check
# Accepts 200/401/403 because security can protect health endpoints in this app.
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD sh -c 'code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || true); [ "$code" = "200" ] || [ "$code" = "401" ] || [ "$code" = "403" ]'

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
