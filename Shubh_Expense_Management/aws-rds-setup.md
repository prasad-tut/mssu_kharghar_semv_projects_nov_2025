# AWS RDS PostgreSQL Setup Guide

This guide provides instructions for setting up an AWS RDS PostgreSQL database for the Expense Management System.

## Prerequisites

- AWS Account with appropriate permissions
- AWS CLI installed and configured (`aws configure`)
- PostgreSQL client installed locally (psql)

## Option 1: AWS CLI Setup (Recommended for Automation)

### Step 1: Set Environment Variables

```bash
# Database Configuration
export DB_INSTANCE_IDENTIFIER="expense-management-db"
export DB_NAME="expensedb"
export DB_USERNAME="expenseadmin"
export DB_PASSWORD="YourSecurePassword123!"  # Change this!
export DB_INSTANCE_CLASS="db.t3.micro"  # Free tier eligible
export ALLOCATED_STORAGE="20"  # GB
export ENGINE_VERSION="14.10"

# Network Configuration
export VPC_SECURITY_GROUP_ID="sg-xxxxxxxxx"  # Your security group ID
export DB_SUBNET_GROUP_NAME="default"  # Or your custom subnet group
```

### Step 2: Create RDS Instance

```bash
aws rds create-db-instance \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --db-instance-class $DB_INSTANCE_CLASS \
    --engine postgres \
    --engine-version $ENGINE_VERSION \
    --master-username $DB_USERNAME \
    --master-user-password $DB_PASSWORD \
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
    --tags Key=Project,Value=ExpenseManagement Key=Environment,Value=Production
```

### Step 3: Wait for Instance to be Available

```bash
aws rds wait db-instance-available --db-instance-identifier $DB_INSTANCE_IDENTIFIER
echo "RDS instance is now available!"
```

### Step 4: Get Connection Details

```bash
aws rds describe-db-instances \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --query 'DBInstances[0].[Endpoint.Address,Endpoint.Port,DBName]' \
    --output table
```

## Option 2: AWS Console Setup (Manual)

### Step 1: Navigate to RDS Console
1. Log in to AWS Console
2. Navigate to RDS service
3. Click "Create database"

### Step 2: Configure Database
- **Engine type**: PostgreSQL
- **Version**: PostgreSQL 14.10 or later
- **Templates**: Free tier (for testing) or Production (for production)
- **DB instance identifier**: `expense-management-db`
- **Master username**: `expenseadmin`
- **Master password**: Create a strong password
- **DB instance class**: db.t3.micro (free tier) or larger
- **Storage**: 20 GB (General Purpose SSD)
- **Storage autoscaling**: Enable (optional)

### Step 3: Configure Connectivity
- **VPC**: Select your VPC
- **Subnet group**: Default or custom
- **Public access**: Yes (for development) or No (for production with VPN)
- **VPC security group**: Create new or select existing
- **Availability Zone**: No preference

### Step 4: Configure Additional Settings
- **Initial database name**: `expensedb`
- **Backup retention**: 7 days
- **Enable encryption**: Yes
- **Enable CloudWatch logs**: PostgreSQL log
- **Enable deletion protection**: Yes (for production)

### Step 5: Create Database
Click "Create database" and wait 5-10 minutes for provisioning.

## Security Group Configuration

### Required Inbound Rules

For development (allow your IP):
```
Type: PostgreSQL
Protocol: TCP
Port: 5432
Source: Your IP address (e.g., 203.0.113.0/32)
```

For production (allow backend EC2/ECS):
```
Type: PostgreSQL
Protocol: TCP
Port: 5432
Source: Backend security group ID (e.g., sg-xxxxxxxxx)
```

### AWS CLI Command to Add Inbound Rule

```bash
# Get your current IP
MY_IP=$(curl -s https://checkip.amazonaws.com)

# Add inbound rule
aws ec2 authorize-security-group-ingress \
    --group-id $VPC_SECURITY_GROUP_ID \
    --protocol tcp \
    --port 5432 \
    --cidr $MY_IP/32
```

## Connection Testing

### Step 1: Get RDS Endpoint

