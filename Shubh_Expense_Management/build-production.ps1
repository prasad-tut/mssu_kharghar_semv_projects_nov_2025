# Production Build Script for Expense Management System (PowerShell)
# This script builds the production-ready JAR file

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Building Expense Management System" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven is installed
try {
    $mvnVersion = mvn --version 2>&1
    Write-Host "Maven version:" -ForegroundColor Green
    Write-Host $mvnVersion
    Write-Host ""
} catch {
    Write-Host "ERROR: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven from https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Clean previous builds
Write-Host "Cleaning previous builds..." -ForegroundColor Yellow
mvn clean
Write-Host ""

# Run tests (optional - comment out to skip)
# Write-Host "Running tests..." -ForegroundColor Yellow
# mvn test
# Write-Host ""

# Build production JAR
Write-Host "Building production JAR..." -ForegroundColor Yellow
mvn package -DskipTests -Dspring.profiles.active=prod
Write-Host ""

# Check if JAR was created
$jarFile = "target\expense-management-system-1.0.0.jar"
if (Test-Path $jarFile) {
    $jarSize = (Get-Item $jarFile).Length / 1MB
    
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "Build successful!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "JAR file location: $jarFile" -ForegroundColor Cyan
    Write-Host "JAR file size: $([math]::Round($jarSize, 2)) MB" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "To run the application:" -ForegroundColor Yellow
    Write-Host "  java -jar -Dspring.profiles.active=prod $jarFile" -ForegroundColor White
    Write-Host ""
    Write-Host "Make sure to set required environment variables:" -ForegroundColor Yellow
    Write-Host "  - DB_URL" -ForegroundColor White
    Write-Host "  - DB_USERNAME" -ForegroundColor White
    Write-Host "  - DB_PASSWORD" -ForegroundColor White
    Write-Host "  - JWT_SECRET" -ForegroundColor White
    Write-Host ""
    Write-Host "See PRODUCTION_ENV_VARIABLES.md for details" -ForegroundColor Cyan
} else {
    Write-Host "ERROR: Build failed - JAR file not found" -ForegroundColor Red
    exit 1
}
