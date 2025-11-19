# Deployment Quick Start Guide

This is a condensed guide for quickly deploying the Expense Management System. For detailed instructions, see [DEPLOYMENT.md](DEPLOYMENT.md).

## Prerequisites Checklist

- [ ] Java 17+ installed
- [ ] Maven 3.6+ installed
- [ ] Node.js 18+ installed
- [ ] AWS CLI configured
- [ ] PostgreSQL client (psql) installed

## 1. Database Setup (5 minutes)

```bash
# Set variables
export DB_INSTANCE_IDENTIFIER="expense-management-db"
export DB_NAME="expensedb"
export DB_USERNAME="expenseadmin"
export DB_PASSWORD="YourSecurePassword123!"

# Create RDS instance
aws rds create-db-instance \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --db-instance-class db.t3.micro \
    --engine postgres \
    --engine-version 14.10 \
    --master-username $DB_USERNAME \
    --master-user-password $DB_PASSWORD \
    --allocated-storage 20 \
    --db-name $DB_NAME \
    --publicly-accessible \
    --storage-encrypted

# Wait for availability (5-10 minutes)
aws rds wait db-instance-available --db-instance-identifier $DB_INSTANCE_IDENTIFIER

# Get endpoint
export RDS_ENDPOINT=$(aws rds describe-db-instances \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text)

echo "Database ready at: $RDS_ENDPOINT"
```

## 2. Backend Deployment (10 minutes)

### Option A: EC2 Deployment

```bash
# Build JAR
mvn clean package -DskipTests

# Upload to EC2
scp -i your-key.pem target/expense-management-system-1.0.0.jar ec2-user@<ec2-ip>:/home/ec2-user/

# SSH to EC2
ssh -i your-key.pem ec2-user@<ec2-ip>

# Install Java
sudo yum install java-17-amazon-corretto -y

# Create systemd service
sudo nano /etc/systemd/system/expense-api.service
```

Add this content:

```ini
[Unit]
Description=Expense Management API
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /home/ec2-user/expense-management-system-1.0.0.jar
Restart=on-failure

Environment="DB_URL=jdbc:postgresql://YOUR_RDS_ENDPOINT:5432/expensedb"
Environment="DB_USERNAME=expenseadmin"
Environment="DB_PASSWORD=YourSecurePassword123!"
Environment="JWT_SECRET=your-production-jwt-secret-minimum-32-characters"

[Install]
WantedBy=multi-user.target
```

```bash
# Start service
sudo systemctl daemon-reload
sudo systemctl enable expense-api
sudo systemctl start expense-api

# Verify
curl http://localhost:8080/api/health
```

### Option B: Elastic Beanstalk

```bash
# Build JAR
mvn clean package -DskipTests

# Install EB CLI
pip install awsebcli

# Initialize and deploy
eb init -p "Corretto 17" expense-management-api --region us-east-1
eb create expense-api-prod --instance-type t3.small

# Set environment variables
eb setenv \
  DB_URL="jdbc:postgresql://$RDS_ENDPOINT:5432/expensedb" \
  DB_USERNAME="expenseadmin" \
  DB_PASSWORD="YourSecurePassword123!" \
  JWT_SECRET="your-production-jwt-secret"

# Deploy
eb deploy
```

## 3. Frontend Deployment (5 minutes)

### Option A: S3 + CloudFront

```bash
cd frontend

# Update production API URL
echo "VITE_API_URL=https://api.yourdomain.com/api" > .env.production

# Build
npm install
npm run build

# Create S3 bucket
aws s3 mb s3://expense-management-frontend

# Upload files
aws s3 sync dist/ s3://expense-management-frontend/ --delete

# Make bucket public (for static website)
aws s3 website s3://expense-management-frontend \
  --index-document index.html \
  --error-document index.html
```

### Option B: Netlify

```bash
cd frontend

# Install Netlify CLI
npm install -g netlify-cli

# Login and deploy
netlify login
netlify deploy --prod

# Set environment variable in Netlify dashboard:
# VITE_API_URL=https://api.yourdomain.com/api
```

## 4. Verification (2 minutes)

