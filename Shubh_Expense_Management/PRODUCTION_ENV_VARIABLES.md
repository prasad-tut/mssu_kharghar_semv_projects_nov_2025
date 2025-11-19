# Production Environment Variables

This document describes all required and optional environment variables for running the Expense Management System backend in production.

## Required Environment Variables

### Database Configuration

- **DB_URL**: PostgreSQL database connection URL
  - Format: `jdbc:postgresql://<host>:<port>/<database>`
  - Example: `jdbc:postgresql://mydb.abc123.us-east-1.rds.amazonaws.com:5432/expensedb`
  - Required: Yes

- **DB_USERNAME**: Database username
  - Example: `expense_app_user`
  - Required: Yes

- **DB_PASSWORD**: Database password
  - Example: `SecurePassword123!`
  - Required: Yes
  - Security: Store securely, never commit to version control

### JWT Configuration

- **JWT_SECRET**: Secret key for JWT token signing
  - Minimum length: 256 bits (32 characters)
  - Example: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6`
  - Required: Yes
  - Security: Use a cryptographically secure random string, never commit to version control

## Optional Environment Variables

### Server Configuration

- **SERVER_PORT**: Port on which the application runs
  - Default: `8080`
  - Example: `8080`
  - Required: No

### File Storage

- **UPLOAD_DIR**: Directory path for storing uploaded receipt files
  - Default: `/var/app/uploads`
  - Example: `/var/app/uploads` or `/mnt/efs/receipts`
  - Required: No
  - Note: Ensure the directory exists and has write permissions

### Logging

- **LOG_FILE**: Path to the application log file
  - Default: `/var/log/expense-management-api/application.log`
  - Example: `/var/log/expense-management-api/application.log`
  - Required: No
  - Note: Ensure the directory exists and has write permissions

### API Documentation

- **API_DOCS_ENABLED**: Enable/disable OpenAPI documentation endpoint
  - Default: `false`
  - Values: `true` or `false`
  - Required: No
  - Recommendation: Keep disabled in production for security

- **SWAGGER_UI_ENABLED**: Enable/disable Swagger UI
  - Default: `false`
  - Values: `true` or `false`
  - Required: No
  - Recommendation: Keep disabled in production for security

## Setting Environment Variables

### Linux/Unix (Shell)

```bash
export DB_URL="jdbc:postgresql://mydb.abc123.us-east-1.rds.amazonaws.com:5432/expensedb"
export DB_USERNAME="expense_app_user"
export DB_PASSWORD="SecurePassword123!"
export JWT_SECRET="a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6"
export UPLOAD_DIR="/var/app/uploads"
```

### Docker

```dockerfile
ENV DB_URL=jdbc:postgresql://mydb.abc123.us-east-1.rds.amazonaws.com:5432/expensedb
ENV DB_USERNAME=expense_app_user
ENV DB_PASSWORD=SecurePassword123!
ENV JWT_SECRET=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6
ENV UPLOAD_DIR=/var/app/uploads
```

### Docker Compose

```yaml
environment:
  - DB_URL=jdbc:postgresql://mydb.abc123.us-east-1.rds.amazonaws.com:5432/expensedb
  - DB_USERNAME=expense_app_user
  - DB_PASSWORD=SecurePassword123!
  - JWT_SECRET=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6
  - UPLOAD_DIR=/var/app/uploads
```

### AWS Elastic Beanstalk

Use the EB CLI or AWS Console to set environment properties:

```bash
eb setenv DB_URL="jdbc:postgresql://..." \
         DB_USERNAME="expense_app_user" \
         DB_PASSWORD="SecurePassword123!" \
         JWT_SECRET="a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6"
```

### Kubernetes

Create a Secret:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: expense-api-secrets
type: Opaque
stringData:
  DB_URL: jdbc:postgresql://mydb.abc123.us-east-1.rds.amazonaws.com:5432/expensedb
  DB_USERNAME: expense_app_user
  DB_PASSWORD: SecurePassword123!
  JWT_SECRET: a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6
```

## Security Best Practices

1. **Never commit secrets to version control**
   - Use `.gitignore` to exclude environment files
   - Use secret management services (AWS Secrets Manager, HashiCorp Vault, etc.)

2. **Rotate secrets regularly**
   - Change JWT_SECRET periodically
   - Update database passwords on a schedule

3. **Use strong passwords**
   - Minimum 16 characters
   - Mix of uppercase, lowercase, numbers, and special characters

4. **Restrict access**
   - Limit who can view/modify environment variables
   - Use IAM roles and policies in cloud environments

5. **Use AWS Secrets Manager or Parameter Store**
   - For AWS deployments, consider using AWS Secrets Manager
   - Automatically rotate credentials
   - Audit access to secrets

## Generating a Secure JWT Secret

Use one of these methods to generate a secure JWT secret:

### OpenSSL
```bash
openssl rand -base64 32
```

### Python
```python
import secrets
print(secrets.token_urlsafe(32))
```

### Node.js
```javascript
require('crypto').randomBytes(32).toString('base64')
```

## Verification

After setting environment variables, verify they are correctly configured:

```bash
# Check if variables are set (without revealing values)
echo "DB_URL is set: ${DB_URL:+yes}"
echo "DB_USERNAME is set: ${DB_USERNAME:+yes}"
echo "DB_PASSWORD is set: ${DB_PASSWORD:+yes}"
echo "JWT_SECRET is set: ${JWT_SECRET:+yes}"
```

## Troubleshooting

### Application fails to start

1. Check that all required environment variables are set
2. Verify database connectivity from the application server
3. Check application logs for specific error messages

### Database connection errors

1. Verify DB_URL format is correct
2. Check that database credentials are valid
3. Ensure security groups/firewalls allow connections
4. Test connection using psql or another PostgreSQL client

### JWT token errors

1. Ensure JWT_SECRET is at least 256 bits (32 characters)
2. Verify the secret hasn't been changed (would invalidate existing tokens)
