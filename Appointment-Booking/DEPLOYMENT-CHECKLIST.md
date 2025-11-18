# Deployment Checklist

Complete checklist for deploying the Appointment Management System with Jenkins CI/CD.

---

## Pre-Deployment Checklist

### Local Development Setup

- [ ] Java 21 installed and configured
- [ ] Maven 3.9+ installed
- [ ] Git installed and configured
- [ ] IDE setup (IntelliJ IDEA, Eclipse, or VS Code)
- [ ] Application runs locally with H2 database
- [ ] All tests pass: `mvn test`
- [ ] Application builds successfully: `mvn clean package`

### Docker Setup (Optional but Recommended)

- [ ] Docker installed and running
- [ ] Docker Compose installed
- [ ] Application runs in Docker: `docker-compose up`
- [ ] Can access application at http://localhost:9080
- [ ] Database container working properly

---

## Jenkins Setup Checklist

### Jenkins Installation

- [ ] Jenkins installed (version 2.400+)
- [ ] Jenkins accessible at http://localhost:8080
- [ ] Initial admin password retrieved
- [ ] Admin user created
- [ ] Suggested plugins installed

### Required Plugins

- [ ] Pipeline plugin
- [ ] Git plugin
- [ ] Maven Integration plugin
- [ ] Docker Pipeline plugin
- [ ] AWS Steps plugin
- [ ] Amazon ECR plugin
- [ ] CloudBees AWS Credentials plugin
- [ ] JUnit plugin
- [ ] Workspace Cleanup plugin
- [ ] Blue Ocean (optional)

### Tool Configuration

- [ ] JDK 21 configured in Jenkins
  - Name: `JDK-21`
  - Path configured correctly
- [ ] Maven configured in Jenkins
  - Name: `Maven-3.9`
  - Auto-install or path configured
- [ ] Git configured in Jenkins
  - Default Git installation
- [ ] Docker configured (if using Docker builds)

### Credentials Configuration

- [ ] AWS credentials added
  - ID: `aws-credentials`
  - Access Key ID entered
  - Secret Access Key entered
- [ ] AWS Account ID added
  - ID: `aws-account-id`
  - 12-digit account number
- [ ] GitHub credentials added (if private repo)
  - ID: `github-credentials`
  - Username and token

---

## AWS Setup Checklist

### AWS Account Setup

- [ ] AWS account created and active
- [ ] IAM user created for Jenkins
- [ ] Access keys generated
- [ ] Appropriate IAM policies attached:
  - [ ] ECR full access
  - [ ] ECS full access
  - [ ] Elastic Beanstalk full access (if using EB)
  - [ ] CloudWatch Logs access
  - [ ] S3 access

### AWS RDS Database

- [ ] RDS MySQL instance created
  - [ ] Instance identifier: `appointment-db`
  - [ ] Engine: MySQL 8.0+
  - [ ] Instance class: db.t3.micro (or larger)
  - [ ] Storage: 20 GB minimum
  - [ ] Public accessibility enabled (for testing)
- [ ] Security group configured
  - [ ] Port 3306 open to application
- [ ] Database created: `appointmentdb`
- [ ] Master username and password saved securely
- [ ] Endpoint URL noted
- [ ] Schema initialized: `schema.sql` executed
- [ ] Sample data loaded: `data.sql` executed
- [ ] Connection tested from local machine

### AWS ECR (Container Registry)

- [ ] ECR repository created
  - [ ] Repository name: `appointment-management`
  - [ ] Region: us-east-1 (or your preferred region)
- [ ] Repository URI noted
- [ ] Lifecycle policy configured (optional)
- [ ] Scan on push enabled (optional)

### AWS ECS (Container Service)

- [ ] ECS cluster created
  - [ ] Cluster name: `appointment-cluster`
  - [ ] Launch type: Fargate
- [ ] Task definition created
  - [ ] Family: `appointment-task`
  - [ ] CPU: 256 (.25 vCPU)
  - [ ] Memory: 512 MB
  - [ ] Container image URI configured
  - [ ] Port mapping: 9080
  - [ ] Environment variables set
  - [ ] CloudWatch logs configured
