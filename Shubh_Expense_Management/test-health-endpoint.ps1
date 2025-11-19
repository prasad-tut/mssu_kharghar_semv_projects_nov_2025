# Health Check Test Script (PowerShell)
# Tests the health endpoint of the Expense Management API

param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Testing Health Endpoint" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$healthUrl = "$BaseUrl/api/health"

Write-Host "Testing endpoint: $healthUrl" -ForegroundColor Yellow
Write-Host ""

try {
    # Make request to health endpoint
    $response = Invoke-RestMethod -Uri $healthUrl -Method Get -TimeoutSec 10
    
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "Health Check: SUCCESS" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "Response:" -ForegroundColor Cyan
    Write-Host "  Status: $($response.status)" -ForegroundColor White
    Write-Host "  Service: $($response.service)" -ForegroundColor White
    Write-Host "  Version: $($response.version)" -ForegroundColor White
    Write-Host "  Timestamp: $($response.timestamp)" -ForegroundColor White
    Write-Host ""
    
    if ($response.status -eq "UP") {
        Write-Host "✓ Application is healthy and running" -ForegroundColor Green
        exit 0
    } else {
        Write-Host "✗ Application status is not UP" -ForegroundColor Red
        exit 1
    }
    
} catch {
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host "Health Check: FAILED" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host ""
    
    if ($_.Exception.Message -match "Unable to connect") {
        Write-Host "✗ Cannot connect to server at $healthUrl" -ForegroundColor Red
        Write-Host ""
        Write-Host "Possible causes:" -ForegroundColor Yellow
        Write-Host "  - Application is not running" -ForegroundColor White
        Write-Host "  - Wrong URL or port" -ForegroundColor White
        Write-Host "  - Firewall blocking connection" -ForegroundColor White
    } else {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
    exit 1
}
