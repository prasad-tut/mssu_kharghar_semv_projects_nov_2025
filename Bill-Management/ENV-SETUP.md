# Environment Configuration Guide

## Overview

This project uses a centralized `.env` file for all configuration. This makes it easy to manage settings across different environments without modifying multiple files.

## Quick Setup

### 1. Create your .env file

```bash
# Copy the example file
cp .env.example .env

# Edit with your values
# Windows: notepad .env
# Mac/Linux: nano .env
```

### 2. Update the values

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=billmanagement
DB_USERNAME=postgres
DB_PASSWORD=your_actual_password

# Backend Configuration
BACKEND_PORT=7890
SPRING_PROFILE=dev

# Frontend Configuration
FRONTEND_PORT=80
API_BASE_URL=http://localhost:7890
```

### 3. Generate frontend config

```bash
# Windows
generate-frontend-config.bat

# Mac/Linux
chmod +x generate-frontend-config.sh
./generate-frontend-config.sh
```

## Environment Variables

### Database Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database host | localhost | Yes |
| `DB_PORT` | Database port | 5432 | Yes |
| `DB_NAME` | Database name | billmanagement | Yes |
| `DB_USERNAME` | Database user | postgres | Yes |
| `DB_PASSWORD` | Database password | - | Yes |

### Backend Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `BACKEND_PORT` | Backend server port | 7890 | Yes |
| `SPRING_PROFILE` | Active Spring profile (dev/prod) | dev | Yes |

### Frontend Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `FRONTEND_PORT` | Frontend port (Docker) | 80 | Yes |
| `API_BASE_URL` | Backend API URL | http://localhost:7890 | Yes |

### AWS Configuration (Production Only)

| Variable | Description | Required |
|----------|-------------|----------|
| `AWS_REGION` | AWS region | Yes |
| `AWS_ACCOUNT_ID` | AWS account ID | Yes |
| `ECR_REPO_NAME` | ECR repository name | Yes |
| `ECS_CLUSTER` | ECS cluster name | Yes |
| `ECS_SERVICE` | ECS service name | Yes |
| `S3_BUCKET` | S3 bucket for frontend | Yes |

## Usage

### Local Development

```bash
# 1. Set up .env file
cp .env.example .env
# Edit .env with your values

# 2. Generate frontend config
./generate-frontend-config.sh  # or .bat on Windows

# 3. Start database only
docker-compose -f docker-compose.dev.yml up -d

# 4. Run backend manually
cd bill_management
mvn spring-boot:run

# 5. Open frontend
# Open frontend/index.html in browser
```

### Docker Compose (Full Stack)

```bash
# 1. Set up .env file
cp .env.example .env

# 2. Start all services
docker-compose up -d

# Access:
# - Frontend: http://localhost:80
# - Backend: http://localhost:8080
# - Database: localhost:5432
```

### Production Deployment

```bash
# 1. Update .env with production values
SPRING_PROFILE=prod
API_BASE_URL=https://your-domain.com/api
AWS_ACCOUNT_ID=123456789012
# ... other AWS settings

# 2. Generate frontend config
./generate-frontend-config.sh

# 3. Deploy to AWS
./deploy-aws.sh
```

## Different Environments

### Development (.env)
```env
SPRING_PROFILE=dev
API_BASE_URL=http://localhost:7890
DB_PASSWORD=root
```

### Staging (.env.staging)
```env
SPRING_PROFILE=prod
API_BASE_URL=https://staging-api.example.com
DB_PASSWORD=staging_secure_password
```

### Production (.env.production)
```env
SPRING_PROFILE=prod
API_BASE_URL=https://api.example.com
DB_PASSWORD=production_secure_password
```

## Switching Environments

```bash
# Use different env file
cp .env.staging .env
./generate-frontend-config.sh
docker-compose up -d
```

## Security Best Practices

1. **Never commit .env files**
   - `.env` is in `.gitignore`
   - Only commit `.env.example`

2. **Use strong passwords**
   - Especially for production
   - Use password managers

3. **Rotate credentials regularly**
   - Update `.env` file
   - Restart services

4. **Use AWS Secrets Manager for production**
   - Store sensitive data in AWS
   - Reference in ECS task definition

## Troubleshooting

### Frontend can't connect to backend

1. Check `API_BASE_URL` in `.env`
2. Regenerate frontend config:
   ```bash
   ./generate-frontend-config.sh
   ```
3. Clear browser cache

### Docker services won't start

1. Check `.env` file exists
2. Verify all required variables are set
3. Check for syntax errors in `.env`

### Database connection failed

1. Verify `DB_PASSWORD` in `.env`
2. Check PostgreSQL is running
3. Verify database exists:
   ```bash
   psql -U postgres -c "\l"
   ```

## Advanced Configuration

### Using environment-specific files

```bash
# Load specific environment
export $(cat .env.production | xargs)
docker-compose up -d
```

### Override specific variables

```bash
# Override in command line
DB_PASSWORD=newpass docker-compose up -d
```

### Validate configuration

```bash
# Check all variables are set
cat .env | grep -v '^#' | grep '='
```

## CI/CD Integration

### GitHub Actions

```yaml
- name: Setup environment
  run: |
    echo "DB_PASSWORD=${{ secrets.DB_PASSWORD }}" >> .env
    echo "AWS_ACCOUNT_ID=${{ secrets.AWS_ACCOUNT_ID }}" >> .env
```

### Jenkins

```groovy
withCredentials([string(credentialsId: 'db-password', variable: 'DB_PASSWORD')]) {
    sh 'echo "DB_PASSWORD=$DB_PASSWORD" >> .env'
}
```

## Summary

✅ Single source of truth for configuration
✅ Easy to switch between environments
✅ Secure (not committed to git)
✅ Works with Docker Compose
✅ Supports CI/CD pipelines
✅ Simple to maintain

For more information, see:
- [README.md](README.md) - Project overview
- [QUICKSTART.md](QUICKSTART.md) - Quick start guide
- [DEPLOYMENT.md](DEPLOYMENT.md) - Deployment guide
