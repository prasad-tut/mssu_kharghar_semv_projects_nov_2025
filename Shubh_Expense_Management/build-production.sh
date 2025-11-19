#!/bin/bash

# Production Build Script for Expense Management System
# This script builds the production-ready JAR file

set -e

echo "=========================================="
echo "Building Expense Management System"
echo "=========================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null
then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven from https://maven.apache.org/download.cgi"
    exit 1
fi

# Display Maven version
echo "Maven version:"
mvn --version
echo ""

# Clean previous builds
echo "Cleaning previous builds..."
mvn clean
echo ""

# Run tests (optional - comment out to skip)
# echo "Running tests..."
# mvn test
# echo ""

# Build production JAR
echo "Building production JAR..."
mvn package -DskipTests -Dspring.profiles.active=prod
echo ""

# Check if JAR was created
JAR_FILE="target/expense-management-system-1.0.0.jar"
if [ -f "$JAR_FILE" ]; then
    echo "=========================================="
    echo "Build successful!"
    echo "=========================================="
    echo ""
    echo "JAR file location: $JAR_FILE"
    echo "JAR file size: $(du -h $JAR_FILE | cut -f1)"
    echo ""
    echo "To run the application:"
    echo "  java -jar -Dspring.profiles.active=prod $JAR_FILE"
    echo ""
    echo "Make sure to set required environment variables:"
    echo "  - DB_URL"
    echo "  - DB_USERNAME"
    echo "  - DB_PASSWORD"
    echo "  - JWT_SECRET"
    echo ""
    echo "See PRODUCTION_ENV_VARIABLES.md for details"
else
    echo "ERROR: Build failed - JAR file not found"
    exit 1
fi
