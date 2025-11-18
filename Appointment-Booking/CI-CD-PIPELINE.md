# CI/CD Pipeline Architecture

Complete CI/CD pipeline architecture for the Appointment Management System using Jenkins.

---

## Pipeline Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Developer Workflow                           │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                                  │ git push
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          GitHub Repository                           │
│                     (Source Code Management)                         │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                                  │ webhook trigger
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         Jenkins Pipeline                             │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 1: Checkout                                            │   │
│  │ - Clone repository from GitHub                               │   │
│  │ - Checkout specific branch                                   │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 2: Build                                               │   │
│  │ - mvn clean compile                                          │   │
│  │ - Compile Java source code                                   │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 3: Test                                                │   │
│  │ - mvn test                                                   │   │
│  │ - Run unit tests                                             │   │
│  │ - Generate test reports                                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 4: Package                                             │   │
│  │ - mvn package                                                │   │
│  │ - Create JAR file                                            │   │
│  │ - Archive artifacts                                          │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 5: Code Quality                                        │   │
│  │ - mvn verify                                                 │   │
│  │ - Run quality checks                                         │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 6: Build Docker Image                                  │   │
│  │ - docker build                                               │   │
│  │ - Create container image                                     │   │
│  │ - Tag with build number                                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 7: Push to ECR                                         │   │
│  │ - AWS ECR login                                              │   │
│  │ - docker push                                                │   │
│  │ - Upload image to registry                                   │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 8: Deploy to ECS                                       │   │
│  │ - Update ECS service                                         │   │
│  │ - Force new deployment                                       │   │
│  │ - Pull latest image                                          │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                  │                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Stage 9: Health Check                                        │   │
│  │ - Wait for deployment                                        │   │
│  │ - Verify application health                                  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
                    ▼                           ▼
        ┌───────────────────────┐   ┌───────────────────────┐
        │   AWS ECR             │   │   AWS ECS             │
        │   (Container Registry)│   │   (Container Service) │
        └───────────────────────┘   └───────────────────────┘
                                                │
                                                ▼
                                    ┌───────────────────────┐
                                    │   Running Application │
                                    │   (Port 9080)         │
                                    └───────────────────────┘
                                                │
                                                ▼
                                    ┌───────────────────────┐
                                    │   AWS RDS MySQL       │
                                    │   (Database)          │
                                    └───────────────────────┘
```

---

## Pipeline Stages Detailed

### Stage 1: Checkout
**Purpose:** Get the latest code from repository

**Actions:**
- Clone repository from GitHub
- Checkout specified branch (main/master)
- Display commit information

**Tools:** Git

**Duration:** ~10 seconds

---

### Stage 2: Build
**Purpose:** Compile the Java application

**Actions:**
- Clean previous builds
- Compile source code
- Resolve dependencies

**Command:** `mvn clean compile`

**Tools:** Maven, JDK 21

**Duration:** ~30-60 seconds

---

### Stage 3: Test
**Purpose:** Run automated tests

**Actions:**
- Execute unit tests
- Generate test reports
- Publish JUnit results

**Command:** `mvn test`

**Tools:** Maven, JUnit

**Duration:** ~20-40 seconds

**Artifacts:** Test reports (XML)

---

### Stage 4: Package
**Purpose:** Create deployable artifact

**Actions:**
- Package application as JAR
- Archive artifacts
- Skip tests (already run)

**Command:** `mvn package -DskipTests`

**Tools:** Maven

**Duration:** ~30 seconds

**Artifacts:** `restapi_app-0.0.1-SNAPSHOT.jar`

---

### Stage 5: Code Quality
**Purpose:** Ensure code quality standards

**Actions:**
- Run verification checks
- Check code coverage
- Validate dependencies

**Command:** `mvn verify`

**Tools:** Maven

**Duration:** ~20 seconds

---

### Stage 6: Build Docker Image
**Purpose:** Containerize the application

**Actions:**
- Build Docker image from Dockerfile
- Tag with build number
- Tag as latest

**Command:** `docker build`

**Tools:** Docker

**Duration:** ~60-120 seconds

**Output:** Docker image

---

### Stage 7: Push to ECR
**Purpose:** Upload image to AWS container registry

**Actions:**
- Authenticate with AWS ECR
- Push image with build tag
- Push image with latest tag

**Command:** `docker push`

**Tools:** Docker, AWS CLI

**Duration:** ~60-180 seconds (depends on image size)

**Output:** Image in ECR

---

### Stage 8: Deploy to ECS
**Purpose:** Deploy application to AWS

**Actions:**
- Update ECS service
- Force new deployment
- Pull latest image from ECR

**Command:** `aws ecs update-service`

**Tools:** AWS CLI

**Duration:** ~30 seconds (deployment takes 2-5 minutes)

---

### Stage 9: Health Check
**Purpose:** Verify deployment success

**Actions:**
- Wait for service to stabilize
- Check application health endpoint
- Verify database connectivity

**Duration:** ~30-60 seconds

---

## Pipeline Triggers

### Automatic Triggers

1. **GitHub Webhook**
   - Triggered on: `git push` to main branch
   - Fastest response time
   - Recommended for production

2. **SCM Polling**
   - Checks repository every 5 minutes
   - Fallback if webhooks fail
   - Schedule: `H/5 * * * *`

3. **Scheduled Builds**
   - Nightly builds
   - Weekly full builds
   - Schedule: `H 2 * * *` (2 AM daily)

### Manual Triggers

1. **Build Now** button in Jenkins
2. **Parameterized builds** with custom options
3. **API trigger** via Jenkins REST API

---

## Environment Variables

### Jenkins Environment Variables

```groovy
APP_NAME = 'appointment-management'
AWS_REGION = 'us-east-1'
AWS_ACCOUNT_ID = credentials('aws-account-id')
ECR_REPO_NAME = 'appointment-management'
DOCKER_IMAGE = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}"
BUILD_VERSION = "${env.BUILD_NUMBER}"
```

### Application Environment Variables

```properties
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://...
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=<from-secrets-manager>
SERVER_PORT=9080
```

---

## Credentials Management

### Required Credentials in Jenkins

1. **aws-credentials**
   - Type: AWS Credentials
   - Access Key ID
   - Secret Access Key

2. **aws-account-id**
   - Type: Secret text
   - 12-digit AWS account ID

3. **github-credentials** (if private repo)
   - Type: Username with password
   - GitHub username
   - Personal access token

---

## Notifications

### Success Notifications

- Email to team
- Slack message
- Build status badge update

### Failure Notifications

- Email alert to team
- Slack alert with error details
- Create JIRA ticket (optional)

---

## Rollback Strategy

### Automatic Rollback

If health check fails:
1. Revert to previous ECS task definition
2. Notify team
3. Mark build as failed

### Manual Rollback

```bash
# List previous builds
aws ecs list-task-definitions --family appointment-task

