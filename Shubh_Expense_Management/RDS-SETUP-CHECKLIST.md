# AWS RDS PostgreSQL Setup Checklist

Use this checklist to ensure all steps are completed for RDS setup.

## ✅ Pre-Setup Checklist

- [ ] AWS Account created and accessible
- [ ] AWS CLI installed (`aws --version`)
- [ ] AWS CLI configured (`aws configure`)
- [ ] PostgreSQL client installed (`psql --version`)
- [ ] Verified AWS permissions (RDS, EC2, VPC access)
- [ ] Decided on database configuration (instance class, storage, etc.)
- [ ] Generated strong master password (min 8 chars, mixed case, numbers, symbols)

## ✅ RDS Instance Creation

- [ ] Created RDS PostgreSQL instance (via script or console)
- [ ] Configured instance with appropriate class (db.t3.micro for dev, larger for prod)
- [ ] Set allocated storage (20GB minimum)
- [ ] Enabled storage autoscaling
- [ ] Configured backup retention (7-30 days)
- [ ] Enabled encryption at rest
- [ ] Enabled CloudWatch logs export
- [ ] Added appropriate tags (Project, Environment, etc.)
- [ ] Waited for instance to become available (5-10 minutes)

## ✅ Network & Security Configuration

- [ ] Created or selected VPC
- [ ] Created or selected DB subnet group
- [ ] Created security group for RDS
- [ ] Added inbound rule for PostgreSQL (port 5432)
- [ ] Restricted access to specific IPs or security groups
- [ ] Configured public accessibility (yes for dev, no for prod with VPN)
- [ ] Verified network connectivity (`telnet` or `nc`)

## ✅ Database Configuration

- [ ] Created initial database (`expensedb`)
- [ ] Verified master username (`expenseadmin`)
- [ ] Saved master password securely
- [ ] Retrieved RDS endpoint address
- [ ] Retrieved RDS port (default 5432)
- [ ] Saved connection details to secure location

## ✅ Connection Testing

- [ ] Tested network connectivity to RDS endpoint
- [ ] Connected using psql client
- [ ] Verified PostgreSQL version
- [ ] Listed databases
- [ ] Checked table count (should be 0 initially)
- [ ] Ran basic SQL query (`SELECT 1;`)
- [ ] Verified transaction support

## ✅ Application Configuration

- [ ] Created `.env` file from `.env.example`
- [ ] Set `DB_URL` environment variable
- [ ] Set `DB_USERNAME` environment variable
- [ ] Set `DB_PASSWORD` environment variable
- [ ] Set `JWT_SECRET` environment variable
- [ ] Verified `.env` is in `.gitignore`
- [ ] Created `application-prod.yml` configuration
- [ ] Configured connection pool settings
- [ ] Configured logging for production

## ✅ Database Migrations

- [ ] Verified Flyway configuration in `application.yml`
- [ ] Ran database migrations (`mvn flyway:migrate`)
- [ ] Verified migration success
- [ ] Checked `flyway_schema_history` table
- [ ] Verified all tables created (users, categories, expenses, receipts)
- [ ] Checked table indexes
- [ ] Verified seed data (categories)

## ✅ Application Testing

- [ ] Ran Java connection test (`mvn test -Dtest=RDSConnectionTest`)
- [ ] Started application with prod profile
- [ ] Verified application connects to RDS
- [ ] Tested health check endpoint (`/api/health`)
- [ ] Tested user registration
- [ ] Tested user login
- [ ] Created test expense
- [ ] Verified data persists in RDS
- [ ] Tested file upload (receipt)
- [ ] Ran full test suite

## ✅ Monitoring & Logging

- [ ] Enabled Enhanced Monitoring
- [ ] Configured CloudWatch alarms for:
  - [ ] CPU utilization
  - [ ] Database connections
  - [ ] Free storage space
  - [ ] Read/Write latency
- [ ] Verified CloudWatch logs are being exported
- [ ] Set up log retention policies
- [ ] Configured application logging to file
- [ ] Tested log aggregation (if using CloudWatch Logs)

## ✅ Security Hardening

- [ ] Verified strong master password
- [ ] Enabled encryption at rest
- [ ] Configured SSL/TLS for connections
- [ ] Restricted security group to minimum required access
- [ ] Enabled deletion protection (for production)
- [ ] Configured automated backups
- [ ] Tested backup restoration
- [ ] Reviewed IAM policies
- [ ] Considered AWS Secrets Manager for credentials
- [ ] Enabled VPC Flow Logs (optional)
- [ ] Configured parameter group with secure settings

## ✅ Documentation

- [ ] Documented RDS endpoint and connection details
- [ ] Saved connection details securely (not in git)
- [ ] Created runbook for common operations
- [ ] Documented backup and restore procedures
- [ ] Documented disaster recovery plan
- [ ] Updated team wiki/documentation
- [ ] Shared access credentials securely with team

## ✅ Cost Optimization

- [ ] Selected appropriate instance class for workload
- [ ] Configured storage autoscaling
- [ ] Set appropriate backup retention period
- [ ] Considered Reserved Instances for production
- [ ] Set up billing alerts
- [ ] Reviewed and optimized connection pool settings
- [ ] Scheduled non-production instances to stop during off-hours (optional)

## ✅ Production Readiness (if deploying to production)

- [ ] Enabled Multi-AZ deployment for high availability
- [ ] Configured read replicas (if needed)
- [ ] Set up automated backups with appropriate retention
- [ ] Tested failover scenarios
- [ ] Configured maintenance window
- [ ] Set up monitoring dashboards
- [ ] Configured alerting for critical metrics
- [ ] Performed load testing
- [ ] Documented scaling procedures
- [ ] Created disaster recovery plan
- [ ] Performed security audit
- [ ] Obtained security team approval

## ✅ Post-Setup Verification

- [ ] Application running successfully with RDS
- [ ] All CRUD operations working
- [ ] File uploads working
- [ ] Reports generating correctly
- [ ] Authentication working
- [ ] Authorization working
- [ ] No connection pool exhaustion
- [ ] No memory leaks
- [ ] Acceptable response times
- [ ] Logs being generated correctly

## ✅ Cleanup (Development Only)

If this is a temporary development setup:
- [ ] Documented how to stop RDS instance
- [ ] Documented how to start RDS instance
- [ ] Documented how to delete RDS instance
- [ ] Created final snapshot before deletion
- [ ] Removed security group rules
- [ ] Cleaned up unused snapshots

## 📝 Notes

Use this section to document any issues encountered or deviations from the standard setup:

```
Date: _______________
Setup performed by: _______________

RDS Endpoint: _______________________________________________
Database Name: _______________
Master Username: _______________
Instance Class: _______________
Storage: _______________
Region: _______________
VPC: _______________
Security Group: _______________

Issues encountered:
- 
- 
- 

Resolutions:
- 
- 
- 

Additional notes:
- 
- 
- 
```

## ✅ Final Sign-Off

- [ ] All checklist items completed
- [ ] Application tested and working
- [ ] Documentation updated
- [ ] Team notified
- [ ] Ready for next phase

**Completed by:** _______________  
**Date:** _______________  
**Reviewed by:** _______________  
**Date:** _______________  

---

**Status:** ⬜ Not Started | 🟡 In Progress | ✅ Complete

**Overall Progress:** _____ / _____ items completed
