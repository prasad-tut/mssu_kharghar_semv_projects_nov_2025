# Multi-stage Dockerfile for Expense Management System

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -Dspring.profiles.active=prod

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Create upload directory
RUN mkdir -p /var/app/uploads && \
    chown -R appuser:appgroup /var/app/uploads && \
    chmod 755 /var/app/uploads

# Create log directory
RUN mkdir -p /var/log/expense-management-api && \
    chown -R appuser:appgroup /var/log/expense-management-api && \
    chmod 755 /var/log/expense-management-api

# Copy JAR from build stage
COPY --from=build /app/target/expense-management-system-1.0.0.jar app.jar

# Change ownership of application files
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Set production profile
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
