#!/bin/bash

# AWS RDS PostgreSQL Setup Script
# This script automates the creation of an RDS PostgreSQL instance for the Expense Management System

set -e  # Exit on error

echo "=========================================="
echo "AWS RDS PostgreSQL Setup Script"
echo "=========================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${NC}ℹ $1${NC}"
}

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    print_error "AWS CLI is not installed. Please install it first."
    echo "Visit: https://aws.amazon.com/cli/"
    exit 1
fi

print_success "AWS CLI is installed"

# Check if AWS CLI is configured
if ! aws sts get-caller-identity &> /dev/null; then
    print_error "AWS CLI is not configured. Please run 'aws configure' first."
    exit 1
fi

print_success "AWS CLI is configured"

# Get AWS account information
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
AWS_REGION=$(aws configure get region)
print_info "AWS Account ID: $AWS_ACCOUNT_ID"
print_info "AWS Region: $AWS_REGION"
echo ""

# Configuration
echo "=========================================="
echo "Database Configuration"
echo "=========================================="

# Prompt for configuration or use defaults
read -p "DB Instance Identifier [expense-management-db]: " DB_INSTANCE_IDENTIFIER
DB_INSTANCE_IDENTIFIER=${DB_INSTANCE_IDENTIFIER:-expense-management-db}

read -p "Database Name [expensedb]: " DB_NAME
DB_NAME=${DB_NAME:-expensedb}

read -p "Master Username [expenseadmin]: " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-expenseadmin}

