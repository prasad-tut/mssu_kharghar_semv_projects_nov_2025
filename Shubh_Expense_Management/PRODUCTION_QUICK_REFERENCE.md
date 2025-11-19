# Production Quick Reference Card

Quick commands and information for production deployment and operations.

## 🚀 Build Commands

### Build Production JAR
```bash
# Windows
.\build-production.ps1

# Linux/Mac
./build-production.sh

# Manual
mvn clean package -DskipTests
```

**Output**: `target/expense-management-system-1.0.0.jar`

## 🔧 Required Environment Variables

```bash
DB_URL=jdbc:postgresql://your-rds-endpoint:5432/expensedb
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your-secure-jwt-secret-minimum-32-characters
```

## ▶️ Run Commands

### Standard Run
```bash
java -jar -Dspring.profiles.active=prod target/expense-management-system-1.0.0.jar
```

### With JVM Options
```bash
java -Xms512m -Xmx1024m -XX:+UseG1GC \
  -Dspring.profiles.active=prod \
  -jar target/expense-management-system-1.0.0.jar
```

### Docker Run
```bash
docker-compose up -d
```

## 🏥 Health Check

### Test Health Endpoint
```bash
# Windows
.\test-health-endpoint.ps1

# Linux/Mac
./test-health-endpoint.sh

# Manual
curl http://localhost:8080/api/health
```

### Expected Response
```json
{
  "status": "UP",
  "timestamp": "2024-11-15T10:30:00",
  "service": "Expense Management API",
  "version": "1.0.0"
}
```

## 📊 Monitoring Endpoints

| Endpoint | Purpose |
|----------|---------|
| `/api/health` | Application health |
| `/actuator/health` | Detailed health info |
| `/actuator/metrics` | Application metrics |
| `/actuator/info` | Application info |

## 🐳 Docker Commands

```bash
# Build image
docker build -t expense-management-api:1.0.0 .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f api

# Stop services
docker-compose down

# Restart service
docker-compose restart api
```

## 📝 Log Locations

| Environment | Location |
|-------------|----------|
| Local | Console output |
| EC2 | `/var/log/expense-management-api/application.log` |
| Docker | `docker logs expense-api` |
| Systemd | `journalctl -u expense-api -f` |

## 🔍 Troubleshooting Commands

### Check Java Version
```bash
java -version
# Should be Java 17+
```

### Test Database Connection
```bash
psql -h your-rds-endpoint -U your_username -d expensedb
```

### Check Environment Variables
```bash
# Linux/Mac
echo $DB_URL
echo $JWT_SECRET

# Windows
echo $env:DB_URL
echo $env:JWT_SECRET
```

### View Application Logs
```bash
# Systemd
sudo journalctl -u expense-api -f

# Docker
docker logs -f expense-api

# File
tail -f /var/log/expense-management-api/application.log
```

### Check Service Status
```bash
# Systemd
sudo systemctl status expense-api

# Docker
docker ps | grep expense-api
```

## 🔐 Generate Secure JWT Secret

```bash
# OpenSSL
openssl rand -base64 32

# Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"

# Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

## 🛠️ Common Operations

### Restart Application
```bash
# Systemd
sudo systemctl restart expense-api

# Docker
docker-compose restart api
```

### Stop Application
```bash
# Systemd
sudo systemctl stop expense-api

# Docker
docker-compose stop api
```

### View Real-time Logs
```bash
# Systemd
sudo journalctl -u expense-api -f

# Docker
docker-compose logs -f api
```

### Check Disk Space
```bash
df -h
```

### Check Memory Usage
```bash
free -h
```

## 📦 Deployment Platforms

| Platform | Command |
|----------|---------|
| **EC2** | `scp` JAR + `systemctl start` |
| **Elastic Beanstalk** | `eb deploy` |
| **Docker** | `docker-compose up -d` |
| **ECS** | Push to ECR + Update service |

## 🚨 Emergency Procedures

### Application Not Responding
1. Check health endpoint
2. View logs for errors
3. Check database connectivity
4. Restart application
5. Check resource usage (CPU/memory)

### Database Connection Failed
1. Verify RDS is running
2. Check security group rules
3. Test connection with psql
4. Verify credentials
5. Check connection pool settings

### High Memory Usage
1. Check JVM heap settings
2. Review logs for memory leaks
3. Consider increasing instance size
4. Restart application

## 📞 Quick Links

- **Full Deployment Guide**: `PRODUCTION_DEPLOYMENT_GUIDE.md`
- **Environment Variables**: `PRODUCTION_ENV_VARIABLES.md`
- **Deployment Checklist**: `PRODUCTION_CHECKLIST.md`
- **Setup Summary**: `PRODUCTION_SETUP_SUMMARY.md`

## 🎯 Key Ports

| Port | Service |
|------|---------|
| 8080 | Application (default) |
| 5432 | PostgreSQL |

## ✅ Pre-Deployment Checklist

- [ ] Environment variables set
- [ ] Database accessible
- [ ] JAR built successfully
- [ ] Health check tested
- [ ] Monitoring configured
- [ ] Backups enabled

## 📈 Performance Tuning

### JVM Options
```bash
-Xms512m              # Initial heap size
-Xmx1024m             # Maximum heap size
-XX:+UseG1GC          # Use G1 garbage collector
-XX:MaxGCPauseMillis=200  # GC pause target
```

### Connection Pool
```yaml
hikari:
  maximum-pool-size: 20
  minimum-idle: 10
```

---

**Keep this reference handy for quick production operations!** 📋
