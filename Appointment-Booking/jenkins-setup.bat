@echo off
REM Jenkins Setup Script for Appointment Management System (Windows)
REM This script helps automate Jenkins configuration

echo ==========================================
echo Jenkins Setup Helper Script
echo Appointment Management System
echo ==========================================
echo.

REM Check if Jenkins is running
echo Checking if Jenkins is running...
curl -s http://localhost:8080 >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Jenkins is running
) else (
    echo [ERROR] Jenkins is not running
    echo Please start Jenkins first
    exit /b 1
)

echo.
echo Checking required tools...

REM Check Java
where java >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Java installed
    java -version
) else (
    echo [ERROR] Java not found
    exit /b 1
)

REM Check Maven
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven installed
    mvn -version | findstr "Apache Maven"
) else (
    echo [ERROR] Maven not found
    exit /b 1
)

REM Check Docker
where docker >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Docker installed
    docker --version
) else (
    echo [WARNING] Docker not found (optional)
)

REM Check AWS CLI
where aws >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] AWS CLI installed
    aws --version
) else (
    echo [WARNING] AWS CLI not found (required for deployment)
)

echo.
echo Testing Jenkins API...
curl -s http://localhost:8080/api/json >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Jenkins API accessible
) else (
    echo [WARNING] Cannot access Jenkins API
    echo You may need to configure authentication
)

echo.
set /p CREATE_ECR="Do you want to create AWS ECR repository? (y/n): "
if /i "%CREATE_ECR%"=="y" (
    echo.
    echo Creating AWS ECR repository...
    set /p AWS_REGION="Enter AWS region (default: us-east-1): "
    if "%AWS_REGION%"=="" set AWS_REGION=us-east-1
    
    aws ecr create-repository --repository-name appointment-management --region %AWS_REGION% 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo [OK] ECR repository created
    ) else (
        echo [INFO] Repository may already exist
    )
)

echo.
echo ==========================================
echo Setup Check Complete!
echo ==========================================
echo.
echo Next steps:
echo 1. Access Jenkins at: http://localhost:8080
echo 2. Install required plugins (see JENKINS-SETUP.md)
echo 3. Configure AWS credentials
echo 4. Create pipeline job
echo 5. Run your first build
echo.
echo For detailed instructions, see:
echo   - JENKINS-SETUP.md
echo   - README.md
echo.

pause
