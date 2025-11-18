# Jenkins CI/CD Setup Guide

Complete guide for setting up Jenkins CI/CD pipeline for the Appointment Management System.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Jenkins Installation](#jenkins-installation)
- [Jenkins Configuration](#jenkins-configuration)
- [Pipeline Setup](#pipeline-setup)
- [AWS Configuration](#aws-configuration)
- [Running the Pipeline](#running-the-pipeline)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

- Jenkins 2.400+ (LTS version recommended)
- Java 21
- Maven 3.9+
- Docker (for containerized builds)
- AWS CLI
- Git

### Required Access

- GitHub/GitLab repository access
- AWS account with appropriate permissions
- AWS credentials (Access Key ID and Secret Access Key)

---

## Jenkins Installation

### Option 1: Install on Windows

1. **Download Jenkins**
```bash
# Download from: https://www.jenkins.io/download/
# Choose Windows installer (.msi)
```

2. **Install Jenkins**
- Run the installer
- Choose installation directory
- Select port (default: 8080)
- Install as Windows service

3. **Start Jenkins**
```bash
# Jenkins starts automatically as a service
# Access at: http://localhost:8080
```

4. **Unlock Jenkins**
```bash
# Get initial admin password
type C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword
```

### Option 2: Install on Mac

```bash
# Install using Homebrew
brew install jenkins-lts

# Start Jenkins
brew services start jenkins-lts

# Access at: http://localhost:8080

# Get initial password
cat /Users/Shared/Jenkins/Home/secrets/initialAdminPassword
```

### Option 3: Install on Linux

```bash
# Add Jenkins repository
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key

echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Update and install
sudo apt-get update
sudo apt-get install jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Get initial password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### Option 4: Run with Docker

```bash
# Run Jenkins in Docker
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts-jdk21

# Get initial password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

---

## Jenkins Configuration

### Step 1: Initial Setup

1. **Access Jenkins**
   - Navigate to http://localhost:8080
   - Enter the initial admin password

2. **Install Suggested Plugins**
   - Click "Install suggested plugins"
   - Wait for installation to complete

3. **Create Admin User**
   - Username: admin
   - Password: (choose strong password)
   - Full name: Your Name
   - Email: your-email@example.com

4. **Configure Jenkins URL**
   - Keep default: http://localhost:8080
   - Click "Save and Finish"

### Step 2: Install Required Plugins

Navigate to: **Manage Jenkins → Plugins → Available plugins**

Install the following plugins:

#### Essential Plugins
- [x] **Pipeline** - Pipeline functionality
- [x] **Git** - Git integration
- [x] **Maven Integration** - Maven build support
- [x] **Docker Pipeline** - Docker support
- [x] **AWS Steps** - AWS integration
- [x] **Amazon ECR** - ECR integration
- [x] **CloudBees AWS Credentials** - AWS credentials management

#### Optional but Recommended
- [x] **Blue Ocean** - Modern UI for pipelines
- [x] **Email Extension** - Email notifications
- [x] **Slack Notification** - Slack integration
- [x] **JUnit** - Test result publishing
- [x] **Workspace Cleanup** - Clean workspace after build

**Installation Steps:**
1. Search for each plugin
2. Check the checkbox
3. Click "Install without restart"
4. Wait for installation
5. Restart Jenkins if required

### Step 3: Configure Tools

Navigate to: **Manage Jenkins → Tools**

#### Configure JDK

1. Click "Add JDK"
2. Name: `JDK-21`
3. Uncheck "Install automatically" if you have JDK installed
4. JAVA_HOME: Path to your JDK 21 installation
   - Windows: `C:\Program Files\Java\jdk-21`
   - Mac: `/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home`
   - Linux: `/usr/lib/jvm/java-21-openjdk`

#### Configure Maven

1. Click "Add Maven"
2. Name: `Maven-3.9`
3. Option A - Install automatically:
   - Check "Install automatically"
   - Version: 3.9.9
4. Option B - Use existing:
   - Uncheck "Install automatically"
   - MAVEN_HOME: Path to Maven installation

#### Configure Git

1. Click "Add Git"
2. Name: `Default`
3. Path to Git executable:
   - Windows: `C:\Program Files\Git\bin\git.exe`
   - Mac/Linux: `/usr/bin/git`

#### Configure Docker

1. Click "Add Docker"
2. Name: `Docker`
3. Check "Install automatically" or provide Docker installation path

### Step 4: Configure AWS Credentials

Navigate to: **Manage Jenkins → Credentials → System → Global credentials**

#### Add AWS Credentials

1. Click "Add Credentials"
2. Kind: `AWS Credentials`
3. ID: `aws-credentials`
4. Description: `AWS Credentials for Deployment`
5. Access Key ID: Your AWS Access Key
6. Secret Access Key: Your AWS Secret Key
7. Click "Create"

#### Add AWS Account ID

1. Click "Add Credentials"
2. Kind: `Secret text`
3. Secret: Your AWS Account ID (12-digit number)
4. ID: `aws-account-id`
5. Description: `AWS Account ID`
6. Click "Create"

#### Add GitHub Credentials (if private repo)

1. Click "Add Credentials"
2. Kind: `Username with password`
3. Username: Your GitHub username
4. Password: GitHub Personal Access Token
5. ID: `github-credentials`
6. Description: `GitHub Credentials`
7. Click "Create"

---

## Pipeline Setup

### Step 1: Create Pipeline Job

1. **Create New Item**
   - Click "New Item" on Jenkins dashboard
   - Enter name: `appointment-management-pipeline`
   - Select "Pipeline"
   - Click "OK"

2. **Configure General Settings**
   - Description: `CI/CD Pipeline for Appointment Management System`
   - Check "GitHub project" (if using GitHub)
   - Project URL: Your repository URL

3. **Configure Build Triggers**
   
   Option A - Poll SCM (Check for changes periodically):
   ```
   H/5 * * * *
   ```
   (Checks every 5 minutes)
   
   Option B - GitHub webhook (Recommended):
   - Check "GitHub hook trigger for GITScm polling"
   - Configure webhook in GitHub repository settings

4. **Configure Pipeline**
   
   Option A - Pipeline from SCM:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: Your repository URL
   - Credentials: Select GitHub credentials (if private)
   - Branch: `*/main` or `*/master`
   - Script Path: `Jenkinsfile`
   
   Option B - Pipeline Script:
   - Definition: `Pipeline script`
   - Copy and paste Jenkinsfile content

5. **Save Configuration**

### Step 2: Configure GitHub Webhook (Optional but Recommended)

1. **Go to GitHub Repository**
   - Navigate to your repository
   - Settings → Webhooks → Add webhook

2. **Configure Webhook**
   - Payload URL: `http://your-jenkins-url:8080/github-webhook/`
   - Content type: `application/json`
   - Secret: (optional)
   - Events: `Just the push event`
   - Active: ✓
   - Click "Add webhook"

---

## AWS Configuration

### Step 1: Create ECR Repository

```bash
# Create ECR repository
aws ecr create-repository \
    --repository-name appointment-management \
    --region us-east-1

# Note the repository URI
```

### Step 2: Create ECS Cluster (if using ECS)

```bash
# Create ECS cluster
aws ecs create-cluster \
    --cluster-name appointment-cluster \
    --region us-east-1
```

### Step 3: Create Task Definition

Create `task-definition.json`:

```json
{
  "family": "appointment-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "appointment-app",
      "image": "<account-id>.dkr.ecr.us-east-1.amazonaws.com/appointment-management:latest",
      "portMappings": [
        {
          "containerPort": 9080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/appointment-app",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

Register task definition:
```bash
aws ecs register-task-definition \
    --cli-input-json file://task-definition.json
```

### Step 4: Create ECS Service

```bash
aws ecs create-service \
    --cluster appointment-cluster \
    --service-name appointment-service \
    --task-definition appointment-task \
    --desired-count 1 \
    --launch-type FARGATE \
    --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=ENABLED}"
```

### Step 5: Configure IAM Permissions

Ensure your AWS user/role has these permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:*",
        "ecs:*",
        "elasticbeanstalk:*",
        "s3:*",
        "logs:*"
      ],
      "Resource": "*"
    }
  ]
}
```

---

## Running the Pipeline

### Manual Build

1. Go to Jenkins dashboard
2. Click on your pipeline job
3. Click "Build Now"
4. Watch the build progress in "Build History"
5. Click on build number to see details
6. View "Console Output" for logs

### Automatic Build (with webhook)

1. Make changes to your code
2. Commit and push to GitHub
```bash
git add .
git commit -m "Update feature"
git push origin main
```
3. Jenkins automatically triggers build
4. Monitor progress on Jenkins dashboard

### Pipeline Stages

The pipeline executes these stages:

1. **Checkout** - Clone repository
2. **Build** - Compile Java code
3. **Test** - Run unit tests
4. **Package** - Create JAR file
5. **Code Quality** - Run quality checks
6. **Build Docker Image** - Create Docker image
7. **Push to ECR** - Upload to AWS ECR
8. **Deploy to ECS** - Deploy to AWS ECS
9. **Health Check** - Verify deployment

---

## Monitoring and Logs

### View Build Logs

1. Click on build number
2. Click "Console Output"
3. View real-time logs

### View Test Results

1. Click on build number
2. Click "Test Results"
3. View passed/failed tests

### View Artifacts

1. Click on build number
2. Click "Build Artifacts"
3. Download JAR file

### Blue Ocean View (Modern UI)

1. Install Blue Ocean plugin
2. Click "Open Blue Ocean" on dashboard
3. View visual pipeline

---

## Troubleshooting

### Build Fails at Checkout Stage

**Problem:** Cannot access repository

**Solution:**
```bash
# Check credentials
# Verify repository URL
# Ensure Jenkins has network access
```

### Build Fails at Build Stage

**Problem:** Maven build fails

**Solution:**
```bash
# Check Java version
# Verify Maven configuration
# Check pom.xml for errors
# Clear Maven cache: mvn clean
```

### Build Fails at Docker Stage

**Problem:** Docker not available

**Solution:**
```bash
# Ensure Docker is installed
# Check Docker daemon is running
# Verify Jenkins user has Docker permissions
# Linux: sudo usermod -aG docker jenkins
```

### Build Fails at AWS Stage

**Problem:** AWS credentials invalid

**Solution:**
```bash
# Verify AWS credentials in Jenkins
# Check IAM permissions
# Test AWS CLI: aws sts get-caller-identity
```

### Pipeline Hangs

**Problem:** Build stuck at a stage

**Solution:**
```bash
# Check Jenkins executor availability
# Increase timeout in Jenkinsfile
# Check for resource constraints
# Restart Jenkins if necessary
```

---

## Best Practices

### Security

- [ ] Use credentials plugin for sensitive data
- [ ] Never hardcode passwords in Jenkinsfile
- [ ] Rotate AWS credentials regularly
- [ ] Use least privilege IAM policies
- [ ] Enable Jenkins security features

### Performance

- [ ] Use parallel stages when possible
- [ ] Cache Maven dependencies
- [ ] Clean workspace after builds
- [ ] Limit build history retention
- [ ] Use Jenkins agents for distributed builds

### Maintenance

- [ ] Regular Jenkins updates
- [ ] Plugin updates
- [ ] Backup Jenkins configuration
- [ ] Monitor disk space
- [ ] Review build logs regularly

---

## Additional Resources

### Jenkins Documentation
- Official Docs: https://www.jenkins.io/doc/
- Pipeline Syntax: https://www.jenkins.io/doc/book/pipeline/syntax/
- Plugin Index: https://plugins.jenkins.io/

### AWS Documentation
- ECR: https://docs.aws.amazon.com/ecr/
- ECS: https://docs.aws.amazon.com/ecs/
- Elastic Beanstalk: https://docs.aws.amazon.com/elasticbeanstalk/

### Tutorials
- Jenkins Pipeline Tutorial: https://www.jenkins.io/doc/tutorials/
- AWS DevOps: https://aws.amazon.com/devops/

---

## Support

For issues:
1. Check Jenkins logs: `Manage Jenkins → System Log`
2. Review build console output
3. Check AWS CloudWatch logs
4. Contact team members

---

**Last Updated:** November 2025  
**Maintained by:** Ishita Parkar, Kirshnandu Gurey, Prutha Jadhav, and Yash Adhau
