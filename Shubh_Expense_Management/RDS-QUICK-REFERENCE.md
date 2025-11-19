# AWS RDS Quick Reference Card

## 🚀 Setup Commands

### Create RDS Instance (Automated)
```bash
./scripts/setup-rds.sh
```

### Test Connection
```bash
./scripts/test-rds-connection.sh
```

### Manual psql Connection
```bash
psql -h your-rds-endpoint.region.rds.amazonaws.com -p 5432 -U expenseadmin -d expensedb
```

## 🔧 Configuration

### Set Environment Variables (Linux/macOS)
```bash
export DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
export DB_USERNAME="expenseadmin"
export DB_PASSWORD="your-password"
export JWT_SECRET="your-jwt-secret"
```

### Set Environment Variables (Windows PowerShell)
```powershell
$env:DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
$env:DB_USERNAME="expenseadmin"
$env:DB_PASSWORD="your-password"
$env:JWT_SECRET="your-jwt-secret"
```

## 🗄️ Database Operations

### Run Migrations
```bash
mvn flyway:migrate
```

### Check Migration Status
```bash
mvn flyway:info
```

### Repair Failed Migration
```bash
mvn flyway:repair
```

### Clean Database (Development Only!)
```bash
mvn flyway:clean
```

## 🏃 Run Application

### Development Mode
```bash
mvn spring-boot:run
```

### Production Mode
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Build and Run JAR
```bash
mvn clean package
java -jar target/expense-management-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 🧪 Testing

### Test RDS Connection (Java)
```bash
mvn test -Dtest=RDSConnectionTest
```

### Run All Tests
```bash
mvn test
```

## 📊 AWS CLI Commands

### Get RDS Endpoint
```bash
aws rds describe-db-instances \
  --db-instance-identifier expense-management-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text
```

### Check RDS Status
```bash
aws rds describe-db-instances \
  --db-instance-identifier expense-management-db \
  --query 'DBInstances[0].DBInstanceStatus' \
  --output text
```

### View CloudWatch Logs
```bash
aws logs tail /aws/rds/instance/expense-management-db/postgresql --follow
```

### Create Snapshot
```bash
aws rds create-db-snapshot \
  --db-instance-identifier expense-management-db \
  --db-snapshot-identifier expense-management-snapshot-$(date +%Y%m%d)
```

### Modify Security Group
```bash
# Add your IP
MY_IP=$(curl -s https://checkip.amazonaws.com)
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxxxxxx \
  --protocol tcp \
  --port 5432 \
  --cidr $MY_IP/32
```

## 🔍 Troubleshooting

### Test Network Connectivity
```bash
telnet your-rds-endpoint 5432
# or
nc -zv your-rds-endpoint 5432
```

### Check PostgreSQL Version
```sql
SELECT version();
```

### List All Databases
```sql
\l
```

### List All Tables
```sql
\dt
```

### Check Active Connections
```sql
SELECT count(*) FROM pg_stat_activity;
```

### View Current Connections
```sql
SELECT datname, usename, application_name, client_addr, state 
FROM pg_stat_activity 
WHERE datname = 'expensedb';
```

## 🔒 Security

### Update Security Group (Allow Specific IP)
```bash
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxxxxxx \
  --protocol tcp \
  --port 5432 \
  --cidr 203.0.113.0/32
```

### Revoke Security Group Rule
```bash
aws ec2 revoke-security-group-ingress \
  --group-id sg-xxxxxxxxx \
  --protocol tcp \
  --port 5432 \
  --cidr 203.0.113.0/32
```

### Enable Deletion Protection
```bash
aws rds modify-db-instance \
  --db-instance-identifier expense-management-db \
  --deletion-protection \
  --apply-immediately
```

## 💾 Backup & Restore

### Create Manual Snapshot
```bash
aws rds create-db-snapshot \
  --db-instance-identifier expense-management-db \
  --db-snapshot-identifier my-snapshot
```

### List Snapshots
```bash
aws rds describe-db-snapshots \
  --db-instance-identifier expense-management-db
```

### Restore from Snapshot
```bash
aws rds restore-db-instance-from-db-snapshot \
  --db-instance-identifier expense-management-db-restored \
  --db-snapshot-identifier my-snapshot
```

## 📈 Monitoring

### View RDS Metrics
```bash
aws cloudwatch get-metric-statistics \
  --namespace AWS/RDS \
  --metric-name CPUUtilization \
  --dimensions Name=DBInstanceIdentifier,Value=expense-management-db \
  --start-time $(date -u -d '1 hour ago' +%Y-%m-%dT%H:%M:%S) \
  --end-time $(date -u +%Y-%m-%dT%H:%M:%S) \
  --period 300 \
  --statistics Average
```

### Check Database Size
```sql
SELECT pg_size_pretty(pg_database_size('expensedb'));
```

### Check Table Sizes
```sql
SELECT 
  schemaname,
  tablename,
  pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

## 🧹 Cleanup (Development Only)

### Stop RDS Instance (to save costs)
```bash
aws rds stop-db-instance \
  --db-instance-identifier expense-management-db
```

### Start RDS Instance
```bash
aws rds start-db-instance \
  --db-instance-identifier expense-management-db
```

### Delete RDS Instance (with snapshot)
```bash
aws rds delete-db-instance \
  --db-instance-identifier expense-management-db \
  --final-db-snapshot-identifier expense-management-final-snapshot
```

### Delete RDS Instance (without snapshot)
```bash
aws rds delete-db-instance \
  --db-instance-identifier expense-management-db \
  --skip-final-snapshot
```

## 📝 Common SQL Queries

### Check Flyway Migration Status
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### Count Records in All Tables
```sql
SELECT 
  schemaname,
  tablename,
  n_live_tup AS row_count
FROM pg_stat_user_tables
WHERE schemaname = 'public'
ORDER BY n_live_tup DESC;
```

### View Recent Expenses
```sql
SELECT * FROM expenses ORDER BY created_at DESC LIMIT 10;
```

## 🔗 Useful Links

- [AWS RDS Console](https://console.aws.amazon.com/rds/)
- [CloudWatch Logs](https://console.aws.amazon.com/cloudwatch/home#logsV2:log-groups)
- [RDS Documentation](https://docs.aws.amazon.com/rds/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 💡 Tips

1. **Always use environment variables** for credentials
2. **Enable automated backups** with 7-30 day retention
3. **Use parameter groups** for custom PostgreSQL settings
4. **Monitor CloudWatch metrics** regularly
5. **Test backups** by restoring to a new instance
6. **Use read replicas** for read-heavy workloads
7. **Enable encryption** at rest and in transit
8. **Set up CloudWatch alarms** for critical metrics
9. **Use IAM authentication** for enhanced security
10. **Regular security group audits** to ensure proper access control
