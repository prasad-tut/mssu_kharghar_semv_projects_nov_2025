# Commands Reference

Quick reference for all commands used in the Appointment Management System.

---

## Maven Commands

### Build & Package

```bash
# Clean build
mvn clean

# Compile
mvn compile

# Package (create JAR)
mvn package

# Clean and package
mvn clean package

# Package without tests
mvn clean package -DskipTests

# Install to local repository
mvn install
```

### Running Application

```bash
# Run with Maven
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Run with Maven Wrapper (Windows)
mvnw.cmd spring-boot:run

# Run with Maven Wrapper (Mac/Linux)
./mvnw spring-boot:run

# Run JAR directly
java -jar target/restapi_app-0.0.1-SNAPSHOT.jar

# Run JAR with profile
java -jar -Dspring.profiles.active=prod target/restapi_app-0.0.1-SNAPSHOT.jar
```

### Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AppointmentControllerTest

# Run tests with coverage
mvn test jacoco:report

# Skip tests during build
mvn package -DskipTests

# Run tests in debug mode
mvn test -Dmaven.surefire.debug
```

### Dependency Management

```bash
# Download dependencies
mvn dependency:resolve

# Display dependency tree
mvn dependency:tree

# Check for updates
mvn versions:display-dependency-updates

# Purge local repository
mvn dependency:purge-local-repository
```

---

## Docker Commands

### Basic Docker

```bash
# Build image
docker build -t appointment-app .

# Build with tag
docker build -t appointment-app:v1.0 .

# Run container
docker run -p 9080:9080 appointment-app

# Run in detached mode
docker run -d -p 9080:9080 appointment-app

# Run with environment variables
docker run -p 9080:9080 -e SPRING_PROFILES_ACTIVE=prod appointment-app

# Stop container
docker stop <container-id>

# Remove container
docker rm <container-id>

# Remove image
docker rmi appointment-app

# View logs
docker logs <container-id>

# Follow logs
docker logs -f <container-id>

# Execute command in container
docker exec -it <container-id> /bin/sh

# List running containers
docker ps

# List all containers
docker ps -a

# List images
docker images

# Remove unused images
docker image prune

# Remove all stopped containers
docker container prune
```

### Docker Compose

```bash
# Start all services
docker-compose up

# Start in detached mode
docker-compose up -d

# Start specific service
docker-compose up app

# Build and start
docker-compose up --build

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View logs
docker-compose logs

# Follow logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f app

# Restart services
docker-compose restart

# Restart specific service
docker-compose restart app

# View running services
docker-compose ps

# Execute command in service
docker-compose exec app /bin/sh

# Scale service
docker-compose up -d --scale app=3

# Validate compose file
docker-compose config

# Pull latest images
docker-compose pull
```

### Development Docker Compose

```bash
# Start only database
docker-compose -f docker-compose.dev.yml up -d

# Stop database
docker-compose -f docker-compose.dev.yml down

# View database logs
docker-compose -f docker-compose.dev.yml logs -f mysql
```

---

## Database Commands

### MySQL

```bash
# Connect to MySQL
mysql -u admin -p

# Connect to specific database
mysql -u admin -p appointmentdb

# Connect to remote MySQL
mysql -h hostname -u admin -p appointmentdb

# Connect to AWS RDS
mysql -h your-rds-endpoint.region.rds.amazonaws.com -u admin -p appointmentdb

# Execute SQL file
mysql -u admin -p appointmentdb < schema.sql

# Dump database
mysqldump -u admin -p appointmentdb > backup.sql

# Restore database
mysql -u admin -p appointmentdb < backup.sql

# Show databases
mysql -u admin -p -e "SHOW DATABASES;"

# Show tables
mysql -u admin -p appointmentdb -e "SHOW TABLES;"

# Describe table
mysql -u admin -p appointmentdb -e "DESCRIBE appointments;"
```

### MySQL Commands (Inside MySQL)

```sql
-- Show databases
SHOW DATABASES;

