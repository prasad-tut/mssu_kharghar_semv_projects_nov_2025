# AWS Deployment Guide

Complete guide for deploying Bill Management System to AWS.

## Prerequisites

- AWS Account
- AWS CLI installed and configured
- Docker installed
- Domain name (optional)

## Architecture

```
CloudFront → S3 (Frontend)
    ↓
ALB → ECS Fargate (Backend) → RDS PostgreSQL
```

## Step 1: Setup AWS Infrastructure

### 1.1 Create RDS Database

```bash
aws rds create-db-instance \
  --db-instance-identifier bill-management-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 15.3 \
  --master-username postgres \
  --master-user-password YOUR_SECURE_PASSWORD \
  --allocated-storage 20 \
  --backup-retention-period 7 \
  --publicly-accessible false
```

### 1.2 Create ECR Repository

```bash
aws ecr create-repository \
  --repository-name bill-management \
  --region us-east-1
```

### 1.3 Create ECS Cluster

```bash
aws ecs create-cluster \
  --cluster-name bill-management-cluster \
  --region us-east-1
```

### 1.4 Create S3 Bucket for Frontend

```bash
aws s3 mb s3://bill-management-frontend

# Enable static website hosting
aws s3 website s3://bill-management-frontend \
  --index-document index.html

# Set bucket policy
aws s3api put-bucket-policy \
  --bucket bill-management-frontend \
  --policy file://s3-policy.json
```

## Step 2: Configure Environment

Update `.env` with production values:

```env
# Production Database
DB_HOST=your-rds-endpoint.rds.amazonaws.com
DB_PORT=5432
DB_NAME=billmanagement
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password

# Backend
BACKEND_PORT=8080
SPRING_PROFILE=prod

# Frontend
API_BASE_URL=https://your-domain.com/api

# AWS
AWS_REGION=us-east-1
AWS_ACCOUNT_ID=123456789012
ECR_REPO_NAME=bill-management
ECS_CLUSTER=bill-management-cluster
ECS_SERVICE=bill-management-service
S3_BUCKET=bill-management-frontend
```

## Step 3: Deploy

### 3.1 Update Task Definition

Edit `task-definition.json` with your values:
- AWS Account ID
- RDS endpoint
- Database password (use Secrets Manager)

### 3.2 Register Task Definition

```bash
aws ecs register-task-definition \
  --cli-input-json file://task-definition.json
```

### 3.3 Create ECS Service

```bash
aws ecs create-service \
  --cluster bill-management-cluster \
  --service-name bill-management-service \
  --task-definition bill-management-task \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=ENABLED}"
```

### 3.4 Run Deployment Script

```bash
./deploy-aws.sh
```

## Step 4: Setup Load Balancer

### 4.1 Create Application Load Balancer

```bash
aws elbv2 create-load-balancer \
  --name bill-management-alb \
  --subnets subnet-xxx subnet-yyy \
  --security-groups sg-xxx
```

### 4.2 Create Target Group

```bash
aws elbv2 create-target-group \
  --name bill-management-tg \
  --protocol HTTP \
  --port 8080 \
  --vpc-id vpc-xxx \
  --target-type ip \
  --health-check-path /actuator/health
```

## Step 5: Setup CloudFront (Optional)

```bash
aws cloudfront create-distribution \
  --origin-domain-name bill-management-frontend.s3.amazonaws.com \
  --default-root-object index.html
```

## Security Configuration

### Security Groups

**Backend Security Group:**
- Allow 8080 from ALB
- Allow 5432 to RDS

**Database Security Group:**
- Allow 5432 from Backend SG only

### IAM Roles

Create roles for:
- ECS Task Execution
- ECS Task (for accessing RDS, S3)

### Secrets Manager

```bash
aws secretsmanager create-secret \
  --name bill-management/db-credentials \
  --secret-string '{"username":"postgres","password":"your_password"}'
```

## Monitoring

### CloudWatch Logs

```bash
aws logs create-log-group \
  --log-group-name /ecs/bill-management
```

### CloudWatch Alarms

```bash
# CPU Alarm
aws cloudwatch put-metric-alarm \
  --alarm-name bill-management-high-cpu \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold
```

## CI/CD with Jenkins

### Jenkins Setup

1. Install plugins:
   - Docker Pipeline
   - AWS Steps
   - Maven Integration

2. Configure credentials:
   - AWS Access Key
   - Docker Registry

3. Create pipeline job pointing to `Jenkinsfile`

### Pipeline Stages

1. Checkout code
2. Build backend (Maven)
3. Run tests
4. Build Docker image
5. Push to ECR
6. Deploy to ECS
7. Deploy frontend to S3

## Rollback

### ECS Rollback

```bash
aws ecs update-service \
  --cluster bill-management-cluster \
  --service bill-management-service \
  --task-definition bill-management-task:PREVIOUS_VERSION
```

### Frontend Rollback

```bash
aws s3 sync s3://bill-management-frontend-backup/ \
  s3://bill-management-frontend/
```

## Cost Optimization

- Use t3.micro for RDS (free tier)
- Use Fargate Spot for non-production
- Enable S3 lifecycle policies
- Use CloudFront caching
- Set up auto-scaling

## Troubleshooting

### ECS Task Won't Start

1. Check CloudWatch logs
2. Verify security groups
3. Check task definition
4. Verify database connectivity

### Frontend Not Loading

1. Check S3 bucket policy
2. Verify CloudFront distribution
3. Check API URL in env-config.js

### Database Connection Failed

1. Verify RDS security group
2. Check database credentials
3. Ensure VPC configuration

## URLs After Deployment

- **Frontend**: https://your-cloudfront-url
- **Backend**: https://your-alb-url
- **Health Check**: https://your-alb-url/actuator/health

## Maintenance

### Regular Tasks

- Monitor CloudWatch logs
- Review security patches
- Update dependencies
- Backup database
- Review costs

### Scaling

```bash
# Update desired count
aws ecs update-service \
  --cluster bill-management-cluster \
  --service bill-management-service \
  --desired-count 4
```

## Support

For deployment issues:
- Check CloudWatch Logs
- Review ECS Service Events
- Verify ALB Target Health
- Check Security Groups
