# Production Deployment Guide

This guide provides step-by-step instructions for deploying the Expense Management System backend to production.

## Prerequisites

- Java 17 or higher installed
- Maven 3.6+ installed
- AWS RDS PostgreSQL database configured and accessible
- Required environment variables configured (see PRODUCTION_ENV_VARIABLES.md)

## Building the Production JAR

### Option 1: Using Build Scripts

#### Windows (PowerShell)
```powershell
.\build-production.ps1
```

#### Linux/Mac
```bash
chmod +x build-production.sh
./build-production.sh
```

### Option 2: Manual Maven Build

```bash
# Clean previous builds
mvn clean

# Build production JAR (skip tests for faster build)
mvn package -DskipTests

# Or build with tests
mvn package
```

The JAR file will be created at: `target/expense-management-system-1.0.0.jar`

## Running the Application

### Basic Command

```bash
java -jar -Dspring.profiles.active=prod target/expense-management-system-1.0.0.jar
```

### With Environment Variables (Linux/Mac)

```bash
export DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
export DB_USERNAME="your_username"
export DB_PASSWORD="your_password"
export JWT_SECRET="your-secure-jwt-secret-minimum-32-characters"

java -jar -Dspring.profiles.active=prod target/expense-management-system-1.0.0.jar
```

### With Environment Variables (Windows PowerShell)

```powershell
$env:DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb"
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your-secure-jwt-secret-minimum-32-characters"

java -jar -Dspring.profiles.active=prod target/expense-management-system-1.0.0.jar
```

### With Inline Environment Variables

```bash
DB_URL="jdbc:postgresql://..." \
DB_USERNAME="user" \
DB_PASSWORD="pass" \
JWT_SECRET="secret" \
java -jar -Dspring.profiles.active=prod target/expense-management-system-1.0.0.jar
```

## Deployment Options

### 1. AWS EC2 Deployment

#### Step 1: Launch EC2 Instance
- Choose Amazon Linux 2 or Ubuntu
- Instance type: t3.small or larger
- Configure security group to allow:
  - Port 8080 (or your chosen port) from your load balancer
  - Port 22 for SSH (restrict to your IP)

#### Step 2: Install Java
```bash
# Amazon Linux 2
sudo yum install java-17-amazon-corretto -y

# Ubuntu
sudo apt update
sudo apt install openjdk-17-jdk -y
```

#### Step 3: Upload JAR File
```bash
scp target/expense-management-system-1.0.0.jar ec2-user@your-ec2-ip:/home/ec2-user/
```

#### Step 4: Create Systemd Service
Create `/etc/systemd/system/expense-api.service`:

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

Environment="DB_URL=jdbc:postgresql://your-rds-endpoint:5432/expensedb"
Environment="DB_USERNAME=your_username"
Environment="DB_PASSWORD=your_password"
Environment="JWT_SECRET=your-jwt-secret"
Environment="UPLOAD_DIR=/var/app/uploads"

[Install]
WantedBy=multi-user.target
```

#### Step 5: Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable expense-api
sudo systemctl start expense-api
sudo systemctl status expense-api
```

### 2. AWS Elastic Beanstalk Deployment

#### Step 1: Install EB CLI
```bash
pip install awsebcli
```

#### Step 2: Initialize EB Application
```bash
eb init -p "Corretto 17" expense-management-api --region us-east-1
```

#### Step 3: Create Environment
```bash
eb create expense-api-prod --instance-type t3.small
```

#### Step 4: Set Environment Variables
```bash
eb setenv \
  DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb" \
  DB_USERNAME="your_username" \
  DB_PASSWORD="your_password" \
  JWT_SECRET="your-jwt-secret"
```

#### Step 5: Deploy
```bash
eb deploy
```

### 3. Docker Deployment

#### Step 1: Create Dockerfile
Create `Dockerfile` in project root:

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/expense-management-system-1.0.0.jar app.jar

RUN mkdir -p /var/app/uploads && \
    chmod 755 /var/app/uploads

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Step 2: Build Docker Image
```bash
docker build -t expense-management-api:1.0.0 .
```

#### Step 3: Run Container
```bash
docker run -d \
  --name expense-api \
  -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://your-rds-endpoint:5432/expensedb" \
  -e DB_USERNAME="your_username" \
  -e DB_PASSWORD="your_password" \
  -e JWT_SECRET="your-jwt-secret" \
  -v /var/app/uploads:/var/app/uploads \
  expense-management-api:1.0.0
```

### 4. AWS ECS/Fargate Deployment

#### Step 1: Push to ECR
```bash
# Create ECR repository
aws ecr create-repository --repository-name expense-management-api

# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag expense-management-api:1.0.0 <account-id>.dkr.ecr.us-east-1.amazonaws.com/expense-management-api:1.0.0
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/expense-management-api:1.0.0
```

#### Step 2: Create ECS Task Definition
Use AWS Console or CLI to create task definition with:
- Container image from ECR
- Environment variables for DB and JWT
- Port mapping 8080:8080
- CloudWatch logs configuration

