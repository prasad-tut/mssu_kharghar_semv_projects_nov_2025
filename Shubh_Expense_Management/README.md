# Expense Management System

A full-stack cloud-based expense tracking and management system with a Java Spring Boot backend and React frontend.

## Overview

The Expense Management System enables users to:
- Track and manage business expenses
- Categorize expenses and attach receipts
- Submit expenses for approval
- Generate expense reports with filtering and export capabilities
- Manage approval workflows (for managers)

## Architecture

```
┌─────────────────┐
│  React Frontend │  ← User Interface (Port 5173/3000)
│   (Vite/React)  │
└────────┬────────┘
         │ HTTP/REST
         │
┌────────▼────────┐
│  Spring Boot    │  ← Backend API (Port 8080)
│   Backend API   │
└────────┬────────┘
         │ JDBC
         │
┌────────▼────────┐
│   AWS RDS       │  ← Database
│  PostgreSQL     │
└─────────────────┘
```

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- Flyway (Database migrations)
- SpringDoc OpenAPI (API documentation)

### Frontend
- React 19
- Vite (Build tool)
- React Router (Navigation)
- Material-UI (UI Components)
- Axios (HTTP Client)
- React Hook Form (Form Management)
- Chart.js (Data Visualization)

### Database
- AWS RDS PostgreSQL 14+

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 18+
- PostgreSQL 14+ (local) or AWS RDS

### Backend Setup

1. **Database Setup**
   ```bash
   # Create PostgreSQL database
   createdb expensedb
   ```

2. **Configure Environment Variables**
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/expensedb
   export DB_USERNAME=postgres
   export DB_PASSWORD=your_password
   export JWT_SECRET=your-secret-key-minimum-256-bits
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Backend will be available at: http://localhost:8080

### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Run Development Server**
   ```bash
   npm run dev
   ```

   Frontend will be available at: http://localhost:5173

## Project Structure

```
expense-management-system/
├── src/                          # Backend source code
│   ├── main/
│   │   ├── java/com/expense/
│   │   │   ├── config/          # Application configuration
│   │   │   ├── controller/      # REST API endpoints
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── exception/       # Custom exceptions and handlers
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── security/        # JWT and authentication
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── application.yml       # Default configuration
│   │       ├── application-dev.yml   # Development configuration
│   │       ├── application-prod.yml  # Production configuration
│   │       └── db/migration/         # Flyway migration scripts
│   └── test/                    # Backend tests
│
├── frontend/                    # Frontend application
│   ├── src/
│   │   ├── components/         # Reusable UI components
│   │   ├── pages/              # Page-level components
│   │   ├── services/           # API service modules
│   │   ├── context/            # React context providers
│   │   ├── utils/              # Utility functions
│   │   └── styles/             # Global styles
│   ├── .env.development        # Development environment config
│   └── .env.production         # Production environment config
│
├── scripts/                     # Utility scripts
│   ├── setup-rds.sh            # AWS RDS setup script
│   └── test-rds-connection.sh  # RDS connection test
│
├── pom.xml                      # Maven configuration
├── Dockerfile                   # Docker configuration
├── docker-compose.yml           # Docker Compose configuration
└── README.md                    # This file
```

## API Documentation

Once the backend is running, access the API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Key Features

### User Management
- User registration and authentication
- JWT-based secure authentication
- Role-based access control (User, Manager, Admin)

### Expense Management
- Create, read, update, delete expenses
- Categorize expenses (Travel, Meals, Office Supplies, Equipment, Other)
- Attach receipt images (JPEG, PNG, PDF up to 5MB)
- Track expense status (Draft, Submitted, Approved, Rejected)

### Approval Workflow
- Submit expenses for manager approval
- Manager dashboard for pending approvals
- Approve or reject expenses with notes

### Reporting
- Filter expenses by date range, category, and status
- View expense summaries and totals
- Export reports as CSV
- Visual charts and analytics

## Testing

### Backend Tests
```bash
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Building for Production

### Backend
```bash
mvn clean package -DskipTests
```
The executable JAR will be in `target/expense-management-system-1.0.0.jar`

### Frontend
```bash
cd frontend
npm run build
```
The production build will be in `frontend/dist/`

## Deployment

For detailed deployment instructions, see:
- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Complete deployment guide for backend and frontend
- **[PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md)** - Backend-specific deployment guide
- **[PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md)** - Environment variable reference
- **[aws-rds-setup.md](aws-rds-setup.md)** - AWS RDS database setup guide

Quick deployment options:
- AWS EC2 + RDS
- AWS Elastic Beanstalk
- Docker containers
- AWS ECS/Fargate

## Environment Variables

### Required (Backend)
- `DB_URL` - PostgreSQL connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret (minimum 32 characters)

### Required (Frontend)
- `VITE_API_URL` - Backend API URL

See [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md) for complete reference.

## Database Migrations

The application uses Flyway for database schema management. Migrations run automatically on application startup.

Migration files are located in: `src/main/resources/db/migration/`

To run migrations manually:
```bash
mvn flyway:migrate
```

See [DATABASE_MIGRATIONS.md](DATABASE_MIGRATIONS.md) for detailed information.

## Health Check

Verify the backend is running:
```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{"status":"UP"}
```

## Security

- Passwords are hashed using BCrypt
- JWT tokens for stateless authentication
- CORS configured for frontend access
- SQL injection protection via JPA
- File upload validation (type and size)
- Role-based authorization

## Troubleshooting

### Backend won't start
- Check Java version: `java -version` (should be 17+)
- Verify database is running and accessible
- Check environment variables are set correctly
- Review logs for specific errors

### Frontend won't connect to backend
- Verify backend is running: `curl http://localhost:8080/api/health`
- Check `VITE_API_URL` in `.env.development`
- Check browser console for CORS errors
- Verify JWT token is being sent in requests

### Database connection errors
- Test database connection: `psql -h localhost -U postgres -d expensedb`
- Verify security group rules (for AWS RDS)
- Check database credentials
- Ensure database exists

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write/update tests
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
- Check the documentation in the project root
- Review the [Troubleshooting](#troubleshooting) section
- Check existing GitHub issues
- Create a new issue with detailed information

## Additional Documentation

📚 **[DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md)** - Complete guide to all documentation

### Quick Links

**Deployment:**
- [DEPLOYMENT_QUICK_START.md](DEPLOYMENT_QUICK_START.md) - Quick deployment (20-30 min)
- [DEPLOYMENT.md](DEPLOYMENT.md) - Complete deployment guide
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Docker deployment
- [PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md) - Backend deployment

**Database:**
- [aws-rds-setup.md](aws-rds-setup.md) - AWS RDS setup
- [DATABASE_MIGRATIONS.md](DATABASE_MIGRATIONS.md) - Database migrations

**Configuration:**
- [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md) - Environment variables
- [PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md) - Deployment checklist
- [PRODUCTION_QUICK_REFERENCE.md](PRODUCTION_QUICK_REFERENCE.md) - Quick reference

**Component Docs:**
- [Frontend README](frontend/README.md) - Frontend documentation
- [Backend API Documentation](http://localhost:8080/swagger-ui.html) - API docs (when running)