# Update to previous version
aws ecs update-service \
    --cluster appointment-cluster \
    --service appointment-service \
    --task-definition appointment-task:PREVIOUS_VERSION
```

---

## Monitoring

### Build Metrics

- Build success rate
- Average build duration
- Test pass rate
- Code coverage percentage

### Deployment Metrics

- Deployment frequency
- Lead time for changes
- Mean time to recovery
- Change failure rate

### Application Metrics

- Response time
- Error rate
- Request count
- Database connections

---

## Best Practices

### Pipeline Optimization

✅ Cache Maven dependencies
✅ Run tests in parallel
✅ Use Docker layer caching
✅ Minimize image size
✅ Clean workspace after build

### Security

✅ Use credentials plugin
✅ Scan Docker images for vulnerabilities
✅ Rotate AWS credentials regularly
✅ Use least privilege IAM policies
✅ Enable audit logging

### Reliability

✅ Implement retry logic
✅ Set appropriate timeouts
✅ Monitor pipeline health
✅ Maintain backup deployment method
✅ Test rollback procedures

---

## Pipeline Execution Time

| Stage | Duration | Percentage |
|-------|----------|------------|
| Checkout | 10s | 2% |
| Build | 45s | 9% |
| Test | 30s | 6% |
| Package | 30s | 6% |
| Code Quality | 20s | 4% |
| Build Docker | 90s | 18% |
| Push to ECR | 120s | 24% |
| Deploy to ECS | 30s | 6% |
| Health Check | 45s | 9% |
| **Total** | **~8 minutes** | **100%** |

*Note: Times are approximate and vary based on code changes and network speed*

---

## Troubleshooting

### Build Fails at Test Stage

**Cause:** Test failures

**Solution:**
- Review test logs
- Fix failing tests
- Run tests locally first

### Build Fails at Docker Stage

**Cause:** Docker daemon not available

**Solution:**
- Ensure Docker is running
- Check Jenkins user permissions
- Verify Dockerfile syntax

### Build Fails at Push Stage

**Cause:** AWS authentication failure

**Solution:**
- Verify AWS credentials
- Check IAM permissions
- Test AWS CLI manually

### Deployment Fails

**Cause:** ECS service issues

**Solution:**
- Check ECS service logs
- Verify task definition
- Check security groups
- Verify RDS connectivity

---

## Future Enhancements

- [ ] Add integration tests
- [ ] Implement blue-green deployment
- [ ] Add performance testing stage
- [ ] Implement canary deployments
- [ ] Add security scanning (SAST/DAST)
- [ ] Integrate with SonarQube
- [ ] Add automated rollback
- [ ] Implement feature flags
- [ ] Add smoke tests
- [ ] Integrate with monitoring tools

---

**Last Updated:** November 2025  
**Maintained by:** Ishita Parkar, Kirshnandu Gurey, Prutha Jadhav, and Yash Adhau
