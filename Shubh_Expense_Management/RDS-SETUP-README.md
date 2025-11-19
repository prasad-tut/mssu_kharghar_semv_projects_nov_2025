# AWS RDS PostgreSQL Setup - Quick Start Guide

This guide helps you set up AWS RDS PostgreSQL for the Expense Management System.

## 📋 Prerequisites

Before starting, ensure you have:

- ✅ AWS Account with appropriate permissions
- ✅ AWS CLI installed and configured (`aws configure`)
- ✅ PostgreSQL client (psql) installed locally
- ✅ Basic understanding of AWS RDS and security groups

## 🚀 Quick Setup Options

### Option 1: Automated Setup (Recommended)

Use the provided setup script for automated RDS creation:

**Linux/macOS:**
```bash
chmod +x scripts/setup-rds.sh
./scripts/setup-rds.sh
```

**Windows (Git Bash):**
```bash
bash scripts/setup-rds.sh
```

The script will:
1. Verify AWS CLI configuration
2. Prompt for database configuration
3. Create security group with proper rules
4. Create RDS PostgreSQL instance
5. Wait for instance to be available
6. Save connection details to `rds-connection-details.txt`

### Option 2: Manual Setup via AWS Console

Follow the detailed instructions in `aws-rds-setup.md` for step-by-step manual setup through the AWS Console.

### Option 3: AWS CLI Commands

Use the AWS CLI commands documented in `aws-rds-setup.md` for custom setup.

## 🧪 Testing the Connection

After RDS setup, test the connection:

**Linux/macOS:**
```bash
chmod +x scripts/test-rds-connection.sh
./scripts/test-rds-connection.sh
```

**Windows (PowerShell):**
```powershell
.\scripts\test-rds-connection.ps1
```

**Java Test (from application):**
```bash
# Set environment variables first
export DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
export DB_USERNAME="expenseadmin"
export DB_PASSWORD="your-password"

# Run the connection test
mvn test -Dtest=RDSConnectionTest
```

## ⚙️ Configure Application

### Step 1: Set Environment Variables

**Linux/macOS:**
```bash
export DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
export DB_USERNAME="expenseadmin"
export DB_PASSWORD="your-secure-password"
export JWT_SECRET="your-production-jwt-secret-minimum-256-bits"
```

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
$env:DB_USERNAME="expenseadmin"
$env:DB_PASSWORD="your-secure-password"
$env:JWT_SECRET="your-production-jwt-secret-minimum-256-bits"
```

**Windows (CMD):**
```cmd
set DB_URL=jdbc:postgresql://your-rds-endpoint:5432/expensedb
set DB_USERNAME=expenseadmin
set DB_PASSWORD=your-secure-password
set JWT_SECRET=your-production-jwt-secret-minimum-256-bits
```

### Step 2: Run Database Migrations

```bash
mvn flyway:migrate
```

### Step 3: Start Application with Production Profile

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📝 Configuration Files

The following files have been created for RDS setup:

1. **aws-rds-setup.md** - Comprehensive setup guide with all options
2. **scripts/setup-rds.sh** - Automated setup script (Linux/macOS)
3. **scripts/test-rds-connection.sh** - Connection test script (Linux/macOS)
4. **scripts/test-rds-connection.ps1** - Connection test script (Windows)
5. **src/main/resources/application-prod.yml** - Production configuration
6. **src/test/java/com/expensemanagement/util/RDSConnectionTest.java** - Java connection test

## 🔒 Security Checklist

Before going to production, ensure:

- [ ] Strong master password set (min 8 characters, mixed case, numbers, symbols)
- [ ] Security group restricts access to specific IPs or security groups
- [ ] Encryption at rest enabled
- [ ] SSL/TLS enabled for connections
- [ ] Backup retention configured (7-30 days)
- [ ] CloudWatch monitoring enabled
- [ ] Deletion protection enabled
- [ ] Environment variables not committed to version control
- [ ] AWS Secrets Manager configured for credentials (optional but recommended)

## 📊 Monitoring

### View RDS Metrics in AWS Console

1. Navigate to RDS Console
2. Select your database instance
3. Click "Monitoring" tab
4. View CPU, memory, connections, and storage metrics

### CloudWatch Logs

```bash
# View PostgreSQL logs
aws logs tail /aws/rds/instance/expense-management-db/postgresql --follow
```

### Application Health Check

```bash
curl http://localhost:8080/api/health
```

## 🛠️ Troubleshooting

### Cannot Connect to RDS

**Problem:** Connection timeout or refused

**Solutions:**
1. Check security group inbound rules (port 5432 open for your IP)
2. Verify RDS instance is publicly accessible (for development)
3. Check VPC and subnet configuration
4. Verify endpoint address is correct
5. Test network connectivity: `telnet your-rds-endpoint 5432`

### Authentication Failed

**Problem:** Password authentication failed

**Solutions:**
1. Verify username and password are correct
2. Check if you're connecting to the correct database name
3. Ensure no special characters causing issues in password

### Migrations Fail

**Problem:** Flyway migrations fail to run

**Solutions:**
1. Check database connection is working
2. Verify Flyway configuration in application.yml
3. Check migration scripts for syntax errors
4. Ensure database user has proper permissions

### Connection Pool Exhausted

**Problem:** Too many connections

**Solutions:**
1. Increase `maximum-pool-size` in application-prod.yml
2. Check for connection leaks in application code
3. Monitor active connections in RDS console
4. Consider upgrading RDS instance class

## 💰 Cost Optimization

- **Development:** Use `db.t3.micro` (free tier eligible for 12 months)
- **Production:** Start with `db.t3.small` and scale as needed
- **Storage:** Start with 20GB and enable autoscaling
- **Backups:** 7-day retention is usually sufficient
- **Reserved Instances:** Save up to 60% for production workloads

## 📚 Additional Resources

- [AWS RDS Documentation](https://docs.aws.amazon.com/rds/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Boot Database Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html)
- [Flyway Documentation](https://flywaydb.org/documentation/)

## 🎯 Next Steps

After successful RDS setup:

1. ✅ Test connection from local environment
2. ✅ Run database migrations
3. ✅ Test application with RDS
4. ✅ Configure monitoring and alarms
5. ✅ Set up automated backups
6. ✅ Document connection details securely
7. ✅ Configure production deployment pipeline

## 📞 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Review AWS RDS logs in CloudWatch
3. Check application logs
4. Verify all environment variables are set correctly
5. Consult the detailed guide in `aws-rds-setup.md`

---

**Note:** Keep your RDS credentials secure and never commit them to version control. Use environment variables or AWS Secrets Manager for production deployments.