- [ ] ECS service created
  - [ ] Service name: `appointment-service`
  - [ ] Desired count: 1
  - [ ] VPC and subnets configured
  - [ ] Security group allows port 9080
  - [ ] Public IP enabled
- [ ] Load balancer configured (optional)
- [ ] Auto-scaling configured (optional)

### Alternative: AWS Elastic Beanstalk

- [ ] EB CLI installed
- [ ] EB application created
  - [ ] Application name: `appointment-management`
- [ ] EB environment created
  - [ ] Environment name: `appointment-env`
  - [ ] Platform: Java 21 (Corretto)
- [ ] Environment variables configured
- [ ] RDS database linked
- [ ] Health check configured

---

## Pipeline Configuration Checklist

### Repository Setup

- [ ] Code pushed to GitHub/GitLab
- [ ] Repository URL noted
- [ ] Branch strategy defined (main/master)
- [ ] `.gitignore` configured properly
- [ ] Sensitive files not committed

### Jenkins Pipeline Job

- [ ] Pipeline job created
  - [ ] Job name: `appointment-management-pipeline`
- [ ] Pipeline configured
  - [ ] Definition: Pipeline script from SCM
  - [ ] SCM: Git
  - [ ] Repository URL entered
  - [ ] Credentials selected (if private)
  - [ ] Branch specified: `*/main`
  - [ ] Script path: `Jenkinsfile`
- [ ] Build triggers configured
  - [ ] GitHub webhook OR
  - [ ] SCM polling: `H/5 * * * *`
- [ ] Build environment configured
- [ ] Post-build actions configured (optional)

### GitHub Webhook (Recommended)

- [ ] Webhook created in GitHub
  - [ ] Payload URL: `http://your-jenkins-url:8080/github-webhook/`
  - [ ] Content type: application/json
  - [ ] Events: Push events
  - [ ] Active: Yes
- [ ] Webhook tested and working

### Pipeline Files

- [ ] `Jenkinsfile` reviewed and customized
  - [ ] AWS region correct
  - [ ] Account ID placeholder replaced
  - [ ] ECR repository name correct
  - [ ] ECS cluster name correct
  - [ ] ECS service name correct
- [ ] `task-definition.json` customized
  - [ ] Account ID replaced
  - [ ] Image URI correct
  - [ ] Environment variables set
  - [ ] Resource limits appropriate

---

## Application Configuration Checklist

### Production Configuration

- [ ] `application.properties` updated for production
  - [ ] Database URL points to RDS
  - [ ] Database credentials configured
  - [ ] Server port set (9080)
  - [ ] Profile set to `prod`
- [ ] Environment variables configured
  - [ ] `SPRING_PROFILES_ACTIVE=prod`
  - [ ] `SPRING_DATASOURCE_URL` set
  - [ ] `SPRING_DATASOURCE_USERNAME` set
  - [ ] `SPRING_DATASOURCE_PASSWORD` set (use Secrets Manager)
- [ ] CORS configuration reviewed
  - [ ] Allowed origins restricted (not `*` in production)
- [ ] Logging configured
  - [ ] Log level appropriate for production
  - [ ] CloudWatch logs enabled

### Security Configuration

- [ ] Passwords are strong and unique
- [ ] Credentials stored in AWS Secrets Manager
- [ ] Security groups properly configured
- [ ] Database not publicly accessible (production)
- [ ] HTTPS enabled (if using load balancer)
- [ ] API rate limiting considered
- [ ] Input validation implemented

---

## Testing Checklist

### Local Testing

- [ ] Application runs locally
- [ ] All unit tests pass
- [ ] Manual testing completed
- [ ] API endpoints tested with curl/Postman
- [ ] Frontend UI tested in browser
- [ ] Database operations verified

### Docker Testing

- [ ] Docker image builds successfully
- [ ] Container runs without errors
- [ ] Application accessible in container
- [ ] Database connectivity works
- [ ] Health check passes

### Jenkins Testing

- [ ] Pipeline syntax validated
- [ ] Test build triggered manually
- [ ] All stages complete successfully
- [ ] Artifacts archived correctly
- [ ] Test results published
- [ ] Docker image pushed to ECR
- [ ] Deployment to ECS successful

### Integration Testing