```bash
export RDS_ENDPOINT=$(aws rds describe-db-instances \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text)

echo "RDS Endpoint: $RDS_ENDPOINT"
```

### Step 2: Test Connection with psql

```bash
psql -h $RDS_ENDPOINT -p 5432 -U $DB_USERNAME -d $DB_NAME
# Enter password when prompted
```

### Step 3: Verify Database

```sql
-- List databases
\l

-- Connect to expensedb
\c expensedb

-- List tables (should be empty initially)
\dt

-- Check PostgreSQL version
SELECT version();

-- Exit
\q
```

## Update Application Configuration

### Step 1: Set Environment Variables

Create a `.env` file or set environment variables:

```bash
export DB_URL="jdbc:postgresql://<RDS_ENDPOINT>:5432/expensedb"
export DB_USERNAME="expenseadmin"
export DB_PASSWORD="YourSecurePassword123!"
export JWT_SECRET="your-production-jwt-secret-minimum-256-bits"
```

### Step 2: Update application-prod.yml (Optional)

Create `src/main/resources/application-prod.yml`:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: false

logging:
  level:
    root: INFO
    com.expensemanagement: INFO
```

## Run Database Migrations

Once connected, run Flyway migrations:

```bash
# Using Maven
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://$RDS_ENDPOINT:5432/expensedb \
    -Dflyway.user=$DB_USERNAME \
    -Dflyway.password=$DB_PASSWORD

# Or run the application (migrations run automatically on startup)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Monitoring and Maintenance

### Enable Enhanced Monitoring

```bash
aws rds modify-db-instance \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --monitoring-interval 60 \
    --monitoring-role-arn arn:aws:iam::YOUR_ACCOUNT_ID:role/rds-monitoring-role \
    --apply-immediately
```

### View CloudWatch Logs

```bash
aws logs tail /aws/rds/instance/$DB_INSTANCE_IDENTIFIER/postgresql --follow
```

### Create Read Replica (Optional)

```bash
aws rds create-db-instance-read-replica \
    --db-instance-identifier expense-management-db-replica \
    --source-db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --db-instance-class db.t3.micro
```

## Cost Optimization

- Use **db.t3.micro** for development (free tier eligible)
- Use **db.t3.small** or larger for production
- Enable storage autoscaling
- Set appropriate backup retention (7 days recommended)
- Delete unused snapshots
- Use Reserved Instances for production (up to 60% savings)

## Troubleshooting

### Cannot Connect to RDS

1. **Check security group**: Ensure port 5432 is open for your IP
2. **Check public accessibility**: Ensure RDS instance is publicly accessible (for dev)
3. **Check VPC and subnet**: Ensure proper network configuration
4. **Check credentials**: Verify username and password
5. **Check endpoint**: Ensure you're using the correct endpoint address

### Connection Timeout

```bash
# Test network connectivity
telnet $RDS_ENDPOINT 5432

# Or use nc
nc -zv $RDS_ENDPOINT 5432
```

### SSL Connection Issues

Add SSL parameters to connection string:

```
jdbc:postgresql://<RDS_ENDPOINT>:5432/expensedb?sslmode=require
```

## Cleanup (Development Only)

To delete the RDS instance:

```bash
# Create final snapshot (recommended)
aws rds delete-db-instance \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --final-db-snapshot-identifier expense-management-final-snapshot

# Or delete without snapshot (not recommended)
aws rds delete-db-instance \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --skip-final-snapshot
```

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use AWS Secrets Manager** for production credentials
3. **Enable encryption** at rest and in transit
4. **Restrict security group** access to specific IPs/security groups
5. **Enable deletion protection** for production databases
6. **Regular backups**: Maintain 7-30 day retention
7. **Enable CloudWatch alarms** for monitoring
8. **Use IAM database authentication** (optional, for enhanced security)

## Next Steps

After RDS setup:
1. ✅ Test connection from local environment
2. ✅ Run database migrations
3. ✅ Update application configuration
4. ✅ Test application with RDS
5. ✅ Set up monitoring and alarms
6. ✅ Document connection details securely
