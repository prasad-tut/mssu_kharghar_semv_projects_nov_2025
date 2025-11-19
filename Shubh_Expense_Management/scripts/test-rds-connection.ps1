# RDS Connection Test Script (PowerShell)
# Tests connectivity to AWS RDS PostgreSQL database

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "RDS Connection Test" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

function Print-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Print-Error {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

function Print-Info {
    param([string]$Message)
    Write-Host "ℹ $Message" -ForegroundColor White
}

# Check if psql is installed
$psqlPath = Get-Command psql -ErrorAction SilentlyContinue
if (-not $psqlPath) {
    Print-Error "psql is not installed. Please install PostgreSQL client."
    Write-Host "Download from: https://www.postgresql.org/download/windows/"
    exit 1
}

Print-Success "PostgreSQL client (psql) is installed"

# Prompt for connection details
Write-Host ""
Print-Info "Enter RDS connection details:"
Write-Host ""

$RDS_ENDPOINT = Read-Host "RDS Endpoint"
$RDS_PORT = Read-Host "Port [5432]"
if ([string]::IsNullOrWhiteSpace($RDS_PORT)) { $RDS_PORT = "5432" }

$DB_NAME = Read-Host "Database Name [expensedb]"
if ([string]::IsNullOrWhiteSpace($DB_NAME)) { $DB_NAME = "expensedb" }

$DB_USERNAME = Read-Host "Username [expenseadmin]"
if ([string]::IsNullOrWhiteSpace($DB_USERNAME)) { $DB_USERNAME = "expenseadmin" }

$DB_PASSWORD = Read-Host "Password" -AsSecureString
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($DB_PASSWORD)
$DB_PASSWORD_PLAIN = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)

Write-Host ""
Print-Info "Testing connection to: $RDS_ENDPOINT`:$RDS_PORT/$DB_NAME"

# Test 1: Network connectivity
Write-Host ""
Write-Host "Test 1: Network Connectivity" -ForegroundColor Yellow
Write-Host "----------------------------" -ForegroundColor Yellow

try {
    $tcpClient = New-Object System.Net.Sockets.TcpClient
    $tcpClient.Connect($RDS_ENDPOINT, $RDS_PORT)
    $tcpClient.Close()
    Print-Success "Network connection successful"
} catch {
    Print-Error "Cannot reach $RDS_ENDPOINT`:$RDS_PORT"
    Print-Info "Check security group rules and network configuration"
    exit 1
}

# Set environment variable for psql
$env:PGPASSWORD = $DB_PASSWORD_PLAIN

# Test 2: Database connection
Write-Host ""
Write-Host "Test 2: Database Connection" -ForegroundColor Yellow
Write-Host "----------------------------" -ForegroundColor Yellow

$testQuery = "SELECT 1;"
$result = & psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -c $testQuery 2>&1

if ($LASTEXITCODE -eq 0) {
    Print-Success "Database connection successful"
} else {
    Print-Error "Database connection failed"
    Print-Info "Check credentials and database name"
    Remove-Item Env:\PGPASSWORD
    exit 1
}

# Test 3: PostgreSQL version
Write-Host ""
Write-Host "Test 3: PostgreSQL Version" -ForegroundColor Yellow
Write-Host "----------------------------" -ForegroundColor Yellow

$versionQuery = "SELECT version();"
$version = & psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c $versionQuery 2>&1
Print-Info "PostgreSQL Version: $version"

# Test 4: List databases
Write-Host ""
Write-Host "Test 4: List Databases" -ForegroundColor Yellow
Write-Host "----------------------------" -ForegroundColor Yellow

$dbQuery = "SELECT datname FROM pg_database WHERE datistemplate = false;"
$databases = & psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d postgres -t -c $dbQuery 2>&1
Print-Info "Available databases:"
Write-Host $databases

# Test 5: Check tables in target database
Write-Host ""
Write-Host "Test 5: Check Tables" -ForegroundColor Yellow
Write-Host "----------------------------" -ForegroundColor Yellow

$tableCountQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';"
$tableCount = (& psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c $tableCountQuery 2>&1).Trim()

if ($tableCount -eq "0") {
    Print-Info "No tables found (database is empty - migrations not run yet)"
} else {
    Print-Success "Found $tableCount tables"
    Print-Info "Tables:"
    & psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -c "\dt" 2>&1
}

# Test 6: Check Flyway schema history (if exists)
Write-Host ""
Write-Host "Test 6: Check Migration Status" -ForegroundColor Yellow
Write-Host "----------------------------" -ForegroundColor Yellow

$flywayCheckQuery = "SELECT 1 FROM information_schema.tables WHERE table_name = 'flyway_schema_history';"
$flywayExists = & psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c $flywayCheckQuery 2>&1

if ($flywayExists -match "1") {
    $migrationCountQuery = "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true;"
    $migrationCount = (& psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c $migrationCountQuery 2>&1).Trim()
    Print-Success "Flyway migrations table exists"
    Print-Info "Successful migrations: $migrationCount"
} else {
    Print-Info "Flyway migrations not run yet"
}

# Test 7: Connection string for application
Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Connection Configuration" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "JDBC URL:"
Write-Host "jdbc:postgresql://$RDS_ENDPOINT`:$RDS_PORT/$DB_NAME"
Write-Host ""
Write-Host "Environment Variables (PowerShell):"
Write-Host "`$env:DB_URL=`"jdbc:postgresql://$RDS_ENDPOINT`:$RDS_PORT/$DB_NAME`""
Write-Host "`$env:DB_USERNAME=`"$DB_USERNAME`""
Write-Host "`$env:DB_PASSWORD=`"your-password-here`""
Write-Host ""
Write-Host "Environment Variables (CMD):"
Write-Host "set DB_URL=jdbc:postgresql://$RDS_ENDPOINT`:$RDS_PORT/$DB_NAME"
Write-Host "set DB_USERNAME=$DB_USERNAME"
Write-Host "set DB_PASSWORD=your-password-here"
Write-Host ""
Write-Host "Spring Boot application.yml:"
Write-Host "spring:"
Write-Host "  datasource:"
Write-Host "    url: jdbc:postgresql://$RDS_ENDPOINT`:$RDS_PORT/$DB_NAME"
Write-Host "    username: $DB_USERNAME"
Write-Host "    password: `${DB_PASSWORD}"
Write-Host ""

# Summary
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Test Summary" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Print-Success "All connection tests passed!"
Print-Info "RDS database is ready for use"
Write-Host ""
Print-Info "Next steps:"
Write-Host "1. Update your application configuration with the connection details above"
Write-Host "2. Run database migrations: mvn flyway:migrate"
Write-Host "3. Start your application: mvn spring-boot:run"
Write-Host ""

# Clean up
Remove-Item Env:\PGPASSWORD
