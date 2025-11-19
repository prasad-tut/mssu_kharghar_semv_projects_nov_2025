# 🐛 Bug Tracker System

[![Java](https://img.shields.io/badge/Java-21-blue?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange?logo=mysql)](https://www.mysql.com/)
[![AWS RDS](https://img.shields.io/badge/AWS%20RDS-MySQL-FF9900?logo=amazon-aws)](https://aws.amazon.com/rds/)
[![Apache Tomcat](https://img.shields.io/badge/Tomcat-10.1.16-red?logo=apache-tomcat)](https://tomcat.apache.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.0-blueviolet?logo=bootstrap)](https://getbootstrap.com/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

A **production-ready bug tracking system** built with **Spring Boot 3.2.0**, **Spring JDBC**, **Thymeleaf**, **Bootstrap 5**, and **AWS RDS MySQL**. Designed for software development teams to efficiently report, track, and manage bugs throughout the development lifecycle with a modern, responsive web interface.

---

## 👥 Team Members

| Name | Role |
|------|------|
| **Vijay Kadam** | Lead Developer |
| **Kartik Devadiga** | Backend Developer |
| **Gaurav Ghanekar** | Database Administrator |
| **Risha Badgujar** | Frontend Developer |
| **Shreya Shelar** | QA & Testing |

**Project Type:** Software Engineering Project Submission 2025

---

## ✨ Key Features

### 🎯 Core Functionality
- **Complete CRUD Operations** - Create, read, update, and delete bugs with full lifecycle management
- **Priority Classification** - Three-tier priority system (HIGH, MEDIUM, LOW)
- **Severity Tracking** - Four-level severity classification (CRITICAL, MARGINAL, NEGLIGIBLE)
- **Status Workflow** - Track bugs through lifecycle: OPEN → IN_PROGRESS → FIXED
- **Team Assignment** - Assign bugs to team members for accountability and tracking
- **Timestamp Management** - Automatic creation/modification timestamps with custom detection dates

### 📊 Dashboard & Analytics
- **Real-Time Statistics** - Live dashboard showing total, open, in-progress, and fixed bugs
- **Interactive UI** - Responsive, Bootstrap 5-based design for all devices
- **User-Friendly Forms** - Intuitive bug reporting and update forms
- **Error Handling** - Custom 404 and 500 error pages

### 🔌 API & Integration
- **REST API Endpoints** - Three endpoints for JSON-based bug management
- **Programmatic Access** - Full CRUD operations via HTTP API
- **JSON Response Format** - Standard JSON for seamless integration

### 💾 Database Features
- **AWS RDS MySQL 8.0+** - Cloud-hosted, fully managed database
- **Automatic Schema** - Database automatically initialized on application startup
- **Connection Pooling** - HikariCP configured for optimal performance (max: 10, min-idle: 5)
- **Data Integrity** - Constraints and indexes for data validation and performance
- **Timestamps** - Automatic created_at and updated_at fields with MySQL triggers

### ⚡ Development Features
- **Hot Reload Support** - Spring DevTools for rapid development cycles
- **Configuration Externalization** - Environment-based configuration via properties file
- **Database Initialization** - Automatic schema.sql execution on startup
- **Dev Mode Optimization** - Thymeleaf caching disabled for quick template updates

---

## 🛠 Technology Stack

### Backend Architecture
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Framework** | Spring Boot | 3.2.0 | Application framework |
| **Language** | Java | 21 LTS (Temurin) | Programming language |
| **Web Server** | Apache Tomcat | 10.1.16 | Embedded application server |
| **Core Framework** | Spring Framework | 6.1.1 | Dependency injection & core |
| **Database Access** | Spring JDBC | 3.2.0 | Direct JDBC operations |
| **Connection Pool** | HikariCP | Latest | Database connection pooling |

### Database & Persistence
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Database** | MySQL | 8.0.33+ | Relational database |
| **Hosting** | AWS RDS | Managed | Cloud-hosted database |
| **Schema** | SQL | Custom | Bug tracking schema |
| **Driver** | MySQL Connector/J | 8.0.33 | Java-MySQL bridge |

### Frontend & UI
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Template Engine** | Thymeleaf | 3.1.1 | Server-side HTML rendering |
| **CSS Framework** | Bootstrap | 5.3.0 | Responsive UI components |
| **Markup** | HTML5 | - | Page structure |
| **Scripting** | JavaScript ES6+ | - | Client-side interactions |
| **Styling** | CSS3 | - | Custom styling |

### Build & DevOps
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Build Tool** | Maven | 3.6+ | Project build automation |
| **Package Format** | JAR | Standalone | Executable application |
| **Containerization** | Docker | Optional | Container deployment |
| **Orchestration** | Docker Compose | Optional | Multi-container management |

---

## 📋 Quick Start (30 Seconds)

### ⚡ Fastest Way - Run Pre-Built JAR

```bash
cd C:\Users\vijay\Desktop\Bug_tacker\target
C:\java\jdk-21.0.1+12\bin\java -jar bug-tracker-1.0.0.jar
```

**Access application in browser:**
- **Dashboard:** http://localhost:8080
- **API:** http://localhost:8080/api/bugs

### Prerequisites for All Methods

```
Java 21+ installed (check with: java -version)
Maven 3.6+ (optional, for building from source)
```

---

## 🔧 Install Maven (Windows)

If Maven is not installed, follow these steps:

### Step 1: Download and Install Maven

```powershell
# Run this PowerShell command as Administrator
$mavenVersion = "3.9.6"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$mavenDir = "C:\maven"
$zipFile = "$env:TEMP\maven.zip"

Write-Host "Downloading Maven $mavenVersion..."
Invoke-WebRequest -Uri $mavenUrl -OutFile $zipFile

Write-Host "Extracting Maven..."
Expand-Archive -Path $zipFile -DestinationPath $env:TEMP -Force
Move-Item -Path "$env:TEMP\apache-maven-$mavenVersion" -Destination $mavenDir -Force
Remove-Item $zipFile

Write-Host "Maven installed successfully at $mavenDir"
```

### Step 2: Set Environment Variables

```powershell
# Set JAVA_HOME (if not already set)
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\java\jdk-21.0.1+12", [EnvironmentVariableTarget]::User)

# Add Maven to PATH
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\maven\bin", [EnvironmentVariableTarget]::User)
```

### Step 3: Verify Installation

```bash
mvn --version
```

You should see:
```
Apache Maven 3.9.6
Java version: 21.0.1
```

---

## 🚀 Run the Project

### Option 1: Using Pre-Built JAR (Fastest - No Maven Needed)

```bash
C:\java\jdk-21.0.1+12\bin\java -jar C:\Users\vijay\Desktop\Bug_tacker\target\bug-tracker-1.0.0.jar
```

### Option 2: Using Maven (Recommended for Development)

```powershell
# Set environment variables for this session
$env:JAVA_HOME = "C:\java\jdk-21.0.1+12"
$env:Path = "C:\maven\bin;$env:Path"

# Navigate to project directory
cd C:\Users\username\Desktop\Bug_tacker

# Run the application
mvn spring-boot:run
```

### Option 3: Build and Run as JAR

```bash
cd C:\Users\username\Desktop\Bug_tacker
mvn clean package -DskipTests
java -jar target/bug-tracker-1.0.0.jar
```

### Option 4: Using Docker Compose

```bash
cd C:\Users\username\Desktop\Bug_tacker
docker-compose up -d
```

---

## Alternative: Build from Source

```bash
# Clone repository
git clone https://github.com/yourusername/bug-tracker.git
cd bug-tracker

# Build with Maven
mvn clean package

# Run the built JAR
java -jar target/bug-tracker-1.0.0.jar
```

---

## 📁 Project Structure

```
bug-tracker/
│
├── 📄 pom.xml                          # Maven dependencies & build config
├── 📄 README.md                        # This file
├── 📄 ARCHITECTURE.md                  # System design documentation
├── 📄 CONTRIBUTING.md                  # Contribution guidelines
├── 📄 LICENSE                          # MIT License
│
├── src/main/java/com/example/bugtracker/
│   │
│   ├── 📄 BugTrackerApplication.java   # Spring Boot entry point
│   │
│   ├── 📂 controller/
│   │   └── 📄 BugController.java       # HTTP request handler (10 routes)
│   │
│   ├── 📂 service/
│   │   └── 📄 BugService.java          # Business logic & validation
│   │
│   ├── 📂 repository/
│   │   └── 📄 BugRepository.java       # JDBC data access layer
│   │
│   └── 📂 model/
│       └── 📄 Bug.java                 # Domain model
│
├── src/main/resources/
│   │
│   ├── 📄 application.properties       # Configuration (40+ settings)
│   ├── 📄 schema.sql                   # Database schema
│   │
│   ├── 📂 templates/                   # Thymeleaf HTML templates
│   │   ├── 📄 base.html                # Master template
│   │   ├── 📄 bugs.html                # Dashboard
│   │   ├── 📄 raise_bug.html           # Bug reporting form
│   │   ├── 📄 update_bug.html          # Bug update form
│   │   ├── 📄 404.html                 # Not Found page
│   │   └── 📄 500.html                 # Server Error page
│   │
│   └── 📂 static/                      # Static assets
│       ├── 📄 styles.css               # Custom CSS
│       └── 📄 script.js                # JavaScript
│
├── target/
│   └── 📦 bug-tracker-1.0.0.jar        # Compiled application (24.59 MB)
│
├── RDS/
│   └── 📄 init.sql                     # Database initialization
│
└── 📄 docker-compose.yml               # Optional Docker configuration
```

---

## 📊 Database Schema

### Bugs Table
```sql
CREATE TABLE bugs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(1000) NOT NULL,
    priority ENUM('HIGH', 'MEDIUM', 'LOW') NOT NULL,
    severity ENUM('CRITICAL', 'MARGINAL', 'NEGLIGIBLE') NOT NULL,
    detected_on DATE NOT NULL,
    assigned_to VARCHAR(100),
    status ENUM('OPEN', 'IN_PROGRESS', 'FIXED') DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Performance Indexes
CREATE INDEX idx_status ON bugs(status);
CREATE INDEX idx_priority ON bugs(priority);
CREATE INDEX idx_severity ON bugs(severity);
CREATE INDEX idx_assigned_to ON bugs(assigned_to);
```

**Details:**
- Character Set: utf8mb4 (Unicode support)
- Constraints: ENUM validation on priority, severity, status
- Auto-Timestamp: created_at, updated_at fields
- Indexes: 4 strategic indexes for query performance

---

## 📡 REST API Endpoints

### Web Interface Routes
| Method | Route | Description |
|--------|-------|-------------|
| **GET** | `/bugs` | Dashboard with statistics |
| **GET** | `/raise-bug` | Bug reporting form |
| **POST** | `/raise-bug` | Submit new bug |
| **GET** | `/update-bug/{id}` | Bug update form |
| **POST** | `/update-bug/{id}` | Update bug status |

### REST API Routes
| Method | Endpoint | Description |
|--------|----------|-------------|
| **GET** | `/api/bugs` | Get all bugs (JSON) |
| **GET** | `/api/bug/{id}` | Get bug by ID |
| **GET** | `/api/bugs/status/{status}` | Filter by status |

### Example API Calls

```bash
# Get all bugs
curl http://localhost:8080/api/bugs

# Get specific bug
curl http://localhost:8080/api/bug/1

# Filter by status
curl http://localhost:8080/api/bugs/status/OPEN
curl http://localhost:8080/api/bugs/status/IN_PROGRESS
curl http://localhost:8080/api/bugs/status/FIXED
```

---

## ⚙️ Configuration

### Database Settings
```properties
# AWS RDS Connection (Use Environment Variables)
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:bug_tracker}?useSSL=false&serverTimezone=UTC
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:your_password_here}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.max-lifetime=1200000
```

### Server Settings
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Thymeleaf (Development Mode)
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

### Application Settings
```properties
# Logging
logging.level.root=INFO
logging.level.com.example.bugtracker=DEBUG

# Application Info
spring.application.name=bug-tracker
app.name=Bug Tracker System
app.version=1.0.0
```

---

## 🚀 Architecture & Design Patterns

### Layered Architecture
```
┌─────────────────────────────────────┐
│      Web Layer (Controller)          │
│   HTTP ↔ HTML/JSON Responses       │
├─────────────────────────────────────┤
│   Business Logic Layer (Service)     │
│   Validation & Business Rules       │
├─────────────────────────────────────┤
│   Data Access Layer (Repository)     │
│   JDBC Operations & Queries         │
├─────────────────────────────────────┤
│   Domain Model Layer                 │
│   Entity Definitions                │
├─────────────────────────────────────┤
│   Database Layer                     │
│   MySQL 8.0+ (AWS RDS)              │
└─────────────────────────────────────┘
```

### Design Patterns Implemented
1. **MVC Pattern** - Separation of concerns
2. **Repository Pattern** - Data access abstraction
3. **Service Layer Pattern** - Business logic encapsulation
4. **Dependency Injection** - Spring-managed beans
5. **DAO Pattern** - Database access objects
6. **Factory Pattern** - Spring bean creation

---

## 🔧 Development Setup

### Option 1: JAR File (Easiest)
```bash
cd target
java -jar bug-tracker-1.0.0.jar
```

### Option 2: Maven (Recommended for Development)
```powershell
# First time setup - Install Maven (see above)
# Set environment variables
$env:JAVA_HOME = "C:\java\jdk-21.0.1+12"
$env:Path = "C:\maven\bin;$env:Path"

# Navigate to project
cd C:\Users\vijay\Desktop\Bug_tacker

# Run application
mvn spring-boot:run
```

**Benefits of using Maven:**
- Hot-reload support via Spring DevTools (automatic restart on file changes)
- Live browser reload on port 35729
- Quick startup (~7 seconds)
- Direct source code execution (no JAR build needed)

### Option 3: IDE (IntelliJ IDEA or Eclipse)
```
1. Open project in IntelliJ IDEA or Eclipse
2. Maven automatically resolves dependencies
3. Right-click BugTrackerApplication.java → Run
4. Application starts in IDE debug mode
```

### Option 4: Docker
```bash
docker-compose up -d
```

---

## 📝 Maven Quick Reference

```bash
# Build project without running
mvn clean package -DskipTests

# Run tests
mvn test

# Run application
mvn spring-boot:run

# View dependency tree
mvn dependency:tree

# Clean build artifacts
mvn clean
```

---

## 🐛 Troubleshooting

### Issue: Java Command Not Found
```bash
# Use full path
C:\java\jdk-21.0.1+12\bin\java -jar bug-tracker-1.0.0.jar

# OR add to PATH permanently
setx PATH "%PATH%;C:\java\jdk-21.0.1+12\bin"
```

### Issue: Port 8080 Already in Use
```bash
# Find process using port
netstat -ano | findstr :8080

# Kill process (replace PID)
taskkill /PID <PID> /F

# OR change port in application.properties
server.port=8081
```

### Issue: Cannot Connect to Database
```bash
# Test connectivity (replace with your actual credentials)
mysql -h <YOUR_DB_HOST> -u <YOUR_DB_USER> -p<YOUR_DB_PASSWORD> -e "SELECT 1;"

# Verify credentials in application.properties
# Check AWS RDS security groups
```

### Issue: 404 Page Not Found
```bash
# Verify application is running
curl http://localhost:8080/api/bugs

# Check correct URLs
http://localhost:8080          # Homepage
http://localhost:8080/bugs     # Dashboard
http://localhost:8080/api/bugs # API
```

### Issue: 500 Internal Server Error
```bash
# Check console output for stack trace
# Common causes:
# 1. Database connection failed
# 2. Incorrect configuration
# 3. Missing templates

# Enable detailed logs
logging.level.root=DEBUG
```

---

## 📦 Deployment

### AWS EC2 Deployment
```bash
# Install Java
sudo yum install java-21-amazon-corretto-devel -y

# Copy and run JAR
scp -i key.pem bug-tracker-1.0.0.jar ec2-user@your-ip:/home/ec2-user/
ssh -i key.pem ec2-user@your-ip
java -jar /home/ec2-user/bug-tracker-1.0.0.jar
```

### Docker Deployment
```bash
docker build -t bugtracker:latest .
docker run -d -p 8080:8080 \
  -e DB_HOST=<YOUR_DB_HOST> \
  -e DB_USER=<YOUR_DB_USER> \
  -e DB_PASSWORD=<YOUR_DB_PASSWORD> \
  bugtracker:latest
```

### Docker Compose
```bash
docker-compose up -d
```

---

## 💾 Database Management

### Backup Database
```bash
mysqldump -h <YOUR_DB_HOST> \
  -u <YOUR_DB_USER> -p<YOUR_DB_PASSWORD> <YOUR_DB_NAME> > backup.sql
```

### Query Database
```bash
mysql -h <YOUR_DB_HOST> \
  -u <YOUR_DB_USER> -p<YOUR_DB_PASSWORD> <YOUR_DB_NAME> -e "SELECT * FROM bugs;"
```

---

## 📚 Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System design & architecture
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Contribution guidelines
- **[LICENSE](LICENSE)** - MIT License

---

## 🔐 Security

✅ **Implemented:**
- SQL Injection Prevention (PreparedStatements)
- Input Validation
- Global Exception Handling
- CORS Ready
- Environment-Based Secrets
- Credentials externalized via environment variables
- .env.example provided as template

### 🚨 Important Security Notes

**NEVER commit sensitive data to Git:**
- Database credentials
- API keys and tokens
- Passwords
- Hostnames (in production)
- AWS secrets

**Before uploading to GitHub:**
1. ✅ Use environment variables for all credentials
2. ✅ .gitignore includes `.env` (private credentials file)
3. ✅ .env.example provided as template (safe to commit)
4. ✅ README.md uses placeholders instead of real credentials
5. ✅ No hardcoded secrets in source code

**How to set credentials:**
```bash
# Create .env file (NOT committed to Git)
DB_HOST=your-rds-endpoint
DB_USER=your-username
DB_PASSWORD=your-secure-password
DB_NAME=your-database-name

# Load in terminal before running
source .env
mvn spring-boot:run
```

**Production Deployment:**
- Use AWS Secrets Manager for sensitive data
- Enable HTTPS/TLS
- Implement Spring Security for authentication
- Add API key authentication
- Enable comprehensive logging
- Use IAM roles instead of hardcoded credentials

⚠️ **Production Recommendations:**
- Enable HTTPS/TLS
- Implement Spring Security
- Add API authentication
- Use AWS Secrets Manager for credentials
- Enable request logging and monitoring
- Set up VPC and security groups

---

## 📄 License

MIT License - See [LICENSE](LICENSE) for details

---

## 🤝 Support

- Check [Troubleshooting](#troubleshooting) section
- Review [ARCHITECTURE.md](ARCHITECTURE.md) for design
- Check application logs
- Open a GitHub issue

---

**Developed with ❤️ by Team Innovators**

Software Engineering Project 2025 | Bug Tracker System v1.0.0
