# Expense Management System - Complete Deployment Guide

This guide provides comprehensive instructions for deploying both the backend and frontend of the Expense Management System to production.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Database Setup](#database-setup)
3. [Backend Deployment](#backend-deployment)
4. [Frontend Deployment](#frontend-deployment)
5. [Environment Configuration](#environment-configuration)
6. [Deployment Options](#deployment-options)
7. [Post-Deployment Verification](#post-deployment-verification)
8. [Monitoring and Maintenance](#monitoring-and-maintenance)
9. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Tools
- Java 17 or higher
- Maven 3.6+
- Node.js 18+
- AWS CLI (for AWS deployments)
- Docker (optional, for containerized deployments)
- PostgreSQL client (psql)

### AWS Resources
- AWS Account with appropriate permissions
- AWS RDS PostgreSQL instance (see [aws-rds-setup.md](aws-rds-setup.md))
- EC2 instances or ECS cluster (for backend)
- S3 bucket and CloudFront distribution (for frontend)

### Required Credentials
- Database credentials (username, password)
- JWT secret key (minimum 32 characters)
- AWS access keys (if using AWS services)

## Database Setup

### Step 1: Create AWS RDS PostgreSQL Instance

Follow the detailed guide in [aws-rds-setup.md](aws-rds-setup.md) or use this quick setup:

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

# Wait for instance to be available
aws rds wait db-instance-available --db-instance-identifier $DB_INSTANCE_IDENTIFIER
```

### Step 2: Get RDS Endpoint

```bash
export RDS_ENDPOINT=$(aws rds describe-db-instances \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text)

echo "RDS Endpoint: $RDS_ENDPOINT"
```

### Step 3: Test Database Connection

```bash
psql -h $RDS_ENDPOINT -p 5432 -U $DB_USERNAME -d $DB_NAME
```

## Backend Deployment

### Option 1: AWS EC2 Deployment

#### Step 1: Build the Application

```bash
# Clone repository (if not already done)
git clone <repository-url>
cd expense-management-system

# Build production JAR
mvn clean package -DskipTests

# Verify JAR was created
ls -lh target/expense-management-system-1.0.0.jar
```

#### Step 2: Launch EC2 Instance

```bash
# Launch EC2 instance (Amazon Linux 2)
aws ec2 run-instances \
    --image-id ami-0c55b159cbfafe1f0 \
    --instance-type t3.small \
    --key-name your-key-pair \
    --security-group-ids sg-xxxxxxxxx \
    --subnet-id subnet-xxxxxxxxx \
    --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=expense-api-server}]'
```

#### Step 3: Install Java on EC2

```bash
# SSH into EC2 instance
ssh -i your-key.pem ec2-user@<ec2-public-ip>

# Install Java 17
sudo yum install java-17-amazon-corretto -y

# Verify installation
java -version
```

#### Step 4: Upload JAR to EC2

```bash
# From your local machine
scp -i your-key.pem target/expense-management-system-1.0.0.jar ec2-user@<ec2-public-ip>:/home/ec2-user/
```

#### Step 5: Create Systemd Service

On the EC2 instance, create `/etc/systemd/system/expense-api.service`:

```bash
sudo nano /etc/systemd/system/expense-api.service
```

Add the following content:

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
RestartSec=10

Environment="DB_URL=jdbc:postgresql://YOUR_RDS_ENDPOINT:5432/expensedb"
Environment="DB_USERNAME=expenseadmin"
Environment="DB_PASSWORD=YourSecurePassword123!"
Environment="JWT_SECRET=your-production-jwt-secret-minimum-32-characters"
Environment="UPLOAD_DIR=/var/app/uploads"

[Install]
WantedBy=multi-user.target
```

#### Step 6: Start the Service

```bash
# Create upload directory
sudo mkdir -p /var/app/uploads
sudo chown ec2-user:ec2-user /var/app/uploads

# Start service
sudo systemctl daemon-reload
sudo systemctl enable expense-api
sudo systemctl start expense-api

# Check status
sudo systemctl status expense-api

# View logs
sudo journalctl -u expense-api -f
```

### Option 2: Docker Deployment

#### Step 1: Build Docker Image

```bash
# Build application
mvn clean package -DskipTests

# Build Docker image
docker build -t expense-management-api:1.0.0 .

# Test locally
docker run -d \
  --name expense-api-test \
  -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/expensedb" \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET="test-secret-key-minimum-32-characters" \
  expense-management-api:1.0.0

# Check logs
docker logs -f expense-api-test

# Test health endpoint
curl http://localhost:8080/api/health

# Stop test container
docker stop expense-api-test
docker rm expense-api-test
```

#### Step 2: Push to Container Registry

```bash
# For AWS ECR
aws ecr create-repository --repository-name expense-management-api

# Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag expense-management-api:1.0.0 \
  <account-id>.dkr.ecr.us-east-1.amazonaws.com/expense-management-api:1.0.0

docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/expense-management-api:1.0.0
```

#### Step 3: Deploy to ECS/Fargate

Create ECS task definition and service through AWS Console or CLI. See [PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md) for detailed ECS instructions.

### Option 3: AWS Elastic Beanstalk

```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init -p "Corretto 17" expense-management-api --region us-east-1

# Create environment
eb create expense-api-prod --instance-type t3.small

# Set environment variables
eb setenv \
  DB_URL="jdbc:postgresql://YOUR_RDS_ENDPOINT:5432/expensedb" \
  DB_USERNAME="expenseadmin" \
  DB_PASSWORD="YourSecurePassword123!" \
  JWT_SECRET="your-production-jwt-secret"

# Deploy
eb deploy

# Check status
eb status

# View logs
eb logs
```

## Frontend Deployment

### Option 1: AWS S3 + CloudFront

#### Step 1: Build Frontend

```bash
cd frontend

# Install dependencies
npm install

# Update production API URL
echo "VITE_API_URL=https://api.yourdomain.com/api" > .env.production

# Build for production
npm run build

# Verify build
ls -lh dist/
```

#### Step 2: Create S3 Bucket

```bash
# Create bucket
aws s3 mb s3://expense-management-frontend

# Enable static website hosting
aws s3 website s3://expense-management-frontend \
  --index-document index.html \
  --error-document index.html

# Set bucket policy for public read
cat > bucket-policy.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::expense-management-frontend/*"
    }
  ]
}
EOF

aws s3api put-bucket-policy \
  --bucket expense-management-frontend \
  --policy file://bucket-policy.json
```

#### Step 3: Upload Build Files

```bash
# Upload files
aws s3 sync dist/ s3://expense-management-frontend/ \
  --delete \
  --cache-control "public, max-age=31536000" \
  --exclude "index.html"

# Upload index.html with no-cache
aws s3 cp dist/index.html s3://expense-management-frontend/index.html \
  --cache-control "no-cache, no-store, must-revalidate"
```

#### Step 4: Create CloudFront Distribution

```bash
# Create distribution (via AWS Console or CLI)
aws cloudfront create-distribution \
  --origin-domain-name expense-management-frontend.s3.amazonaws.com \
  --default-root-object index.html

# Note the CloudFront domain name
# Example: d1234567890abc.cloudfront.net
```

#### Step 5: Configure Custom Domain (Optional)

1. Request SSL certificate in AWS Certificate Manager
2. Add custom domain to CloudFront distribution
3. Update DNS records to point to CloudFront

### Option 2: Netlify Deployment

```bash
# Install Netlify CLI
npm install -g netlify-cli

# Login to Netlify
netlify login

# Deploy
cd frontend
netlify deploy --prod

# Follow prompts:
# - Build command: npm run build
# - Publish directory: dist

# Set environment variable in Netlify dashboard
# VITE_API_URL=https://api.yourdomain.com/api
```

### Option 3: Vercel Deployment

```bash
# Install Vercel CLI
npm install -g vercel

# Login to Vercel
vercel login

# Deploy
cd frontend
vercel --prod

# Set environment variable in Vercel dashboard
# VITE_API_URL=https://api.yourdomain.com/api
```

### Option 4: Serve with Nginx

```bash
# Install Nginx
sudo yum install nginx -y  # Amazon Linux
# or
sudo apt install nginx -y  # Ubuntu

# Copy build files
sudo cp -r dist/* /usr/share/nginx/html/

# Configure Nginx
sudo nano /etc/nginx/conf.d/expense-frontend.conf
```

Add configuration:

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend-server:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

```bash
# Test configuration
sudo nginx -t

# Start Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

## Environment Configuration

### Backend Environment Variables

Create a `.env` file or set system environment variables:

```bash
# Database Configuration
export DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
export DB_USERNAME="expenseadmin"
export DB_PASSWORD="YourSecurePassword123!"

# JWT Configuration
export JWT_SECRET="your-production-jwt-secret-minimum-32-characters"

# File Storage
export UPLOAD_DIR="/var/app/uploads"

# Optional: Server Configuration
export SERVER_PORT="8080"
```

See [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md) for complete reference.

### Frontend Environment Variables

Create `.env.production`:

```bash
VITE_API_URL=https://api.yourdomain.com/api
```

## Deployment Options Comparison

| Option | Backend | Frontend | Complexity | Cost | Scalability |
|--------|---------|----------|------------|------|-------------|
| EC2 + S3 | EC2 | S3 + CloudFront | Medium | Low | Manual |
| Elastic Beanstalk + S3 | EB | S3 + CloudFront | Low | Medium | Auto |
| ECS/Fargate + S3 | ECS | S3 + CloudFront | High | Medium | Auto |
| Docker Compose | Docker | Docker | Low | Very Low | Manual |
| Netlify/Vercel | EC2/EB | Netlify/Vercel | Low | Low | Auto |

### Docker Deployment

For a complete guide on deploying with Docker and Docker Compose, see [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md).

Quick start with Docker:
```bash
# Clone and navigate to project
git clone <repository-url>
cd expense-management-system

# Start all services
docker-compose up -d

# Verify
curl http://localhost:8080/api/health
```

## Post-Deployment Verification

### Backend Health Check

```bash
# Test health endpoint
curl https://api.yourdomain.com/api/health

# Expected response:
# {"status":"UP"}

# Test API documentation (if enabled)
curl https://api.yourdomain.com/api-docs

# Test authentication
curl -X POST https://api.yourdomain.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Frontend Verification

1. Open browser to your frontend URL
2. Test user registration
3. Test login
4. Create a test expense
5. Upload a receipt
6. Test all major features

### Database Verification

```bash
# Connect to database
psql -h your-rds-endpoint -U expenseadmin -d expensedb

# Check tables were created
\dt

# Check for data
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM categories;
SELECT COUNT(*) FROM expenses;

# Exit
\q
```

## Monitoring and Maintenance

### Application Monitoring

#### CloudWatch Logs (AWS)

```bash
# View backend logs
aws logs tail /aws/elasticbeanstalk/expense-api-prod/var/log/eb-engine.log --follow

# Create CloudWatch alarm for errors
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

#### Application Logs

```bash
# View systemd logs
sudo journalctl -u expense-api -f

# View Docker logs
docker logs -f expense-api

# View log file
tail -f /var/log/expense-management-api/application.log
```

### Database Monitoring

```bash
# Check RDS metrics
aws rds describe-db-instances \
  --db-instance-identifier expense-management-db \
  --query 'DBInstances[0].[DBInstanceStatus,AllocatedStorage,DBInstanceClass]'

# View slow query logs
aws rds download-db-log-file-portion \
  --db-instance-identifier expense-management-db \
  --log-file-name slowquery/postgresql.log
```

### Performance Monitoring

- Set up CloudWatch dashboards
- Monitor API response times
- Track database query performance
- Monitor memory and CPU usage
- Set up alerts for anomalies

### Regular Maintenance Tasks

- **Daily**: Review application logs for errors
- **Weekly**: Check CloudWatch metrics and alarms
- **Monthly**: Review and optimize database queries
- **Quarterly**: Update dependencies and security patches
- **Annually**: Review and rotate secrets (JWT, database passwords)

## Troubleshooting

### Backend Issues

#### Application Won't Start

```bash
# Check Java version
java -version

# Check environment variables
env | grep DB_
env | grep JWT_

# Check logs
sudo journalctl -u expense-api -n 100

# Test database connection
psql -h $RDS_ENDPOINT -U $DB_USERNAME -d $DB_NAME
```

#### Database Connection Errors

```bash
# Test network connectivity
telnet your-rds-endpoint 5432

# Check security group rules
aws ec2 describe-security-groups --group-ids sg-xxxxxxxxx

# Verify RDS is running
aws rds describe-db-instances \
  --db-instance-identifier expense-management-db \
  --query 'DBInstances[0].DBInstanceStatus'
```

#### High Memory Usage

```bash
# Check Java heap settings
ps aux | grep java

# Adjust JVM options
java -jar -Xms512m -Xmx1024m app.jar
```

### Frontend Issues

#### API Connection Errors

1. Check `VITE_API_URL` is correct
2. Verify backend is accessible
3. Check CORS configuration in backend
4. Review browser console for errors

#### Build Failures

```bash
# Clear cache and rebuild
rm -rf node_modules dist
npm install
npm run build
```

#### Routing Issues (404 on refresh)

Ensure your web server is configured to serve `index.html` for all routes:

**Nginx:**
```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

**S3 + CloudFront:**
Configure custom error response to serve `index.html` for 404 errors.

### Database Issues

#### Migration Failures

```bash
# Check migration status
mvn flyway:info

# Repair failed migration
mvn flyway:repair

# Run migrations manually
mvn flyway:migrate
```

#### Connection Pool Exhausted

Adjust connection pool settings in `application-prod.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
```

## Security Checklist

- [ ] JWT_SECRET is strong and unique (minimum 32 characters)
- [ ] Database password is strong and secure
- [ ] Environment variables are not in version control
- [ ] HTTPS is enabled for all endpoints
- [ ] Security groups restrict access appropriately
- [ ] API documentation is disabled in production
- [ ] Database backups are configured
- [ ] CloudWatch alarms are set up
- [ ] IAM roles follow least privilege principle
- [ ] Secrets are stored in AWS Secrets Manager
- [ ] Regular security updates are scheduled

## Rollback Procedure

### Backend Rollback

```bash
# Stop current version
sudo systemctl stop expense-api

# Deploy previous version
sudo cp /backup/expense-management-system-0.9.0.jar /home/ec2-user/expense-management-system-1.0.0.jar

# Start service
sudo systemctl start expense-api

# Verify
curl http://localhost:8080/api/health
```

### Frontend Rollback

```bash
# For S3 deployment
aws s3 sync s3://expense-frontend-backup/ s3://expense-management-frontend/ --delete

# Invalidate CloudFront cache
aws cloudfront create-invalidation \
  --distribution-id E1234567890ABC \
  --paths "/*"
```

### Database Rollback

```bash
# Restore from snapshot
aws rds restore-db-instance-from-db-snapshot \
  --db-instance-identifier expense-management-db-restored \
  --db-snapshot-identifier expense-db-snapshot-20231115
```

## Additional Resources

- [Backend Deployment Guide](PRODUCTION_DEPLOYMENT_GUIDE.md)
- [Environment Variables Reference](PRODUCTION_ENV_VARIABLES.md)
- [AWS RDS Setup Guide](aws-rds-setup.md)
- [Database Migrations Guide](DATABASE_MIGRATIONS.md)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [AWS Documentation](https://docs.aws.amazon.com/)
- [React Deployment Guide](https://vitejs.dev/guide/static-deploy.html)

## Support

For issues and questions:
- Review this deployment guide
- Check the troubleshooting section
- Review application logs
- Consult AWS documentation
- Create a GitHub issue with detailed information
