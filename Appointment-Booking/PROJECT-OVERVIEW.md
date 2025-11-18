# Project Overview

## Appointment Management System

A full-stack web application for managing appointments with a Spring Boot backend and vanilla JavaScript frontend.

---

## 📋 Table of Contents

- [Project Description](#project-description)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Key Features](#key-features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Documentation](#documentation)
- [Team](#team)

---

## Project Description

The Appointment Management System is a RESTful web application designed to handle appointment scheduling and management. It provides a complete CRUD (Create, Read, Update, Delete) interface for managing appointments between clients, providers, and services.

### Use Cases

- Healthcare appointment scheduling
- Service provider booking systems
- Consultation management
- General appointment tracking

---

## Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Client Browser                       │
│                  (HTML/CSS/JavaScript)                   │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/REST API
                     │ (JSON)
┌────────────────────▼────────────────────────────────────┐
│              Spring Boot Application                     │
│                    (Port 9080)                           │
│  ┌─────────────────────────────────────────────────┐   │
│  │         Controller Layer                         │   │
│  │    (AppointmentController.java)                 │   │
│  │    - REST Endpoints                             │   │
│  │    - Request/Response Handling                  │   │
│  └──────────────────┬──────────────────────────────┘   │
│                     │                                    │
│  ┌──────────────────▼──────────────────────────────┐   │
│  │         Service Layer                            │   │
│  │    (AppointmentService.java)                    │   │
│  │    - Business Logic                             │   │
│  │    - Transaction Management                     │   │
│  └──────────────────┬──────────────────────────────┘   │
│                     │                                    │
│  ┌──────────────────▼──────────────────────────────┐   │
│  │         Repository Layer                         │   │
│  │    (AppointmentRepository.java)                 │   │
│  │    - Data Access                                │   │
│  │    - SQL Operations (JdbcTemplate)              │   │
│  └──────────────────┬──────────────────────────────┘   │
└────────────────────┬────────────────────────────────────┘
                     │ JDBC
                     │
┌────────────────────▼────────────────────────────────────┐
│                  Database Layer                          │
│         H2 (Development) / MySQL (Production)           │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │           appointments table                    │    │
│  │  - appointment_id (PK)                         │    │
│  │  - client_id                                   │    │
│  │  - provider_id                                 │    │
│  │  - service_id                                  │    │
│  │  - appointment_date                            │    │
│  │  - appointment_time                            │    │
│  │  - status                                      │    │
│  │  - created_at                                  │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### Request Flow

1. **User Action**: User interacts with web interface (e.g., clicks "Add Appointment")
2. **Frontend**: JavaScript sends HTTP request with JSON payload
3. **Controller**: Receives request, validates, and routes to service
4. **Service**: Applies business logic and calls repository
5. **Repository**: Executes SQL query using JdbcTemplate
6. **Database**: Processes query and returns results
7. **Response**: Data flows back through layers as JSON to frontend
8. **UI Update**: JavaScript updates the interface with new data

---

## Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 3.5.5 | Application framework |
| Spring Web | 3.5.5 | REST API support |
| Spring JDBC | 3.5.5 | Database connectivity |
| Maven | 3.6+ | Build and dependency management |
| MySQL Connector | 9.4.0 | MySQL database driver |
| H2 Database | Runtime | In-memory database for development |

### Frontend Technologies

| Technology | Purpose |
|------------|---------|
| HTML5 | Structure and markup |
| CSS3 | Styling and layout |
| JavaScript (ES6+) | Client-side logic |
| Fetch API | HTTP requests |

### DevOps & Cloud

| Technology | Purpose |
|------------|---------|
| Docker | Containerization |
| Docker Compose | Multi-container orchestration |
| AWS RDS | Managed MySQL database |
| AWS Elastic Beanstalk | Application hosting |
| AWS ECR | Container registry |
| Git | Version control |

---

## Key Features

### Functional Features

✅ **Complete CRUD Operations**
- Create new appointments
- Read/view all appointments
- Update existing appointments
- Delete appointments

✅ **RESTful API**
- Standard HTTP methods (GET, POST, PUT, DELETE)
- JSON request/response format
- Clean URL structure

✅ **Web Interface**
- User-friendly form-based interface
- Real-time appointment list
- Responsive design
- Status management (Scheduled, Confirmed, Completed, Cancelled)

✅ **Data Management**
- Client tracking
- Provider assignment
- Service categorization
- Timestamp tracking

### Technical Features

✅ **Dual Database Support**
- H2 for rapid development
- MySQL for production

✅ **CORS Configuration**
- Cross-origin resource sharing enabled
- Frontend-backend communication

✅ **Hot Reload**
- Spring Boot DevTools integration
- Automatic restart on code changes

✅ **Containerization**
- Docker support
- Docker Compose orchestration

✅ **Cloud Ready**
- AWS RDS integration
- Elastic Beanstalk deployment
- Environment-based configuration

---

## Project Structure

```
restapi_app/
│
├── src/
│   ├── main/
│   │   ├── java/mssu/in/restapi_app/
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java              # CORS configuration
│   │   │   ├── controller/
│   │   │   │   └── AppointmentController.java  # REST endpoints
│   │   │   ├── service/
│   │   │   │   └── AppointmentService.java     # Business logic
│   │   │   ├── repository/
│   │   │   │   └── AppointmentRepository.java  # Data access
│   │   │   ├── entity/
│   │   │   │   └── Appointment.java            # Domain model
│   │   │   └── RestapiAppApplication.java      # Main class
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html                  # Frontend UI
│   │       │   ├── app.js                      # Frontend logic
│   │       │   └── styles.css                  # Styling
│   │       ├── application.properties          # Configuration
│   │       ├── schema.sql                      # Database schema
│   │       └── data.sql                        # Sample data
│   │
│   └── test/
│       └── java/mssu/in/restapi_app/
│           └── RestapiAppApplicationTests.java # Unit tests
│
├── .ebextensions/                              # AWS EB configuration
│   ├── 01-java.config
│   ├── 02-healthcheck.config
│   └── 03-logs.config
│
├── target/                                     # Build output (gitignored)
│
├── .gitignore                                  # Git ignore rules
├── .dockerignore                               # Docker ignore rules
├── .env.example                                # Environment template
│
├── Dockerfile                                  # Docker image definition
├── docker-compose.yml                          # Production Docker setup
├── docker-compose.dev.yml                      # Development Docker setup
│
├── deploy-aws.sh                               # AWS deployment script (Linux/Mac)
├── deploy-aws.bat                              # AWS deployment script (Windows)
│
├── pom.xml                                     # Maven configuration
│
├── README.md                                   # Main documentation
├── DEPLOYMENT.md                               # Deployment guide
├── QUICK-START.md                              # Quick start guide
└── PROJECT-OVERVIEW.md                         # This file
```

---

## Getting Started

### Quick Start (3 Options)

**Option 1: H2 Database (Fastest)**
```bash
mvnw spring-boot:run
# Access: http://localhost:9080
```

**Option 2: Docker**
```bash
docker-compose up -d
# Access: http://localhost:9080
```

**Option 3: Local MySQL**
```bash
# Setup database first
mysql -u root -p < src/main/resources/schema.sql
# Update application.properties
mvnw spring-boot:run
```

### Detailed Instructions

See [QUICK-START.md](QUICK-START.md) for step-by-step instructions.

---

## Documentation

| Document | Description |
|----------|-------------|
| [README.md](README.md) | Main project documentation with API reference |
| [DEPLOYMENT.md](DEPLOYMENT.md) | Comprehensive deployment guide for AWS and Docker |
| [QUICK-START.md](QUICK-START.md) | Get started in under 5 minutes |
| [PROJECT-OVERVIEW.md](PROJECT-OVERVIEW.md) | This document - high-level overview |

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/appointments/get` | Get all appointments |
| GET | `/appointments/get/{id}` | Get appointment by ID |
| POST | `/appointments/add` | Create new appointment |
| PUT | `/appointments/edit` | Update appointment |
| DELETE | `/appointments/delete/{id}` | Delete appointment |

Base URL: `http://localhost:9080`

---

## Database Schema

### appointments Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| appointment_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| client_id | INT | NOT NULL | Client reference |
| provider_id | INT | NOT NULL | Provider reference |
| service_id | INT | NOT NULL | Service reference |
| appointment_date | VARCHAR(50) | NOT NULL | Appointment date |
| appointment_time | VARCHAR(50) | NOT NULL | Appointment time |
| status | VARCHAR(50) | NOT NULL | Status (Scheduled, Confirmed, etc.) |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |

---

## Development Workflow

### Local Development

1. Clone repository
2. Run with H2 database
3. Make changes
4. Test locally
5. Commit changes

### Testing

```bash
# Run unit tests
mvn test

# Run with test coverage
mvn test jacoco:report
```

### Building

```bash
# Clean build
mvn clean package

# Skip tests
mvn clean package -DskipTests
```

### Deployment

```bash
# Deploy to AWS
./deploy-aws.sh

# Or use Docker
docker-compose up -d
```

---

## Team

**Project Team Members:**
- Ishita Parkar
- Kirshnandu Gurey
- Prutha Jadhav
- Yash Adhau

**Course:** Cloud Computing  
**Year:** 2025

---

## Project Goals

This project demonstrates:

1. **Full-Stack Development**
   - Backend API development with Spring Boot
   - Frontend development with vanilla JavaScript
   - Database design and integration

2. **Cloud Computing Concepts**
   - Cloud deployment (AWS)
   - Database as a Service (RDS)
   - Platform as a Service (Elastic Beanstalk)
   - Containerization (Docker)

3. **Software Engineering Practices**
   - Layered architecture
   - RESTful API design
   - Version control with Git
   - Documentation

4. **DevOps Practices**
   - Containerization
   - Infrastructure as Code
   - Automated deployment
   - Environment management

---

## Future Enhancements

Potential improvements for the system:

- [ ] User authentication and authorization
- [ ] Email notifications
- [ ] Calendar integration
- [ ] Advanced search and filtering
- [ ] Reporting and analytics
- [ ] Mobile application
- [ ] Payment integration
- [ ] Multi-language support
- [ ] CI/CD pipeline with Jenkins
- [ ] Comprehensive test coverage
- [ ] API documentation with Swagger
- [ ] Rate limiting and security enhancements

---

## License

This project is created for educational purposes as part of a Cloud Computing course.

---

## Support & Contact

For questions or issues:
- Review documentation files
- Check troubleshooting sections
- Contact team members

---

**Last Updated:** November 2025  
**Version:** 1.0.0