# Secure password input
read -sp "Master Password (min 8 chars): " DB_PASSWORD
echo ""
if [ ${#DB_PASSWORD} -lt 8 ]; then
    print_error "Password must be at least 8 characters long"
    exit 1
fi

read -p "DB Instance Class [db.t3.micro]: " DB_INSTANCE_CLASS
DB_INSTANCE_CLASS=${DB_INSTANCE_CLASS:-db.t3.micro}

read -p "Allocated Storage (GB) [20]: " ALLOCATED_STORAGE
ALLOCATED_STORAGE=${ALLOCATED_STORAGE:-20}

read -p "PostgreSQL Version [14.10]: " ENGINE_VERSION
ENGINE_VERSION=${ENGINE_VERSION:-14.10}

read -p "Publicly Accessible? (yes/no) [yes]: " PUBLIC_ACCESS
PUBLIC_ACCESS=${PUBLIC_ACCESS:-yes}

echo ""
echo "=========================================="
echo "Network Configuration"
echo "=========================================="

# Get default VPC
DEFAULT_VPC=$(aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query 'Vpcs[0].VpcId' --output text 2>/dev/null || echo "")

if [ -z "$DEFAULT_VPC" ] || [ "$DEFAULT_VPC" == "None" ]; then
    print_warning "No default VPC found. You'll need to specify a VPC."
    read -p "VPC ID: " VPC_ID
else
    print_info "Default VPC found: $DEFAULT_VPC"
    read -p "Use default VPC? (yes/no) [yes]: " USE_DEFAULT_VPC
    USE_DEFAULT_VPC=${USE_DEFAULT_VPC:-yes}
    
    if [ "$USE_DEFAULT_VPC" == "yes" ]; then
        VPC_ID=$DEFAULT_VPC
    else
        read -p "VPC ID: " VPC_ID
    fi
fi

# Create or select security group
echo ""
print_info "Creating security group for RDS..."

SG_NAME="expense-management-rds-sg"
SG_DESCRIPTION="Security group for Expense Management RDS PostgreSQL"

# Check if security group already exists
EXISTING_SG=$(aws ec2 describe-security-groups \
    --filters "Name=group-name,Values=$SG_NAME" "Name=vpc-id,Values=$VPC_ID" \
    --query 'SecurityGroups[0].GroupId' \
    --output text 2>/dev/null || echo "")

if [ -n "$EXISTING_SG" ] && [ "$EXISTING_SG" != "None" ]; then
    print_warning "Security group already exists: $EXISTING_SG"
    read -p "Use existing security group? (yes/no) [yes]: " USE_EXISTING_SG
    USE_EXISTING_SG=${USE_EXISTING_SG:-yes}
    
    if [ "$USE_EXISTING_SG" == "yes" ]; then
        VPC_SECURITY_GROUP_ID=$EXISTING_SG
    else
        read -p "Enter security group ID: " VPC_SECURITY_GROUP_ID
    fi
else
    # Create new security group
    VPC_SECURITY_GROUP_ID=$(aws ec2 create-security-group \
        --group-name $SG_NAME \
        --description "$SG_DESCRIPTION" \
        --vpc-id $VPC_ID \
        --query 'GroupId' \
        --output text)
    
    print_success "Created security group: $VPC_SECURITY_GROUP_ID"
    
    # Get current IP address
    MY_IP=$(curl -s https://checkip.amazonaws.com)
    print_info "Your IP address: $MY_IP"
    
    # Add inbound rule for PostgreSQL
    aws ec2 authorize-security-group-ingress \
        --group-id $VPC_SECURITY_GROUP_ID \
        --protocol tcp \
        --port 5432 \
        --cidr $MY_IP/32 \
        --group-name $SG_NAME 2>/dev/null || true
    
    print_success "Added inbound rule for your IP: $MY_IP/32"
fi

# Get DB subnet group
read -p "DB Subnet Group Name [default]: " DB_SUBNET_GROUP_NAME
DB_SUBNET_GROUP_NAME=${DB_SUBNET_GROUP_NAME:-default}

echo ""
echo "=========================================="
echo "Configuration Summary"
echo "=========================================="
echo "DB Instance Identifier: $DB_INSTANCE_IDENTIFIER"
echo "Database Name: $DB_NAME"
echo "Master Username: $DB_USERNAME"
echo "DB Instance Class: $DB_INSTANCE_CLASS"
echo "Allocated Storage: ${ALLOCATED_STORAGE}GB"
echo "PostgreSQL Version: $ENGINE_VERSION"
echo "VPC: $VPC_ID"
echo "Security Group: $VPC_SECURITY_GROUP_ID"
echo "Subnet Group: $DB_SUBNET_GROUP_NAME"
echo "Publicly Accessible: $PUBLIC_ACCESS"
echo "=========================================="
echo ""

read -p "Proceed with RDS creation? (yes/no): " PROCEED
if [ "$PROCEED" != "yes" ]; then
    print_warning "RDS creation cancelled"
    exit 0
fi

echo ""
print_info "Creating RDS PostgreSQL instance..."
print_warning "This may take 5-10 minutes..."

# Create RDS instance
aws rds create-db-instance \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --db-instance-class $DB_INSTANCE_CLASS \
    --engine postgres \
    --engine-version $ENGINE_VERSION \
    --master-username $DB_USERNAME \
    --master-user-password "$DB_PASSWORD" \
    --allocated-storage $ALLOCATED_STORAGE \
    --storage-type gp2 \
    --vpc-security-group-ids $VPC_SECURITY_GROUP_ID \
    --db-subnet-group-name $DB_SUBNET_GROUP_NAME \
    --backup-retention-period 7 \
    --preferred-backup-window "03:00-04:00" \
    --preferred-maintenance-window "mon:04:00-mon:05:00" \
    --publicly-accessible \
    --storage-encrypted \
    --enable-cloudwatch-logs-exports '["postgresql"]' \
    --db-name $DB_NAME \
    --tags Key=Project,Value=ExpenseManagement Key=Environment,Value=Production \
    --no-deletion-protection \
    > /dev/null

print_success "RDS instance creation initiated"

# Wait for instance to be available
print_info "Waiting for RDS instance to become available..."
aws rds wait db-instance-available --db-instance-identifier $DB_INSTANCE_IDENTIFIER

print_success "RDS instance is now available!"

# Get connection details
echo ""
echo "=========================================="
echo "Connection Details"
echo "=========================================="

RDS_ENDPOINT=$(aws rds describe-db-instances \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text)

RDS_PORT=$(aws rds describe-db-instances \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --query 'DBInstances[0].Endpoint.Port' \
    --output text)

echo "Endpoint: $RDS_ENDPOINT"
echo "Port: $RDS_PORT"
echo "Database: $DB_NAME"
echo "Username: $DB_USERNAME"
echo ""

# Save connection details to file
CONNECTION_FILE="rds-connection-details.txt"
cat > $CONNECTION_FILE << EOF
AWS RDS PostgreSQL Connection Details
======================================
Created: $(date)

Endpoint: $RDS_ENDPOINT
Port: $RDS_PORT
Database: $DB_NAME
Username: $DB_USERNAME
Password: [REDACTED - Use the password you provided during setup]

JDBC URL:
jdbc:postgresql://$RDS_ENDPOINT:$RDS_PORT/$DB_NAME

Environment Variables:
export DB_URL="jdbc:postgresql://$RDS_ENDPOINT:$RDS_PORT/$DB_NAME"
export DB_USERNAME="$DB_USERNAME"
export DB_PASSWORD="your-password-here"

psql Connection:
psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME

Security Group: $VPC_SECURITY_GROUP_ID
VPC: $VPC_ID
Region: $AWS_REGION
EOF

print_success "Connection details saved to: $CONNECTION_FILE"

echo ""
echo "=========================================="
echo "Next Steps"
echo "=========================================="
echo "1. Test connection: psql -h $RDS_ENDPOINT -p $RDS_PORT -U $DB_USERNAME -d $DB_NAME"
echo "2. Update application.yml or set environment variables"
echo "3. Run database migrations"
echo "4. Test application with RDS"
echo ""
print_success "RDS setup complete!"
