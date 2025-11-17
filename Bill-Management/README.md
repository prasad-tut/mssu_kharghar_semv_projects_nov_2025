# Bill Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

A Simple, Scalable bill management application with Spring Boot backend and modern JavaScript frontend.

## Team Y2KODE

**Cloud Computing Project Submission**

Team Members:
- Bhavya Sharma
- Hrishikesh Patil
- Tejas Bhor
- Viraj Sawant

## Features

- Dashboard with real-time statistics
- Complete CRUD operations for bills
- Search and filter functionality
- Export to CSV
- Responsive design
- Docker containerization
- Automated CI/CD pipeline with Jenkins
- AWS deployment support (ECS, ECR, RDS)
- Infrastructure as Code with Docker Compose

## Tech Stack

**Backend:**
- Spring Boot 3.5.6
- Java 17
- PostgreSQL 15
- JDBC Template

**Frontend:**
- HTML5, CSS3, JavaScript (ES6+)
- Bootstrap 5
- Vanilla JS (no framework)

**DevOps:**
- Docker & Docker Compose
- Jenkins CI/CD Pipeline
- AWS Cloud Services (ECS, ECR, RDS, S3)
- Automated Testing & Deployment

## Quick Start

### Prerequisites
- Java 17+
- PostgreSQL 15+
- Docker & Docker Compose (optional)

### Setup

1. **Clone and configure**
```bash
git clone <repository-url>
cd Bill-Management

# Copy and edit environment file
cp .env.example .env
# Update DB_PASSWORD in .env
```

2. **Generate frontend config**
```bash
# Windows
generate-frontend-config.bat

# Mac/Linux
chmod +x generate-frontend-config.sh
./generate-frontend-config.sh
```

3. **Start application**

**Option A: Docker (Recommended)**
```bash
docker-compose up -d
```
- Frontend: http://localhost
- Backend: http://localhost:8080

**Option B: Manual**
```bash
# Terminal 1: Start database
docker-compose -f docker-compose.dev.yml up postgres

# Terminal 2: Start backend
cd bill_management
mvn spring-boot:run

# Terminal 3: Open frontend
# Open frontend/index.html in browser
```

## Configuration

All configuration is managed through `.env` file:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=billmanagement
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Backend
BACKEND_PORT=7890
SPRING_PROFILE=dev

# Frontend
API_BASE_URL=http://localhost:7890
```

See [ENV-SETUP.md](ENV-SETUP.md) for detailed configuration options.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/bills/get` | Get all bills |
| GET | `/bills/get/{id}` | Get bill by ID |
| POST | `/bills/add` | Create new bill |
| PUT | `/bills/edit` | Update bill |
| DELETE | `/bills/delete/{id}` | Delete bill |
| GET | `/actuator/health` | Health check |

## Project Structure

```
Bill-Management/
├── bill_management/          # Spring Boot Backend
│   ├── src/main/java/
│   │   └── y2kode/bill_management/
│   │       ├── controller/   # REST Controllers
│   │       ├── service/      # Business Logic
│   │       ├── repository/   # Data Access
│   │       ├── entity/       # Domain Models
│   │       └── exception/    # Error Handling
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                 # Frontend Application
│   ├── index.html
│   ├── style.css
│   ├── script.js
│   └── env-config.js
├── .env                      # Environment Configuration
├── docker-compose.yml        # Production Setup
├── docker-compose.dev.yml    # Development Setup
├── init.sql                  # Database Schema
└── Jenkinsfile              # CI/CD Pipeline
```

## Development

### Database Setup
```bash
# Create database
psql -U postgres -c "CREATE DATABASE billmanagement;"

# Run schema
psql -U postgres -d billmanagement -f init.sql
```

### Run Tests
```bash
cd bill_management
mvn test
```

### View Logs
```bash
# Docker
docker-compose logs -f backend

# Manual
# Check terminal where mvn spring-boot:run is running
```

## Docker Commands

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild after changes
docker-compose up -d --build

