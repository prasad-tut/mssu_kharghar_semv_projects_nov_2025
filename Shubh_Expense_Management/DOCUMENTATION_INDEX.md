# Documentation Index

Complete guide to all documentation for the Expense Management System.

## 📚 Quick Navigation

### 🚀 Getting Started

1. **[README.md](README.md)** - Start here! Project overview and quick start guide
2. **[Frontend README](frontend/README.md)** - Frontend-specific setup and development

### 🏗️ Deployment Guides

Choose the guide that matches your deployment scenario:

| Document | Best For | Time Required |
|----------|----------|---------------|
| **[DEPLOYMENT_QUICK_START.md](DEPLOYMENT_QUICK_START.md)** | Quick production deployment | 20-30 min |
| **[DEPLOYMENT.md](DEPLOYMENT.md)** | Complete deployment guide (all options) | 1-2 hours |
| **[DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)** | Docker/Docker Compose deployment | 30-45 min |
| **[PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md)** | Backend-specific production deployment | 1-2 hours |

### 🗄️ Database Setup

| Document | Purpose |
|----------|---------|
| **[aws-rds-setup.md](aws-rds-setup.md)** | Complete AWS RDS PostgreSQL setup guide |
| **[DATABASE_MIGRATIONS.md](DATABASE_MIGRATIONS.md)** | Database schema migrations with Flyway |
| **[RDS-SETUP-README.md](RDS-SETUP-README.md)** | Alternative RDS setup guide |
| **[RDS-SETUP-CHECKLIST.md](RDS-SETUP-CHECKLIST.md)** | RDS setup checklist |
| **[RDS-QUICK-REFERENCE.md](RDS-QUICK-REFERENCE.md)** | Quick RDS commands reference |

### ⚙️ Configuration

| Document | Purpose |
|----------|---------|
| **[PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md)** | All environment variables explained |
| **[.env.example](.env.example)** | Example environment file |
| **[frontend/.env.development](frontend/.env.development)** | Frontend development config |
| **[frontend/.env.production](frontend/.env.production)** | Frontend production config |

### ✅ Checklists and References

| Document | Purpose |
|----------|---------|
| **[PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md)** | Pre/post-deployment checklist |
| **[PRODUCTION_QUICK_REFERENCE.md](PRODUCTION_QUICK_REFERENCE.md)** | Quick commands and troubleshooting |
| **[PRODUCTION_SETUP_SUMMARY.md](PRODUCTION_SETUP_SUMMARY.md)** | Production setup summary |

## 📖 Documentation by Use Case

### I want to...

#### Run the application locally for development

1. Read: [README.md](README.md) - Quick Start section
2. Read: [Frontend README](frontend/README.md) - Setup section
3. Set up local PostgreSQL or use Docker Compose

#### Deploy to production for the first time

1. Read: [DEPLOYMENT_QUICK_START.md](DEPLOYMENT_QUICK_START.md)
2. Follow: [aws-rds-setup.md](aws-rds-setup.md) for database
3. Use: [PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md) to track progress
4. Reference: [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md) for configuration

#### Deploy using Docker

1. Read: [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)
2. Use the provided `docker-compose.yml`
3. Configure environment variables

#### Understand database migrations

1. Read: [DATABASE_MIGRATIONS.md](DATABASE_MIGRATIONS.md)
2. Review migration files in `src/main/resources/db/migration/`

#### Troubleshoot production issues

1. Check: [PRODUCTION_QUICK_REFERENCE.md](PRODUCTION_QUICK_REFERENCE.md) - Troubleshooting section
2. Review: [DEPLOYMENT.md](DEPLOYMENT.md) - Troubleshooting section
3. Check application logs and health endpoints

#### Set up AWS RDS database

1. Follow: [aws-rds-setup.md](aws-rds-setup.md)
2. Use: [RDS-SETUP-CHECKLIST.md](RDS-SETUP-CHECKLIST.md)
3. Reference: [RDS-QUICK-REFERENCE.md](RDS-QUICK-REFERENCE.md)

