#!/bin/bash

# RDS Connection Test Script
# Tests connectivity to AWS RDS PostgreSQL database

set -e

echo "=========================================="
echo "RDS Connection Test"
echo "=========================================="
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${NC}ℹ $1${NC}"
}

# Check if psql is installed
if ! command -v psql &> /dev/null; then
    print_error "psql is not installed. Please install PostgreSQL client."
    echo "Ubuntu/Debian: sudo apt-get install postgresql-client"
    echo "macOS: brew install postgresql"
    echo "Windows: Download from https://www.postgresql.org/download/windows/"
    exit 1
fi

print_success "PostgreSQL client (psql) is installed"

# Prompt for connection details
echo ""
print_info "Enter RDS connection details:"
echo ""

read -p "RDS Endpoint: " RDS_ENDPOINT
read -p "Port [5432]: " RDS_PORT
RDS_PORT=${RDS_PORT:-5432}
read -p "Database Name [expensedb]: " DB_NAME
DB_NAME=${DB_NAME:-expensedb}
read -p "Username [expenseadmin]: " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-expenseadmin}
read -sp "Password: " DB_PASSWORD
echo ""

echo ""
print_info "Testing connection to: $RDS_ENDPOINT:$RDS_PORT/$DB_NAME"

# Test 1: Network connectivity
echo ""
echo "Test 1: Network Connectivity"
echo "----------------------------"

if command -v nc &> /dev/null; then
    if nc -zv $RDS_ENDPOINT $RDS_PORT 2>&1 | grep -q "succeeded"; then
        print_success "Network connection successful"
    else
        print_error "Cannot reach $RDS_ENDPOINT:$RDS_PORT"
        print_info "Check security group rules and network configuration"
        exit 1
    fi
elif command -v telnet &> /dev/null; then
    if timeout 5 telnet $RDS_ENDPOINT $RDS_PORT 2>&1 | grep -q "Connected"; then
        print_success "Network connection successful"
    else
        print_error "Cannot reach $RDS_ENDPOINT:$RDS_PORT"
        print_info "Check security group rules and network configuration"
        exit 1
    fi
else
    print_info "Skipping network test (nc or telnet not available)"
fi

# Test 2: Database connection
echo ""
echo "Test 2: Database Connection"
echo "----------------------------"

export PGPASSWORD=$DB_PASSWORD

if psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -c "SELECT 1;" > /dev/null 2>&1; then
    print_success "Database connection successful"
else
    print_error "Database connection failed"
    print_info "Check credentials and database name"
    exit 1
fi

# Test 3: PostgreSQL version
echo ""
echo "Test 3: PostgreSQL Version"
echo "----------------------------"

PG_VERSION=$(psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c "SELECT version();" 2>/dev/null)
print_info "PostgreSQL Version: $PG_VERSION"

# Test 4: List databases
echo ""
echo "Test 4: List Databases"
echo "----------------------------"

DATABASES=$(psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d postgres -t -c "SELECT datname FROM pg_database WHERE datistemplate = false;" 2>/dev/null)
print_info "Available databases:"
echo "$DATABASES"

# Test 5: Check tables in target database
echo ""
echo "Test 5: Check Tables"
echo "----------------------------"

TABLE_COUNT=$(psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" 2>/dev/null | xargs)

if [ "$TABLE_COUNT" -eq "0" ]; then
    print_info "No tables found (database is empty - migrations not run yet)"
else
    print_success "Found $TABLE_COUNT tables"
    print_info "Tables:"
    psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -c "\dt" 2>/dev/null
fi

# Test 6: Check Flyway schema history (if exists)
echo ""
echo "Test 6: Check Migration Status"
echo "----------------------------"

if psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c "SELECT 1 FROM information_schema.tables WHERE table_name = 'flyway_schema_history';" 2>/dev/null | grep -q 1; then
    MIGRATION_COUNT=$(psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME -t -c "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true;" 2>/dev/null | xargs)
    print_success "Flyway migrations table exists"
    print_info "Successful migrations: $MIGRATION_COUNT"
else
    print_info "Flyway migrations not run yet"
fi

# Test 7: Connection string for application
echo ""
echo "=========================================="
echo "Connection Configuration"
echo "=========================================="
echo ""
echo "JDBC URL:"
echo "jdbc:postgresql://$RDS_ENDPOINT:$RDS_PORT/$DB_NAME"
echo ""
echo "Environment Variables:"
echo "export DB_URL=\"jdbc:postgresql://$RDS_ENDPOINT:$RDS_PORT/$DB_NAME\""
echo "export DB_USERNAME=\"$DB_USERNAME\""
echo "export DB_PASSWORD=\"your-password-here\""
echo ""
echo "Spring Boot application.yml:"
echo "spring:"
echo "  datasource:"
echo "    url: jdbc:postgresql://$RDS_ENDPOINT:$RDS_PORT/$DB_NAME"
echo "    username: $DB_USERNAME"
echo "    password: \${DB_PASSWORD}"
echo ""

# Summary
echo "=========================================="
echo "Test Summary"
echo "=========================================="
print_success "All connection tests passed!"
print_info "RDS database is ready for use"
echo ""
print_info "Next steps:"
echo "1. Update your application configuration with the connection details above"
echo "2. Run database migrations: mvn flyway:migrate"
echo "3. Start your application: mvn spring-boot:run"
echo ""

unset PGPASSWORD
