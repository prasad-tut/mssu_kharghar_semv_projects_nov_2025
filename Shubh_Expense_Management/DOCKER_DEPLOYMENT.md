# Docker Deployment Guide

This guide explains how to deploy the Expense Management System using Docker and Docker Compose.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Quick Start](#quick-start)
3. [Docker Compose Deployment](#docker-compose-deployment)
4. [Individual Container Deployment](#individual-container-deployment)
5. [Production Deployment](#production-deployment)
6. [Configuration](#configuration)
7. [Monitoring and Logs](#monitoring-and-logs)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

- Docker 20.10+ installed
- Docker Compose 2.0+ installed
- 2GB+ available RAM
- 10GB+ available disk space

### Install Docker

**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

**macOS:**
```bash
brew install --cask docker
```

**Windows:**
Download and install Docker Desktop from https://www.docker.com/products/docker-desktop

### Verify Installation

```bash
docker --version
docker-compose --version
```

## Quick Start

The fastest way to get the entire system running:

```bash
# Clone repository
git clone <repository-url>
cd expense-management-system

# Build and start all services
docker-compose up -d

# Wait for services to be healthy (30-60 seconds)
docker-compose ps

# Test backend
curl http://localhost:8080/api/health

# Access frontend (if configured)
# Open browser to http://localhost:5173
```

That's it! The system is now running with:
- PostgreSQL database on port 5432
- Backend API on port 8080
- Automatic database migrations
- Persistent data volumes

## Docker Compose Deployment

### Architecture

The `docker-compose.yml` file defines three services:

```
┌─────────────────┐
│   PostgreSQL    │  ← Database (Port 5432)
│   (postgres)    │
└────────┬────────┘
         │
┌────────▼────────┐
│  Backend API    │  ← Spring Boot API (Port 8080)
│     (api)       │
└─────────────────┘
```

### Services Overview

**postgres:**
- Image: `postgres:14-alpine`
- Port: 5432
- Database: `expensedb`
- Health check: Automatic
- Data persistence: `postgres_data` volume

**api:**
- Built from Dockerfile
- Port: 8080
- Depends on: postgres
- Health check: `/api/health` endpoint
- Data persistence: `upload_data` and `log_data` volumes

### Step-by-Step Deployment

#### 1. Configure Environment Variables

Edit `docker-compose.yml` to update sensitive values:

```yaml
environment:
  # Change these values!
  POSTGRES_PASSWORD: your-secure-password
  JWT_SECRET: your-secure-jwt-secret-minimum-32-characters
```

Or create a `.env` file:

```bash
# .env
POSTGRES_PASSWORD=your-secure-password
JWT_SECRET=your-secure-jwt-secret-minimum-32-characters
DB_USERNAME=postgres
DB_NAME=expensedb
```

Update `docker-compose.yml` to use environment variables:

```yaml
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  JWT_SECRET: ${JWT_SECRET}
```

#### 2. Build Images

```bash
# Build all services
docker-compose build

# Build with no cache (if needed)
docker-compose build --no-cache
```

#### 3. Start Services

```bash
# Start in detached mode
docker-compose up -d

# Start with logs visible
docker-compose up

# Start specific service
docker-compose up -d postgres
```

#### 4. Verify Services

```bash
# Check service status
docker-compose ps

# Expected output:
# NAME          STATUS        PORTS
# expense-api   Up (healthy)  0.0.0.0:8080->8080/tcp
# expense-db    Up (healthy)  0.0.0.0:5432->5432/tcp

# Test backend health
curl http://localhost:8080/api/health

# Expected: {"status":"UP"}
```

#### 5. View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f api
docker-compose logs -f postgres

# Last 100 lines
docker-compose logs --tail=100 api
```

### Managing Services

```bash
# Stop services (keeps containers)
docker-compose stop

# Start stopped services
docker-compose start

# Restart services
docker-compose restart

# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (deletes data!)
docker-compose down -v

# Scale services (if configured)
docker-compose up -d --scale api=3
```

## Individual Container Deployment

### 1. Create Network

```bash
docker network create expense-network
```

### 2. Run PostgreSQL

```bash
docker run -d \
  --name expense-db \
  --network expense-network \
  -e POSTGRES_DB=expensedb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -v expense-postgres-data:/var/lib/postgresql/data \
  postgres:14-alpine
```

### 3. Build Backend Image

```bash
# Build application
mvn clean package -DskipTests

# Build Docker image
docker build -t expense-management-api:1.0.0 .
```

### 4. Run Backend Container

```bash
docker run -d \
  --name expense-api \
  --network expense-network \
  -e DB_URL="jdbc:postgresql://expense-db:5432/expensedb" \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET="your-secure-jwt-secret-minimum-32-characters" \
  -e UPLOAD_DIR="/var/app/uploads" \
  -p 8080:8080 \
  -v expense-upload-data:/var/app/uploads \
  -v expense-log-data:/var/log/expense-management-api \
  expense-management-api:1.0.0
```

### 5. Verify Deployment

```bash
# Check containers
docker ps

# Test health
curl http://localhost:8080/api/health

# View logs
docker logs -f expense-api
```

## Production Deployment

### 1. Production Docker Compose

Create `docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  api:
    image: your-registry/expense-management-api:1.0.0
    container_name: expense-api-prod
    environment:
      DB_URL: jdbc:postgresql://your-rds-endpoint:5432/expensedb
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      UPLOAD_DIR: /var/app/uploads
      SPRING_PROFILES_ACTIVE: prod
    ports:
      - "8080:8080"
    volumes:
      - /mnt/efs/uploads:/var/app/uploads
      - /var/log/expense-api:/var/log/expense-management-api
    restart: always
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
```

### 2. Deploy to Production

```bash
# Set environment variables
export DB_USERNAME=expenseadmin
export DB_PASSWORD=your-secure-password
export JWT_SECRET=your-production-jwt-secret

# Deploy
docker-compose -f docker-compose.prod.yml up -d

# Verify
docker-compose -f docker-compose.prod.yml ps
```

### 3. Use External Database

For production, use AWS RDS instead of containerized PostgreSQL:

```yaml
services:
  api:
    environment:
      DB_URL: jdbc:postgresql://your-rds-endpoint.rds.amazonaws.com:5432/expensedb
      # Remove postgres service from docker-compose
```

### 4. Set Up Reverse Proxy

Use Nginx as a reverse proxy:

```yaml
services:
  nginx:
    image: nginx:alpine
    container_name: expense-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - api
    restart: always
```

Create `nginx.conf`:

```nginx
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server api:8080;
    }

    server {
        listen 80;
        server_name api.yourdomain.com;

        location / {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

## Configuration

### Environment Variables

All environment variables can be configured in `docker-compose.yml` or `.env` file:

**Required:**
- `DB_URL` - Database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret (minimum 32 characters)

**Optional:**
- `UPLOAD_DIR` - Receipt upload directory (default: `/var/app/uploads`)
- `SERVER_PORT` - API port (default: `8080`)
- `SPRING_PROFILES_ACTIVE` - Spring profile (default: `prod`)
- `LOG_FILE` - Log file path

### Volumes

**postgres_data:**
- Stores PostgreSQL database files
- Location: Docker managed volume
- Backup: `docker run --rm -v postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres-backup.tar.gz /data`

**upload_data:**
- Stores uploaded receipt files
- Location: Docker managed volume
- Backup: `docker run --rm -v upload_data:/data -v $(pwd):/backup alpine tar czf /backup/uploads-backup.tar.gz /data`

**log_data:**
- Stores application logs
- Location: Docker managed volume

### Ports

- `5432` - PostgreSQL (only expose for development)
- `8080` - Backend API
- `80/443` - Nginx (if using reverse proxy)

## Monitoring and Logs

### View Container Status

```bash
# List running containers
docker-compose ps

# View resource usage
docker stats

# Inspect container
docker inspect expense-api
```

### Access Logs

```bash
# Application logs
docker-compose logs -f api

# Database logs
docker-compose logs -f postgres

# All logs
docker-compose logs -f

# Save logs to file
docker-compose logs > logs.txt
```

### Execute Commands in Container

```bash
# Open shell in API container
docker-compose exec api sh

# Open shell in database container
docker-compose exec postgres psql -U postgres -d expensedb

# Run one-off command
docker-compose exec api java -version
```

### Health Checks

```bash
# Check health status
docker inspect expense-api | grep -A 10 Health

# Manual health check
curl http://localhost:8080/api/health
```

## Backup and Restore

### Backup Database

```bash
# Backup using pg_dump
docker-compose exec postgres pg_dump -U postgres expensedb > backup.sql

# Backup volume
docker run --rm \
  -v expense-management-system_postgres_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/postgres-backup.tar.gz /data
```

### Restore Database

```bash
# Restore from SQL dump
docker-compose exec -T postgres psql -U postgres expensedb < backup.sql

# Restore volume
docker run --rm \
  -v expense-management-system_postgres_data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/postgres-backup.tar.gz -C /
```

### Backup Uploads

```bash
# Backup uploads volume
docker run --rm \
  -v expense-management-system_upload_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/uploads-backup.tar.gz /data
```

## Troubleshooting

### Container Won't Start

```bash
# Check logs
docker-compose logs api

# Check if port is already in use
netstat -an | grep 8080
lsof -i :8080

# Remove and recreate
docker-compose down
docker-compose up -d
```

### Database Connection Errors

```bash
# Check if database is healthy
docker-compose ps

# Test database connection
docker-compose exec postgres psql -U postgres -d expensedb -c "SELECT 1"

# Check network
docker network inspect expense-management-system_default
```

### Out of Memory

```bash
# Check memory usage
docker stats

# Increase memory limit in docker-compose.yml
services:
  api:
    mem_limit: 1g
    mem_reservation: 512m
```

### Disk Space Issues

```bash
# Check disk usage
docker system df

# Clean up unused resources
docker system prune -a

# Remove unused volumes
docker volume prune
```

### Application Errors

```bash
# View detailed logs
docker-compose logs --tail=200 api

# Check environment variables
docker-compose exec api env | grep DB_

# Restart service
docker-compose restart api
```

## Performance Tuning

### JVM Options

Add JVM options in Dockerfile:

```dockerfile
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Database Connection Pool

Configure in `application-prod.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
```

### Resource Limits

Set in `docker-compose.yml`:

```yaml
services:
  api:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

## Security Best Practices

1. **Don't expose PostgreSQL port in production**
   ```yaml
   # Remove this in production:
   ports:
     - "5432:5432"
   ```

2. **Use secrets for sensitive data**
   ```yaml
   secrets:
     db_password:
       file: ./secrets/db_password.txt
   ```

3. **Run as non-root user**
   ```dockerfile
   USER 1000:1000
   ```

4. **Keep images updated**
   ```bash
   docker-compose pull
   docker-compose up -d
   ```

5. **Use read-only root filesystem**
   ```yaml
   services:
     api:
       read_only: true
       tmpfs:
         - /tmp
   ```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Build and push Docker image
        run: |
          docker build -t myregistry/expense-api:${{ github.sha }} .
          docker push myregistry/expense-api:${{ github.sha }}
      
      - name: Deploy to server
        run: |
          ssh user@server "docker pull myregistry/expense-api:${{ github.sha }}"
          ssh user@server "docker-compose up -d"
```

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Main Deployment Guide](DEPLOYMENT.md)
- [Production Deployment Guide](PRODUCTION_DEPLOYMENT_GUIDE.md)

## Support

For Docker-specific issues:
1. Check container logs: `docker-compose logs`
2. Verify configuration: `docker-compose config`
3. Review this guide
4. Consult Docker documentation
5. Create a GitHub issue with logs and configuration