#### Step 3: Create ECS Service
- Choose Fargate launch type
- Configure load balancer
- Set desired task count
- Configure auto-scaling

## Health Check Verification

After deployment, verify the application is running:

```bash
# Check health endpoint
curl http://your-server:8080/api/health

# Expected response:
# {"status":"UP"}

# Check API documentation (if enabled)
curl http://your-server:8080/api-docs

# Check Swagger UI (if enabled)
# Open in browser: http://your-server:8080/swagger-ui.html
```

## Database Migration

The application uses Flyway for database migrations. Migrations run automatically on startup.

### Manual Migration (if needed)
```bash
# Run migrations manually
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://... -Dflyway.user=... -Dflyway.password=...
```

## Monitoring and Logging

### View Application Logs

#### Systemd Service
```bash
sudo journalctl -u expense-api -f
```

#### Docker Container
```bash
docker logs -f expense-api
```

#### Log File (if configured)
```bash
tail -f /var/log/expense-management-api/application.log
```

### Health Check Endpoints

- **Health**: `GET /actuator/health`
- **Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

### CloudWatch Integration (AWS)

Configure CloudWatch agent to collect logs and metrics:

```bash
# Install CloudWatch agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
sudo rpm -U ./amazon-cloudwatch-agent.rpm

# Configure and start agent
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config \
  -m ec2 \
  -s \
  -c file:/opt/aws/amazon-cloudwatch-agent/etc/config.json
```

## Troubleshooting

### Application Won't Start

1. **Check Java version**
   ```bash
   java -version
   # Should be Java 17 or higher
   ```

2. **Check environment variables**
   ```bash
   echo $DB_URL
   echo $JWT_SECRET
   ```

3. **Check database connectivity**
   ```bash
   psql -h your-rds-endpoint -U your_username -d expensedb
   ```

4. **Check logs**
   ```bash
   # Look for error messages in logs
   tail -100 /var/log/expense-management-api/application.log
   ```

### Database Connection Errors

1. **Verify RDS security group**
   - Ensure inbound rule allows PostgreSQL (port 5432) from application server

2. **Test connection**
   ```bash
   telnet your-rds-endpoint 5432
   ```

3. **Check credentials**
   - Verify username and password are correct
   - Check database name exists

### JWT Token Errors

1. **Verify JWT_SECRET is set**
   ```bash
   echo $JWT_SECRET
   ```

2. **Ensure secret is at least 32 characters**

3. **Check token expiration settings**

### File Upload Errors

1. **Check upload directory exists**
   ```bash
   ls -la /var/app/uploads
   ```

2. **Check permissions**
   ```bash
   sudo chmod 755 /var/app/uploads
   sudo chown ec2-user:ec2-user /var/app/uploads
   ```

3. **Check disk space**
   ```bash
   df -h
   ```

## Security Checklist

- [ ] JWT_SECRET is a strong, random string (minimum 32 characters)
- [ ] Database password is strong and secure
- [ ] Environment variables are not committed to version control
- [ ] HTTPS is enabled (use load balancer or reverse proxy)
- [ ] Security groups restrict access appropriately
- [ ] API documentation endpoints are disabled in production
- [ ] Error messages don't expose sensitive information
- [ ] Database backups are configured
- [ ] CloudWatch alarms are set up for critical metrics
- [ ] IAM roles follow principle of least privilege

## Performance Tuning

### JVM Options

For production, consider these JVM options:

```bash
java -jar \
  -Xms512m \
  -Xmx1024m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Dspring.profiles.active=prod \
  target/expense-management-system-1.0.0.jar
```

### Database Connection Pool

Adjust in `application-prod.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
```

## Rollback Procedure

If deployment fails:

1. **Stop the new version**
   ```bash
   sudo systemctl stop expense-api
   ```

2. **Deploy previous version**
   ```bash
   sudo cp /backup/expense-management-system-1.0.0.jar /home/ec2-user/
   ```

3. **Start service**
   ```bash
   sudo systemctl start expense-api
   ```

4. **Verify health**
   ```bash
   curl http://localhost:8080/api/health
   ```

## Backup and Recovery

### Database Backups

AWS RDS provides automated backups. Configure:
- Backup retention period: 7-30 days
- Backup window: During low-traffic hours
- Enable automated snapshots

### Application Backups

- Keep previous JAR versions
- Store in S3 or artifact repository
- Tag releases in Git

## Support and Maintenance

### Regular Maintenance Tasks

- Monitor application logs daily
- Review CloudWatch metrics weekly
- Update dependencies monthly
- Rotate secrets quarterly
- Review and optimize database queries
- Clean up old receipt files

### Scaling Considerations

- Use Application Load Balancer for multiple instances
- Configure auto-scaling based on CPU/memory metrics
- Consider read replicas for database if needed
- Use CloudFront for static content delivery
- Implement caching (Redis/ElastiCache) for frequently accessed data

## Additional Resources

- [Spring Boot Production-Ready Features](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [AWS RDS Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html)
- [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elasticbeanstalk/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