-- Create database
CREATE DATABASE appointmentdb;

-- Use database
USE appointmentdb;

-- Show tables
SHOW TABLES;

-- Describe table
DESCRIBE appointments;

-- Show table structure
SHOW CREATE TABLE appointments;

-- Select all appointments
SELECT * FROM appointments;

-- Count appointments
SELECT COUNT(*) FROM appointments;

-- Filter by status
SELECT * FROM appointments WHERE status = 'Scheduled';

-- Order by date
SELECT * FROM appointments ORDER BY appointment_date DESC;

-- Delete all data
TRUNCATE TABLE appointments;

-- Drop table
DROP TABLE appointments;

-- Create user
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'password';

-- Grant privileges
GRANT ALL PRIVILEGES ON appointmentdb.* TO 'admin'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;

-- Show users
SELECT User, Host FROM mysql.user;

-- Exit
EXIT;
```

### H2 Database

```bash
# Access H2 Console
# Navigate to: http://localhost:9080/h2-console

# Connection settings:
# JDBC URL: jdbc:h2:mem:appointmentdb
# Username: sa
# Password: (leave empty)
```

### Docker MySQL

```bash
# Connect to MySQL in Docker
docker exec -it appointment-mysql mysql -u admin -p

# Execute SQL in Docker container
docker exec -i appointment-mysql mysql -u admin -p appointmentdb < schema.sql

# Backup from Docker
docker exec appointment-mysql mysqldump -u admin -p appointmentdb > backup.sql

# Restore to Docker
docker exec -i appointment-mysql mysql -u admin -p appointmentdb < backup.sql
```

---

## AWS Commands

### AWS CLI Configuration

```bash
# Configure AWS CLI
aws configure

# Set region
aws configure set region us-east-1

# List configurations
aws configure list

# Use specific profile
aws --profile myprofile s3 ls
```

### AWS RDS

```bash
# Create RDS instance
aws rds create-db-instance \
    --db-instance-identifier appointment-db \
    --db-instance-class db.t3.micro \
    --engine mysql \
    --master-username admin \
    --master-user-password YourPassword \
    --allocated-storage 20

# Describe RDS instances
aws rds describe-db-instances

# Get specific instance
aws rds describe-db-instances --db-instance-identifier appointment-db

# Modify RDS instance
aws rds modify-db-instance \
    --db-instance-identifier appointment-db \
    --allocated-storage 30

# Delete RDS instance
aws rds delete-db-instance \
    --db-instance-identifier appointment-db \
    --skip-final-snapshot

# Create snapshot
aws rds create-db-snapshot \
    --db-instance-identifier appointment-db \
    --db-snapshot-identifier appointment-snapshot
```

### AWS Elastic Beanstalk

```bash
# Initialize EB
eb init

# Create environment
eb create appointment-env

# Deploy application
eb deploy

# Open application in browser
eb open

# View status
eb status

# View logs
eb logs

# Set environment variables
eb setenv SPRING_PROFILES_ACTIVE=prod

# SSH into instance
eb ssh

# Terminate environment
eb terminate appointment-env

# List environments
eb list

# Use specific environment
eb use appointment-env

# Scale environment
eb scale 2

# Restart application
eb restart
```

### AWS ECR (Container Registry)

```bash
# Create repository
aws ecr create-repository --repository-name appointment-management

# Get login command
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag image
docker tag appointment-app:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/appointment-management:latest

# Push image
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/appointment-management:latest

# List images
aws ecr list-images --repository-name appointment-management

# Delete image
aws ecr batch-delete-image \
    --repository-name appointment-management \
    --image-ids imageTag=latest
```

---

## Git Commands

### Basic Git

```bash
# Initialize repository
git init

# Clone repository
git clone <repository-url>

# Check status
git status

# Add files
git add .
git add filename

# Commit changes
git commit -m "Commit message"

