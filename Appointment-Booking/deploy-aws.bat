@echo off
REM AWS Deployment Script for Appointment Management System (Windows)
REM This script automates the deployment to AWS Elastic Beanstalk

echo ==========================================
echo AWS Deployment Script
echo Appointment Management System
echo ==========================================
echo.

REM Check if AWS CLI is installed
where aws >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: AWS CLI is not installed
    echo Please install AWS CLI: https://aws.amazon.com/cli/
    exit /b 1
)

REM Check if EB CLI is installed
where eb >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: EB CLI is not installed
    echo Please install EB CLI: pip install awsebcli
    exit /b 1
)

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Maven is not installed
    echo Please install Maven
    exit /b 1
)

echo Step 1: Building application...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo Build failed
    exit /b 1
)
echo Build successful
echo.

echo Step 2: Checking EB initialization...
if not exist ".elasticbeanstalk" (
    echo EB not initialized. Initializing...
    call eb init
) else (
    echo EB already initialized
)
echo.

echo Step 3: Deploying to Elastic Beanstalk...
call eb deploy

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================
    echo Deployment successful!
    echo ==========================================
    echo.
    echo To view your application:
    echo   eb open
    echo.
    echo To view logs:
    echo   eb logs
    echo.
    echo To check status:
    echo   eb status
) else (
    echo Deployment failed
    exit /b 1
)
