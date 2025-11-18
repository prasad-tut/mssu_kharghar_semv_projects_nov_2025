# Project Files Reference

Complete reference of all files in the Appointment Management System project.

---

## Documentation Files

| File | Description |
|------|-------------|
| `README.md` | Main project documentation with setup, API reference, and usage |
| `QUICK-START.md` | Quick start guide to get running in under 5 minutes |
| `DEPLOYMENT.md` | Comprehensive deployment guide for AWS, Docker, and EC2 |
| `JENKINS-SETUP.md` | Complete Jenkins CI/CD setup and configuration guide |
| `CI-CD-PIPELINE.md` | CI/CD pipeline architecture and workflow documentation |
| `PROJECT-OVERVIEW.md` | High-level architecture and system overview |
| `COMMANDS.md` | Quick reference for all commands (Maven, Docker, AWS, etc.) |
| `PROJECT-FILES.md` | This file - complete project files reference |

---

## CI/CD Files

| File | Description |
|------|-------------|
| `Jenkinsfile` | Complete CI/CD pipeline for AWS ECS deployment |
| `Jenkinsfile.simple` | Simplified pipeline for AWS Elastic Beanstalk |
| `jenkins-setup.sh` | Jenkins setup helper script (Mac/Linux) |
| `jenkins-setup.bat` | Jenkins setup helper script (Windows) |
| `task-definition.json` | AWS ECS task definition configuration |

---

## Docker Files

| File | Description |
|------|-------------|
| `Dockerfile` | Multi-stage Docker image definition |
| `docker-compose.yml` | Production Docker Compose configuration |
| `docker-compose.dev.yml` | Development Docker Compose (database only) |
| `.dockerignore` | Files to exclude from Docker build context |

---

## Deployment Scripts

| File | Description |
|------|-------------|
| `deploy-aws.sh` | AWS deployment automation script (Mac/Linux) |
| `deploy-aws.bat` | AWS deployment automation script (Windows) |

---

## Configuration Files

| File | Description |
|------|-------------|
| `pom.xml` | Maven project configuration and dependencies |
| `.env.example` | Environment variables template |
| `.gitignore` | Git ignore rules |
| `.gitattributes` | Git attributes configuration |

---

## AWS Configuration

| Directory/File | Description |
|----------------|-------------|
| `.ebextensions/` | AWS Elastic Beanstalk configuration directory |
| `.ebextensions/01-java.config` | JVM and Java configuration |
| `.ebextensions/02-healthcheck.config` | Health check configuration |
| `.ebextensions/03-logs.config` | CloudWatch logs configuration |

---

## Source Code Structure

```
src/
├── main/
│   ├── java/mssu/in/restapi_app/
│   │   ├── config/
│   │   │   └── WebConfig.java              # CORS configuration
│   │   ├── controller/
│   │   │   └── AppointmentController.java  # REST API endpoints
│   │   ├── service/
│   │   │   └── AppointmentService.java     # Business logic
│   │   ├── repository/
│   │   │   └── AppointmentRepository.java  # Data access layer
│   │   ├── entity/
│   │   │   └── Appointment.java            # Domain model
│   │   └── RestapiAppApplication.java      # Main application class
│   │
│   └── resources/
│       ├── static/
│       │   ├── index.html                  # Frontend UI
│       │   ├── app.js                      # Frontend JavaScript
│       │   └── styles.css                  # Frontend styling
│       ├── application.properties          # Application configuration
│       ├── schema.sql                      # Database schema
│       └── data.sql                        # Sample data
│
└── test/
    └── java/mssu/in/restapi_app/
        └── RestapiAppApplicationTests.java # Unit tests
```

---

## Build Files

| File | Description |
|------|-------------|
| `mvnw` | Maven wrapper script (Mac/Linux) |
| `mvnw.cmd` | Maven wrapper script (Windows) |
| `.mvn/` | Maven wrapper configuration directory |
| `target/` | Build output directory (gitignored) |

---

## File Purposes by Category

### Getting Started
1. Start here: `README.md`
2. Quick setup: `QUICK-START.md`
3. Run locally: `mvnw spring-boot:run`

### Development
1. Source code: `src/main/java/`
2. Frontend: `src/main/resources/static/`
3. Configuration: `src/main/resources/application.properties`
4. Tests: `src/test/`

### Docker Deployment
1. Build image: `Dockerfile`
2. Run locally: `docker-compose.yml`
3. Development DB: `docker-compose.dev.yml`

### Jenkins CI/CD
1. Setup Jenkins: `JENKINS-SETUP.md`
2. Pipeline config: `Jenkinsfile`
3. Setup helper: `jenkins-setup.sh` or `jenkins-setup.bat`
4. Pipeline docs: `CI-CD-PIPELINE.md`

### AWS Deployment
1. Deployment guide: `DEPLOYMENT.md`
2. Deploy script: `deploy-aws.sh` or `deploy-aws.bat`
3. ECS config: `task-definition.json`
4. EB config: `.ebextensions/`

### Reference
1. All commands: `COMMANDS.md`
2. Architecture: `PROJECT-OVERVIEW.md`
3. This file: `PROJECT-FILES.md`

