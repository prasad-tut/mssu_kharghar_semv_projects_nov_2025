# Bug Tracker Database Query Script
# This script connects to AWS RDS MySQL and retrieves data

param(
    [string]$Query = "SELECT * FROM bugs;",
    [string]$Host = "database-1.cm5mwsc24il9.us-east-1.rds.amazonaws.com",
    [string]$User = "admin",
    [string]$Password = "mssu2025",
    [string]$Database = "bug_tracker",
    [int]$Port = 3306
)

# Check if MySQL client is available
$mysql = Get-Command mysql -ErrorAction SilentlyContinue
if (-not $mysql) {
    Write-Host "ERROR: MySQL CLI not found. Install MySQL Community Edition." -ForegroundColor Red
    Write-Host "Download: https://dev.mysql.com/downloads/mysql/" -ForegroundColor Yellow
    exit 1
}

Write-Host "Connecting to AWS RDS: $Host" -ForegroundColor Cyan
Write-Host "Database: $Database" -ForegroundColor Cyan
Write-Host ""

try {
    # Execute query
    & mysql -h $Host -u $User -p$Password -P $Port $Database -e $Query
    Write-Host ""
    Write-Host "Query executed successfully!" -ForegroundColor Green
}
catch {
    Write-Host "ERROR: Failed to execute query" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
