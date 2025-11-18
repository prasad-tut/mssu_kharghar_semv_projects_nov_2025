# Quick Start Guide

Get the Appointment Management System running in under 5 minutes.

## Prerequisites

- Java 21+
- Maven 3.6+
- Docker (optional, for containerized deployment)

---

## Option 1: Run with H2 Database (Fastest)

Perfect for development and testing. No external database needed.

```bash
# Clone repository
git clone <repository-url>
cd restapi_app

# Run application
mvnw spring-boot:run
```

**Access:**
- Frontend: http://localhost:9080
- H2 Console: http://localhost:9080/h2-console
  - JDBC URL: `jdbc:h2:mem:appointmentdb`
  - Username: `sa`
  - Password: (leave empty)

---

## Option 2: Run with Docker

Includes MySQL database and application in containers.

```bash
# Start everything
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop everything
docker-compose down
```

**Access:**
- Frontend: http://localhost:9080
- API: http://localhost:9080/appointments/get

---

## Option 3: Run with Local MySQL

If you have MySQL installed locally.

### Step 1: Setup Database

```bash
# Connect to MySQL
mysql -u root -p

# Create database and user
CREATE DATABASE appointmentdb;
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON appointmentdb.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
USE appointmentdb;

# Run schema
SOURCE src/main/resources/schema.sql;
SOURCE src/main/resources/data.sql;
```

### Step 2: Update Configuration

Edit `src/main/resources/application.properties`:

```properties
server.port=9080

# Comment out H2 configuration
#spring.datasource.url=jdbc:h2:mem:appointmentdb
#spring.datasource.driver-class-name=org.h2.Driver
#spring.datasource.username=sa
#spring.datasource.password=
#spring.h2.console.enabled=true

# Uncomment MySQL configuration
spring.datasource.url=jdbc:mysql://localhost:3306/appointmentdb?useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=admin
spring.datasource.password=password
```

### Step 3: Run Application

```bash
mvnw spring-boot:run
```

---

## Test the API

### Using Browser
Navigate to: http://localhost:9080

### Using curl

```bash
# Get all appointments
curl http://localhost:9080/appointments/get

# Get specific appointment
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
```

### Using Postman

1. Import collection from API documentation
2. Set base URL: `http://localhost:9080`
3. Test endpoints

---

## Common Issues

### Port 9080 already in use

```bash
# Windows
netstat -ano | findstr :9080
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :9080
kill -9 <PID>
```

### Java version mismatch

```bash
# Check Java version
java -version

# Should be 21 or higher
# If not, install Java 21
```

### Maven not found

```bash
# Use Maven wrapper instead
./mvnw spring-boot:run  # Mac/Linux
mvnw.cmd spring-boot:run  # Windows
```

### Database connection failed

```bash
# For H2: Check if application.properties has correct H2 config
# For MySQL: Verify MySQL is running and credentials are correct

# Test MySQL connection
mysql -u admin -p appointmentdb
```

---

## Next Steps

- Read [README.md](README.md) for full documentation
- See [DEPLOYMENT.md](DEPLOYMENT.md) for production deployment
- Check API endpoints in README
- Explore the web interface at http://localhost:9080

---

## Quick Commands Reference

```bash
# Build project
mvn clean package

# Run tests
mvn test

# Run application
mvn spring-boot:run

# Build Docker image
docker build -t appointment-app .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop Docker services
docker-compose down

# Deploy to AWS
./deploy-aws.sh  # Mac/Linux
deploy-aws.bat   # Windows
```

---

## Support

Having issues? Check:
1. This quick start guide
2. [README.md](README.md) - Full documentation
3. [DEPLOYMENT.md](DEPLOYMENT.md) - Deployment guides
4. GitHub Issues

---

**Team:** Ishita Parkar, Kirshnandu Gurey, Prutha Jadhav, and Yash Adhau