---

## File Sizes (Approximate)

| Category | Files | Total Size |
|----------|-------|------------|
| Documentation | 8 files | ~150 KB |
| Source Code | ~10 files | ~50 KB |
| Configuration | ~15 files | ~20 KB |
| Scripts | 4 files | ~10 KB |
| Build Output | varies | ~20 MB |

---

## Important Files to Customize

### Before First Run

1. **`src/main/resources/application.properties`**
   - Update database connection for production
   - Configure server port if needed

2. **`.env.example`**
   - Copy to `.env`
   - Update with your credentials

### Before Deployment

1. **`task-definition.json`**
   - Replace `<account-id>` with your AWS account ID
   - Update region if not using us-east-1

2. **`Jenkinsfile`**
   - Update AWS region if needed
   - Configure notification settings

3. **`.ebextensions/` files**
   - Adjust JVM memory settings if needed
   - Configure health check endpoints

---

## Files You Should NOT Modify

- `mvnw`, `mvnw.cmd` - Maven wrapper (auto-generated)
- `.mvn/` - Maven wrapper config (auto-generated)
- `target/` - Build output (auto-generated)
- `.git/` - Git repository data
- `.classpath`, `.project` - IDE files (auto-generated)

---

## Files to Keep Secret

Never commit these files to version control:

- `.env` - Contains sensitive credentials
- `*.pem` - SSH keys
- `*.key` - Private keys
- AWS credentials files
- Database passwords

These are already in `.gitignore`

---

## Workflow by Use Case

### Local Development
```
1. README.md (understand project)
2. QUICK-START.md (setup)
3. mvnw spring-boot:run (run)
4. src/main/java/ (code)
5. COMMANDS.md (reference)
```

### Docker Development
```
1. Dockerfile (review)
2. docker-compose.dev.yml (database)
3. mvnw spring-boot:run (app)
4. docker-compose.yml (full stack)
```

### Jenkins Setup
```
1. JENKINS-SETUP.md (read guide)
2. jenkins-setup.sh (run helper)
3. Jenkinsfile (review pipeline)
4. CI-CD-PIPELINE.md (understand flow)
```

### AWS Deployment
```
1. DEPLOYMENT.md (read guide)
2. .env.example → .env (configure)
3. task-definition.json (customize)
4. deploy-aws.sh (deploy)
```

---

## Quick File Lookup

**Need to...**

- Understand the project? → `README.md`
- Get started quickly? → `QUICK-START.md`
- Deploy to AWS? → `DEPLOYMENT.md`
- Setup Jenkins? → `JENKINS-SETUP.md`
- Find a command? → `COMMANDS.md`
- Understand architecture? → `PROJECT-OVERVIEW.md`
- See pipeline flow? → `CI-CD-PIPELINE.md`
- Reference all files? → `PROJECT-FILES.md` (this file)

**Want to modify...**

- API endpoints? → `src/main/java/.../controller/`
- Business logic? → `src/main/java/.../service/`
- Database queries? → `src/main/java/.../repository/`
- Frontend UI? → `src/main/resources/static/`
- Configuration? → `src/main/resources/application.properties`
- Database schema? → `src/main/resources/schema.sql`

**Need to configure...**

- Docker? → `Dockerfile`, `docker-compose.yml`
- Jenkins? → `Jenkinsfile`, `JENKINS-SETUP.md`
- AWS ECS? → `task-definition.json`
- AWS EB? → `.ebextensions/`
- Environment? → `.env.example` → `.env`

---

## File Creation Order

These files were created in this order:

1. **Core Application** (Spring Boot generated)
   - `pom.xml`
   - `src/` directory structure
   - Java source files

2. **Documentation** (Phase 1)
   - `README.md`
   - `QUICK-START.md`

3. **Deployment** (Phase 2)
   - `DEPLOYMENT.md`
   - `Dockerfile`
   - `docker-compose.yml`
   - `docker-compose.dev.yml`
   - `.dockerignore`
   - `deploy-aws.sh`
   - `deploy-aws.bat`
   - `.ebextensions/`

4. **CI/CD** (Phase 3)
   - `Jenkinsfile`
   - `Jenkinsfile.simple`
   - `JENKINS-SETUP.md`
   - `jenkins-setup.sh`
   - `jenkins-setup.bat`
   - `task-definition.json`
   - `CI-CD-PIPELINE.md`

5. **Reference** (Phase 4)
   - `PROJECT-OVERVIEW.md`
   - `COMMANDS.md`
   - `PROJECT-FILES.md`
   - `.env.example`

---

## Maintenance

### Regular Updates Needed

- `README.md` - Keep API docs current
- `DEPLOYMENT.md` - Update for new AWS services
- `pom.xml` - Update dependencies
- `Jenkinsfile` - Adjust pipeline as needed

### Periodic Review

- Security configurations
- Dependency versions
- AWS resource configurations
- Documentation accuracy

---

**Last Updated:** November 2025  
**Maintained by:** Ishita Parkar, Kirshnandu Gurey, Prutha Jadhav, and Yash Adhau