#### Configure environment variables

1. Read: [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md)
2. Copy: [.env.example](.env.example) and customize

#### Deploy frontend only

1. Read: [Frontend README](frontend/README.md) - Deployment section
2. Reference: [DEPLOYMENT.md](DEPLOYMENT.md) - Frontend Deployment section

#### Deploy backend only

1. Read: [PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md)
2. Use: [PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md)

## 📋 Document Descriptions

### Core Documentation

**README.md**
- Project overview and architecture
- Technology stack
- Quick start guide for local development
- Key features
- Testing and building instructions

**Frontend README**
- Frontend-specific setup
- Available scripts
- Deployment options
- Troubleshooting

### Deployment Documentation

**DEPLOYMENT.md** (Comprehensive)
- Complete deployment guide covering all options
- Backend deployment (EC2, Elastic Beanstalk, Docker, ECS)
- Frontend deployment (S3, Netlify, Vercel, Nginx)
- Environment configuration
- Post-deployment verification
- Monitoring and maintenance
- Troubleshooting

**DEPLOYMENT_QUICK_START.md** (Quick Reference)
- Condensed deployment guide
- Step-by-step commands
- Minimal explanations
- Quick troubleshooting
- Cost estimates

**DOCKER_DEPLOYMENT.md** (Docker-Specific)
- Docker and Docker Compose deployment
- Container configuration
- Volume management
- Production Docker setup
- Backup and restore
- Performance tuning

**PRODUCTION_DEPLOYMENT_GUIDE.md** (Backend-Specific)
- Detailed backend deployment
- Multiple deployment options
- Systemd service configuration
- Health checks and monitoring
- Security best practices
- Rollback procedures

### Database Documentation

**aws-rds-setup.md**
- AWS RDS PostgreSQL setup
- CLI and Console instructions
- Security group configuration
- Connection testing
- Monitoring and maintenance
- Cost optimization

**DATABASE_MIGRATIONS.md**
- Flyway migration system
- Migration file structure
- Creating new migrations
- Running migrations
- Troubleshooting
- Best practices
- Rollback strategies

**RDS-SETUP-README.md**
- Alternative RDS setup guide
- Step-by-step instructions
- Configuration examples

**RDS-SETUP-CHECKLIST.md**
- Checklist for RDS setup
- Verification steps

**RDS-QUICK-REFERENCE.md**
- Quick RDS commands
- Common operations
- Troubleshooting tips

### Configuration Documentation

**PRODUCTION_ENV_VARIABLES.md**
- Complete environment variable reference
- Required vs optional variables
- Security best practices
- Platform-specific configuration
- Troubleshooting

**.env.example**
- Template for environment variables
- Example values
- Comments explaining each variable

### Checklist and Reference Documentation

**PRODUCTION_CHECKLIST.md**
- Pre-deployment checklist
- Deployment steps
- Post-deployment verification
- Monitoring setup
- Rollback procedure
- Sign-off template

**PRODUCTION_QUICK_REFERENCE.md**
- Quick command reference
- Common operations
- Troubleshooting commands
- Emergency procedures
- Key ports and endpoints

**PRODUCTION_SETUP_SUMMARY.md**
- Summary of production setup
- Key decisions and configurations
- Quick reference

## 🎯 Recommended Reading Order

### For First-Time Deployment

1. **[README.md](README.md)** - Understand the project
2. **[PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md)** - Understand configuration
3. **[aws-rds-setup.md](aws-rds-setup.md)** - Set up database
4. **[DEPLOYMENT_QUICK_START.md](DEPLOYMENT_QUICK_START.md)** - Deploy quickly
5. **[PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md)** - Verify everything

### For Docker Deployment

1. **[README.md](README.md)** - Understand the project
2. **[DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)** - Deploy with Docker
3. **[PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md)** - Configure environment

