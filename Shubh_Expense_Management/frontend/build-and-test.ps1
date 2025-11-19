# Frontend Production Build and Test Script
# This script builds the frontend for production and optionally tests it locally

param(
    [switch]$SkipTest,
    [switch]$Clean
)

Write-Host "=== Expense Management System - Frontend Production Build ===" -ForegroundColor Cyan
Write-Host ""

# Navigate to frontend directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

# Clean previous build if requested
if ($Clean) {
    Write-Host "Cleaning previous build..." -ForegroundColor Yellow
    if (Test-Path "dist") {
        Remove-Item -Recurse -Force "dist"
        Write-Host "✓ Cleaned dist directory" -ForegroundColor Green
    }
    if (Test-Path "node_modules") {
        Remove-Item -Recurse -Force "node_modules"
        Write-Host "✓ Cleaned node_modules directory" -ForegroundColor Green
    }
    Write-Host ""
}

# Install dependencies
Write-Host "Installing dependencies..." -ForegroundColor Yellow
npm install
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Failed to install dependencies" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Dependencies installed" -ForegroundColor Green
Write-Host ""

# Build for production
Write-Host "Building for production..." -ForegroundColor Yellow
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Build failed" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Production build completed" -ForegroundColor Green
Write-Host ""

# Display build output
Write-Host "Build output:" -ForegroundColor Cyan
Get-ChildItem -Path "dist" -Recurse | ForEach-Object {
    $size = if ($_.PSIsContainer) { "" } else { " ({0:N2} KB)" -f ($_.Length / 1KB) }
    Write-Host "  $($_.FullName.Replace($PWD.Path, ''))$size"
}
Write-Host ""

# Test locally if not skipped
if (-not $SkipTest) {
    Write-Host "Starting preview server for local testing..." -ForegroundColor Yellow
    Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Preview server will be available at: http://localhost:3000/" -ForegroundColor Cyan
    Write-Host "Make sure your backend is running at the configured API URL" -ForegroundColor Yellow
    Write-Host ""
    npm run preview
} else {
    Write-Host "Skipping local test (use without -SkipTest to test)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "To test the build locally, run:" -ForegroundColor Cyan
    Write-Host "  npm run preview" -ForegroundColor White
}

Write-Host ""
Write-Host "=== Build Complete ===" -ForegroundColor Green
Write-Host "The production build is ready in the 'dist' directory" -ForegroundColor Green
Write-Host "See PRODUCTION_DEPLOYMENT.md for deployment instructions" -ForegroundColor Cyan