- [ ] Application deployed to AWS
- [ ] Application accessible via public URL
- [ ] Database connectivity verified
- [ ] API endpoints working
- [ ] Frontend loads correctly
- [ ] CRUD operations functional
- [ ] Performance acceptable

---

## Deployment Execution Checklist

### Pre-Deployment

- [ ] All checklists above completed
- [ ] Team notified of deployment
- [ ] Backup of current production (if applicable)
- [ ] Rollback plan prepared
- [ ] Monitoring tools ready

### Deployment Steps

1. **Trigger Build**
   - [ ] Push code to main branch OR
   - [ ] Click "Build Now" in Jenkins

2. **Monitor Pipeline**
   - [ ] Watch build progress
   - [ ] Check each stage completes
   - [ ] Review console output
   - [ ] Verify no errors

3. **Verify Deployment**
   - [ ] Check ECS service status
   - [ ] Verify task is running
   - [ ] Check application logs
   - [ ] Test health endpoint
   - [ ] Verify database connectivity

4. **Post-Deployment Testing**
   - [ ] Access application URL
   - [ ] Test all API endpoints
   - [ ] Verify frontend functionality
   - [ ] Check database operations
   - [ ] Monitor for errors

### Post-Deployment

- [ ] Application running successfully
- [ ] No errors in logs
- [ ] Performance metrics normal
- [ ] Team notified of successful deployment
- [ ] Documentation updated
- [ ] Deployment notes recorded

---

## Monitoring Checklist

### Application Monitoring

- [ ] CloudWatch logs configured
- [ ] Log groups created
- [ ] Logs streaming correctly
- [ ] Alarms configured for errors
- [ ] Dashboard created (optional)

### Infrastructure Monitoring

- [ ] ECS service health monitored
- [ ] RDS database metrics monitored
- [ ] CPU and memory usage tracked
- [ ] Network traffic monitored
- [ ] Disk space monitored

### Alerts Configuration

- [ ] Email alerts configured
- [ ] Slack notifications setup (optional)
- [ ] Alert thresholds defined
- [ ] On-call rotation defined
- [ ] Escalation procedures documented

---

## Rollback Checklist

### Preparation

- [ ] Previous version identified
- [ ] Rollback procedure documented
- [ ] Team aware of rollback process
- [ ] Database backup available

### Rollback Execution

- [ ] Stop current deployment
- [ ] Revert to previous ECS task definition
- [ ] Verify rollback successful
- [ ] Test application functionality
- [ ] Notify team of rollback
- [ ] Document rollback reason

---

## Maintenance Checklist

### Regular Maintenance

- [ ] Review logs weekly
- [ ] Check for security updates
- [ ] Update dependencies monthly
- [ ] Review and optimize costs
- [ ] Test backup and restore procedures

### Quarterly Reviews

- [ ] Review and update documentation
- [ ] Audit security configurations
- [ ] Review IAM permissions
- [ ] Optimize resource allocation
- [ ] Update disaster recovery plan

---

## Troubleshooting Quick Reference

### Build Fails

1. Check Jenkins console output
2. Verify credentials
3. Check tool configurations
4. Review Jenkinsfile syntax

### Deployment Fails

1. Check ECS service events
2. Review task definition
3. Verify security groups
4. Check CloudWatch logs

### Application Issues

1. Check application logs
2. Verify database connectivity
3. Check environment variables
4. Review security group rules

---

## Success Criteria

Deployment is successful when:

- ✅ Jenkins pipeline completes all stages
- ✅ Docker image pushed to ECR
- ✅ ECS service running with desired count
- ✅ Application accessible via URL
- ✅ Health check endpoint returns 200 OK
- ✅ API endpoints respond correctly
- ✅ Frontend loads without errors
- ✅ Database operations work
- ✅ No errors in CloudWatch logs
- ✅ Performance metrics within acceptable range

---

## Team Sign-Off

- [ ] Developer: Code reviewed and tested
- [ ] DevOps: Infrastructure configured
- [ ] QA: Testing completed
- [ ] Team Lead: Deployment approved

---

**Deployment Date:** _______________  
**Deployed By:** _______________  
**Version:** _______________  
**Notes:** _______________

---

**Last Updated:** November 2025  
**Maintained by:** Ishita Parkar, Kirshnandu Gurey, Prutha Jadhav, and Yash Adhau
