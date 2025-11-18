# Multi-stage build for optimized image size

# Stage 1: Build
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/target/restapi_app-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 9080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9080/appointments/get || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