### For Detailed Understanding

1. **[README.md](README.md)** - Project overview
2. **[DEPLOYMENT.md](DEPLOYMENT.md)** - Complete deployment guide
3. **[DATABASE_MIGRATIONS.md](DATABASE_MIGRATIONS.md)** - Database management
4. **[PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md)** - Backend details
5. **[Frontend README](frontend/README.md)** - Frontend details

## 🔍 Finding Information

### Search by Topic

**Authentication & Security**
- [README.md](README.md) - Security section
- [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md) - JWT_SECRET
- [PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md) - Security checklist

**Database**
- [aws-rds-setup.md](aws-rds-setup.md) - RDS setup
- [DATABASE_MIGRATIONS.md](DATABASE_MIGRATIONS.md) - Schema management
- [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md) - DB configuration

**Deployment**
- [DEPLOYMENT.md](DEPLOYMENT.md) - All deployment options
- [DEPLOYMENT_QUICK_START.md](DEPLOYMENT_QUICK_START.md) - Quick deployment
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Docker deployment
- [PRODUCTION_DEPLOYMENT_GUIDE.md](PRODUCTION_DEPLOYMENT_GUIDE.md) - Backend deployment

**Troubleshooting**
- [PRODUCTION_QUICK_REFERENCE.md](PRODUCTION_QUICK_REFERENCE.md) - Quick fixes
- [DEPLOYMENT.md](DEPLOYMENT.md) - Troubleshooting section
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Docker troubleshooting

**Configuration**
- [PRODUCTION_ENV_VARIABLES.md](PRODUCTION_ENV_VARIABLES.md) - All variables
- [.env.example](.env.example) - Example configuration
- [Frontend .env files](frontend/) - Frontend configuration

**Monitoring**
- [DEPLOYMENT.md](DEPLOYMENT.md) - Monitoring section
- [PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md) - Monitoring setup
- [PRODUCTION_QUICK_REFERENCE.md](PRODUCTION_QUICK_REFERENCE.md) - Monitoring endpoints

## 📝 Documentation Standards

All documentation in this project follows these standards:

- **Markdown format** for easy reading and version control
- **Code examples** with syntax highlighting
- **Step-by-step instructions** where applicable
- **Troubleshooting sections** for common issues
- **Cross-references** to related documentation
- **Platform-specific instructions** (Windows/Linux/macOS)
- **Security best practices** highlighted
- **Quick reference sections** for common operations

## 🆘 Getting Help

If you can't find what you're looking for:

1. **Search this index** for relevant keywords
2. **Check the README** for general information
3. **Review troubleshooting sections** in relevant guides
4. **Check application logs** for specific errors
5. **Create a GitHub issue** with:
   - What you're trying to do
   - What documentation you've read
   - What error you're encountering
   - Relevant logs and configuration

## 🔄 Keeping Documentation Updated

When making changes to the system:

1. Update relevant documentation
2. Add new sections if needed
3. Update this index if adding new documents
4. Keep code examples current
5. Update version numbers and dates

## 📊 Documentation Coverage

This project includes documentation for:

- ✅ Project overview and architecture
- ✅ Local development setup
- ✅ Database setup and migrations
- ✅ Environment configuration
- ✅ Backend deployment (multiple options)
- ✅ Frontend deployment (multiple options)
- ✅ Docker deployment
- ✅ Production checklists
- ✅ Troubleshooting guides
- ✅ Quick reference cards
- ✅ Security best practices
- ✅ Monitoring and maintenance

## 🎓 Additional Resources

### External Documentation

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [React Documentation](https://react.dev/)
- [Vite Documentation](https://vitejs.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [AWS RDS Documentation](https://docs.aws.amazon.com/rds/)
- [Docker Documentation](https://docs.docker.com/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

### API Documentation

When the application is running:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

---

**Last Updated**: November 2024

**Need help?** Start with [README.md](README.md) or create a GitHub issue.