# Push to remote
git push origin main

# Pull from remote
git pull origin main

# View commit history
git log

# View branches
git branch

# Create branch
git branch feature-name

# Switch branch
git checkout feature-name

# Create and switch branch
git checkout -b feature-name

# Merge branch
git merge feature-name

# Delete branch
git branch -d feature-name

# View remote
git remote -v

# Add remote
git remote add origin <repository-url>
```

---

## API Testing Commands

### cURL

```bash
# Get all appointments
curl http://localhost:9080/appointments/get

# Get appointment by ID
curl http://localhost:9080/appointments/get/1

# Create appointment
curl -X POST http://localhost:9080/appointments/add \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 104,
    "providerId": 204,
    "serviceId": 304,
    "appointmentDate": "2025-11-25",
    "appointmentTime": "15:00",
    "status": "Scheduled"
  }'

# Update appointment
curl -X PUT http://localhost:9080/appointments/edit \
  -H "Content-Type: application/json" \
  -d '{
    "appointmentId": 1,
    "clientId": 101,
    "providerId": 201,
    "serviceId": 301,
    "appointmentDate": "2025-11-26",
    "appointmentTime": "16:00",
    "status": "Confirmed"
  }'

# Delete appointment
curl -X DELETE http://localhost:9080/appointments/delete/1

# Pretty print JSON response
curl http://localhost:9080/appointments/get | json_pp

# Save response to file
curl http://localhost:9080/appointments/get > appointments.json

# View response headers
curl -i http://localhost:9080/appointments/get

# Verbose output
curl -v http://localhost:9080/appointments/get
```

---

## System Commands

### Windows

```bash
# Check port usage
netstat -ano | findstr :9080

# Kill process by PID
taskkill /PID <PID> /F

# Check Java version
java -version

# Check Maven version
mvn -version

# Set environment variable
set SPRING_PROFILES_ACTIVE=prod

# View environment variables
set

# Create directory
mkdir directory-name

# Remove directory
rmdir /s directory-name

# Copy file
copy source.txt destination.txt

# View file
type filename.txt
```

### Mac/Linux

```bash
# Check port usage
lsof -i :9080

# Kill process by PID
kill -9 <PID>

# Check Java version
java -version

# Check Maven version
mvn -version

# Set environment variable
export SPRING_PROFILES_ACTIVE=prod

# View environment variables
env

# Create directory
mkdir directory-name

# Remove directory
rm -rf directory-name

# Copy file
cp source.txt destination.txt

# View file
cat filename.txt

# Make script executable
chmod +x script.sh

# Run script
./script.sh
```

---

## Troubleshooting Commands

```bash
# Check if application is running
curl http://localhost:9080/appointments/get

# Check Java processes
jps

# View Java process details
jps -v

# Check system resources
# Windows: Task Manager (Ctrl+Shift+Esc)
# Mac: Activity Monitor
# Linux: top or htop

# Clear Maven cache
mvn dependency:purge-local-repository

# Clear Docker cache
docker system prune -a

# Check disk space
# Windows: dir
# Mac/Linux: df -h

# Check memory usage
# Windows: systeminfo
# Mac/Linux: free -h
```

---

## Quick Reference

### Start Application

```bash
# Development (H2)
mvnw spring-boot:run

# Docker
docker-compose up -d

# Production JAR
java -jar target/restapi_app-0.0.1-SNAPSHOT.jar
```

### Stop Application

```bash
# Maven: Ctrl+C

# Docker
docker-compose down

# Kill process
# Windows: taskkill /F /IM java.exe
# Mac/Linux: pkill java
```

### View Logs

```bash
# Maven: Console output

# Docker
docker-compose logs -f app

# JAR: Check console or log file
```

### Deploy

```bash
# AWS
./deploy-aws.sh  # Mac/Linux
deploy-aws.bat   # Windows

# Docker
docker-compose up -d --build
```

---

**Last Updated:** November 2025
