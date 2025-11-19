# Production Deployment Checklist

Use this checklist to ensure all steps are completed before and after deploying to production.

## Pre-Deployment Checklist

### Code and Build

- [ ] All code changes are committed and pushed to version control
- [ ] Code has been reviewed and approved
- [ ] All tests pass locally
- [ ] Production profile configuration is complete (`application-prod.yml`)
- [ ] Build scripts are tested and working
- [ ] Production JAR builds successfully
- [ ] JAR file size is reasonable (check for bloat)

### Configuration

- [ ] Database connection details are configured
- [ ] JWT secret is generated (minimum 32 characters, cryptographically secure)
- [ ] All required environment variables are documented
- [ ] File upload directory is configured
- [ ] Logging configuration is appropriate for production
- [ ] API documentation endpoints are disabled (or secured)
- [ ] CORS settings are configured correctly
- [ ] Error messages don't expose sensitive information

### Database

- [ ] AWS RDS PostgreSQL instance is created and running
- [ ] Database is accessible from application server
- [ ] Security groups allow connections on port 5432
- [ ] Database credentials are secure and stored safely
- [ ] Database backups are configured
- [ ] Flyway migrations are tested
- [ ] Database has sufficient storage allocated
- [ ] Performance Insights is enabled (optional but recommended)

### Security

- [ ] Passwords are strong and unique
- [ ] Secrets are not committed to version control
- [ ] JWT secret is stored securely
- [ ] HTTPS/TLS is configured (via load balancer or reverse proxy)
- [ ] Security groups follow principle of least privilege
- [ ] IAM roles are configured with minimal permissions
- [ ] Application runs as non-root user
- [ ] File upload directory has appropriate permissions

### Infrastructure

- [ ] Server/instance is provisioned (EC2, ECS, etc.)
- [ ] Java 17 is installed on the server
- [ ] Required ports are open (8080 or configured port)
- [ ] Load balancer is configured (if using multiple instances)
- [ ] Auto-scaling is configured (if needed)
- [ ] Monitoring and alerting are set up
- [ ] Log aggregation is configured (CloudWatch, etc.)

### Testing

- [ ] Application starts successfully with production profile
- [ ] Health check endpoint responds correctly
- [ ] Database connection is successful
- [ ] Authentication endpoints work
- [ ] File upload functionality works
- [ ] All critical API endpoints are tested
- [ ] Performance testing is completed
- [ ] Load testing is completed (if applicable)

## Deployment Steps

### 1. Build Production JAR

```bash
# Windows
.\build-production.ps1

# Linux/Mac
./build-production.sh
```

- [ ] Build completed successfully
- [ ] JAR file exists in `target/` directory
- [ ] JAR file size is as expected

### 2. Set Environment Variables

- [ ] DB_URL is set correctly
- [ ] DB_USERNAME is set correctly
- [ ] DB_PASSWORD is set correctly
- [ ] JWT_SECRET is set correctly
- [ ] UPLOAD_DIR is set (if different from default)
- [ ] All environment variables are verified

### 3. Deploy Application

Choose your deployment method:

#### EC2 Deployment
- [ ] JAR file uploaded to server
- [ ] Systemd service file created
- [ ] Service enabled and started
- [ ] Service status is active/running

#### Elastic Beanstalk
- [ ] EB environment created
- [ ] Environment variables configured
- [ ] Application deployed
- [ ] Environment health is green

#### Docker/ECS
- [ ] Docker image built
- [ ] Image pushed to ECR
- [ ] Task definition created
- [ ] Service created and running
- [ ] Tasks are healthy

### 4. Verify Deployment

- [ ] Application is running
- [ ] Health check endpoint responds: `curl http://your-server:8080/api/health`
- [ ] Response shows `"status": "UP"`
- [ ] Application logs show no errors
- [ ] Database migrations completed successfully

## Post-Deployment Checklist

### Immediate Verification (0-15 minutes)

- [ ] Health check endpoint is accessible
- [ ] Application logs show successful startup
- [ ] Database connection is established
- [ ] No error messages in logs
- [ ] Memory usage is normal
- [ ] CPU usage is normal

### Functional Testing (15-30 minutes)

- [ ] User registration works
- [ ] User login works and returns JWT token
- [ ] Create expense works
- [ ] Retrieve expenses works
- [ ] Update expense works
- [ ] Delete expense works
- [ ] File upload works
- [ ] Expense approval workflow works
- [ ] Report generation works

### Monitoring Setup (30-60 minutes)

- [ ] CloudWatch logs are being collected
- [ ] CloudWatch metrics are being reported
- [ ] Alarms are configured for:
  - [ ] High CPU usage (>80%)
  - [ ] High memory usage (>80%)
  - [ ] Application errors
  - [ ] Database connection failures
  - [ ] Disk space (>80%)
- [ ] Dashboard is created for key metrics
- [ ] Alert notifications are configured

### Documentation

- [ ] Deployment date and time recorded
- [ ] Deployed version number recorded
- [ ] Configuration changes documented
- [ ] Known issues documented
- [ ] Rollback procedure documented
- [ ] Team notified of deployment

## Monitoring and Maintenance

### Daily Tasks

- [ ] Check application logs for errors
- [ ] Verify health check endpoint
- [ ] Monitor CloudWatch alarms
- [ ] Check disk space usage

### Weekly Tasks

- [ ] Review application metrics
- [ ] Check database performance
- [ ] Review security logs
- [ ] Clean up old log files
- [ ] Verify backups are working

### Monthly Tasks

- [ ] Review and optimize database queries
- [ ] Update dependencies (security patches)
- [ ] Review and rotate secrets
- [ ] Capacity planning review
- [ ] Cost optimization review

## Rollback Procedure

If issues are detected after deployment:

1. [ ] Stop the current application
2. [ ] Deploy previous version
3. [ ] Verify health check
4. [ ] Test critical functionality
5. [ ] Document the issue
6. [ ] Investigate root cause

## Emergency Contacts

Document key contacts for production issues:

- **DevOps Lead**: [Name/Contact]
- **Database Admin**: [Name/Contact]
- **Security Team**: [Name/Contact]
- **On-Call Engineer**: [Name/Contact]

## Troubleshooting Quick Reference

### Application Won't Start

1. Check Java version: `java -version`
2. Check environment variables: `echo $DB_URL`
3. Check logs: `tail -f /var/log/expense-management-api/application.log`
4. Test database connection: `psql -h <rds-endpoint> -U <username> -d expensedb`

### High Memory Usage

1. Check JVM heap settings
2. Review application logs for memory leaks
3. Consider increasing instance size
4. Enable heap dump on OutOfMemoryError

### Database Connection Issues

1. Verify RDS security group rules
2. Test connection from application server
3. Check database credentials
4. Review connection pool settings

### Slow Response Times

1. Check database query performance
2. Review application logs for slow queries
3. Check CPU and memory usage
4. Consider adding database indexes
5. Review connection pool settings

## Success Criteria

Deployment is considered successful when:

- [ ] Application is running and accessible
- [ ] Health check returns "UP" status
- [ ] All critical functionality works
- [ ] No errors in application logs
- [ ] Monitoring and alerts are active
- [ ] Performance meets requirements
- [ ] Team is notified and documentation is updated

## Sign-Off

- **Deployed by**: ___________________
- **Date**: ___________________
- **Version**: ___________________
- **Approved by**: ___________________
- **Notes**: ___________________

---

**Remember**: Always have a rollback plan ready before deploying to production!