```bash
# Test backend
curl https://api.yourdomain.com/api/health
# Expected: {"status":"UP"}

# Test frontend
# Open browser to your frontend URL
# Try registering a user and logging in
```

## 5. Post-Deployment Setup

### Configure Security Groups

```bash
# Allow backend to access RDS
aws ec2 authorize-security-group-ingress \
    --group-id <rds-security-group> \
    --protocol tcp \
    --port 5432 \
    --source-group <backend-security-group>

# Allow public access to backend API
aws ec2 authorize-security-group-ingress \
    --group-id <backend-security-group> \
    --protocol tcp \
    --port 8080 \
    --cidr 0.0.0.0/0
```

### Set Up Monitoring

```bash
# Create CloudWatch alarm for API errors
aws cloudwatch put-metric-alarm \
  --alarm-name expense-api-errors \
  --alarm-description "Alert on API errors" \
  --metric-name Errors \
  --namespace AWS/ApplicationELB \
  --statistic Sum \
  --period 300 \
  --threshold 10 \
  --comparison-operator GreaterThanThreshold
```

## Environment Variables Reference

### Backend (Required)

```bash
DB_URL=jdbc:postgresql://your-rds-endpoint:5432/expensedb
DB_USERNAME=expenseadmin
DB_PASSWORD=YourSecurePassword123!
JWT_SECRET=your-production-jwt-secret-minimum-32-characters
```

### Frontend (Required)

```bash
VITE_API_URL=https://api.yourdomain.com/api
```

## Common Commands

### Backend

```bash
# View logs (systemd)
sudo journalctl -u expense-api -f

# Restart service
sudo systemctl restart expense-api

# Check status
sudo systemctl status expense-api

# View EB logs
eb logs
```

### Frontend

```bash
# Redeploy to S3
aws s3 sync dist/ s3://expense-management-frontend/ --delete

# Invalidate CloudFront cache
aws cloudfront create-invalidation --distribution-id YOUR_ID --paths "/*"

# Redeploy to Netlify
netlify deploy --prod
```

### Database

```bash
# Connect to database
psql -h $RDS_ENDPOINT -U expenseadmin -d expensedb

# Check tables
\dt

# View migration history
SELECT * FROM flyway_schema_history;
```

## Troubleshooting

### Backend won't start

```bash
# Check logs
sudo journalctl -u expense-api -n 100

# Test database connection
psql -h $RDS_ENDPOINT -U $DB_USERNAME -d $DB_NAME

# Verify environment variables
sudo systemctl show expense-api | grep Environment
```

### Frontend can't connect to backend

1. Check `VITE_API_URL` is correct
2. Verify backend is accessible: `curl https://api.yourdomain.com/api/health`
3. Check CORS configuration in backend
4. Review browser console for errors

### Database connection errors

```bash
# Test connectivity
telnet $RDS_ENDPOINT 5432

# Check security group rules
aws ec2 describe-security-groups --group-ids <sg-id>

# Verify RDS status
aws rds describe-db-instances --db-instance-identifier $DB_INSTANCE_IDENTIFIER
```

## Cost Estimates (AWS)

- **RDS db.t3.micro**: ~$15/month
- **EC2 t3.small**: ~$15/month
- **S3 + CloudFront**: ~$1-5/month
- **Data transfer**: Variable
- **Total**: ~$30-40/month

## Next Steps

1. ✅ Set up custom domain
2. ✅ Configure SSL/TLS certificates
3. ✅ Set up automated backups
4. ✅ Configure monitoring and alerts
5. ✅ Set up CI/CD pipeline
6. ✅ Review security settings
7. ✅ Load test the application
8. ✅ Document runbooks

## Additional Resources

- [Complete Deployment Guide](DEPLOYMENT.md)
- [Backend Deployment Guide](PRODUCTION_DEPLOYMENT_GUIDE.md)
- [Environment Variables Reference](PRODUCTION_ENV_VARIABLES.md)
- [AWS RDS Setup Guide](aws-rds-setup.md)
- [Database Migrations Guide](DATABASE_MIGRATIONS.md)

## Support

For detailed instructions and troubleshooting, refer to the complete documentation files listed above.