# Start only database (for development)
docker-compose -f docker-compose.dev.yml up postgres

# Access database
docker exec -it bill-management-db psql -U postgres -d billmanagement
```

## CI/CD Pipeline

This project includes a complete Jenkins CI/CD pipeline that automates:

1. **Build** - Compiles Spring Boot application with Maven
2. **Test** - Runs unit tests and generates reports
3. **Dockerize** - Builds Docker images for backend and frontend
4. **Push** - Pushes images to AWS ECR (Elastic Container Registry)
5. **Deploy** - Automatically deploys to AWS ECS

### Pipeline Configuration

The `Jenkinsfile` defines the complete pipeline with the following stages:
- Checkout code from repository
- Build backend with Maven
- Run automated tests
- Build Docker images
- Push to AWS ECR
- Deploy to AWS ECS cluster

### Jenkins Setup

1. Install required plugins: AWS Steps, Docker Pipeline, Maven Integration
2. Configure AWS credentials in Jenkins
3. Create pipeline job pointing to `Jenkinsfile`
4. Configure webhooks for automatic builds on push

## Deployment

### AWS Deployment

1. **Configure AWS credentials**
```bash
# Update .env with AWS settings
AWS_REGION=us-east-1
AWS_ACCOUNT_ID=your_account_id
ECR_REPO_NAME=bill-management
```

2. **Deploy**
```bash
./deploy-aws.sh
```

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed AWS setup instructions.

### Production Checklist

- [ ] Update `.env` with production values
- [ ] Set `SPRING_PROFILE=prod`
- [ ] Configure production database
- [ ] Set strong passwords
- [ ] Enable HTTPS
- [ ] Configure monitoring
- [ ] Set up backups
- [ ] Review security settings

## Troubleshooting

### Backend won't start
```bash
# Check database connection
psql -U postgres -d billmanagement -c "SELECT 1;"

# Verify password in .env
cat .env | grep DB_PASSWORD

# Check port availability
netstat -ano | findstr :7890  # Windows
lsof -i :7890                 # Mac/Linux
```

### Frontend can't connect
```bash
# Regenerate frontend config
./generate-frontend-config.bat  # Windows
./generate-frontend-config.sh   # Mac/Linux

# Check backend is running
curl http://localhost:7890/actuator/health

# Clear browser cache (Ctrl+Shift+Delete)
```

### Database errors
```bash
# Check PostgreSQL is running
# Windows: Services -> postgresql
# Mac: brew services list
# Linux: systemctl status postgresql

# Verify table exists
psql -U postgres -d billmanagement -c "\dt"
```

## Common Commands

```bash
# Test API
curl http://localhost:7890/bills/get

# Add test bill
curl -X POST http://localhost:7890/bills/add \
  -H "Content-Type: application/json" \
  -d '{"biller":"Test","description":"Test","amount":"100","paymentMode":"CASH","paymentStatus":"PENDING"}'

# Check health
curl http://localhost:7890/actuator/health

# View database
psql -U postgres -d billmanagement
```

## Documentation

- [ENV-SETUP.md](ENV-SETUP.md) - Environment configuration guide
- [DEPLOYMENT.md](DEPLOYMENT.md) - AWS deployment guide
- [QUICK-REFERENCE.md](QUICK-REFERENCE.md) - Quick command reference

## Security

- Environment-based configuration
- CORS properly configured
- SQL injection prevention (PreparedStatements)
- Non-root Docker user
- Global exception handling
- Secrets management support

## Project Submission

This project is submitted as part of the Cloud Computing course. The implementation demonstrates:
- Containerization with Docker
- Cloud deployment on AWS
- CI/CD automation with Jenkins
- Microservices architecture
- Infrastructure as Code practices

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License

MIT License

## Support

For issues and questions:
- Check [QUICK-REFERENCE.md](QUICK-REFERENCE.md)
- Check [ENV-SETUP.md](ENV-SETUP.md)
- Open a GitHub issue

---

**Developed by Team Y2KODE** | Cloud Computing Project 2025
