# 🚀 Quick Setup Guide

## For New Developers

Follow these steps to set up the Bug Tracker System locally:

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/bug-tracker.git
cd bug-tracker
```

---

## Step 2: Create Environment File

```bash
# Copy the example file
cp .env.example .env

# Edit with your database credentials
# Open .env and update:
# - DB_HOST: Your AWS RDS endpoint
# - DB_PORT: 3306 (usually)
# - DB_NAME: Your database name
# - DB_USER: Your database username
# - DB_PASSWORD: Your secure password
```

**Example .env file:**
```
DB_HOST=your-rds-instance.region.rds.amazonaws.com
DB_PORT=3306
DB_NAME=bug_tracker
DB_USER=admin
DB_PASSWORD=your_secure_password_here
SERVER_PORT=8080
LOG_LEVEL=DEBUG
```

---

## Step 3: Install Prerequisites

### Java 21+
```bash
# Check if Java is installed
java -version

# If not installed, download from:
# https://adoptium.net/download/
```

### Maven (Optional but Recommended)

**Option A: Download and Install**
```powershell
# Download Maven
$mavenVersion = "3.9.6"
$url = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
Invoke-WebRequest -Uri $url -OutFile "maven.zip"

# Extract
Expand-Archive -Path "maven.zip" -DestinationPath "C:\"

# Add to PATH
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\apache-maven-$mavenVersion\bin", [EnvironmentVariableTarget]::User)
```

**Option B: Use Pre-built JAR**
- No Maven needed
- Just run the JAR file directly

---

## Step 4: Run the Application

### Method A: Using Maven (Recommended for Development)

```bash
# Load environment variables
$env:JAVA_HOME = "C:\java\jdk-21.0.1+12"  # Adjust path as needed
$env:Path = "C:\maven\bin;$env:Path"

# Run application
mvn spring-boot:run
```

**Advantages:**
- Hot-reload enabled (changes apply automatically)
- Spring DevTools active
- Development optimized

### Method B: Using Pre-built JAR

```bash
java -jar target/bug-tracker-1.0.0.jar
```

**Advantages:**
- No build step needed
- Fast startup
- No Maven required

### Method C: Using IDE

1. Open project in IntelliJ IDEA or Eclipse
2. Wait for Maven dependencies to download
3. Right-click `BugTrackerApplication.java`
4. Click "Run"

---

## Step 5: Access the Application

Open your browser:

| Feature | URL |
|---------|-----|
| Dashboard | http://localhost:8080/bugs |
| Report Bug | http://localhost:8080/raise-bug |
| API | http://localhost:8080/api/bugs |
| Home | http://localhost:8080 |

---

## Troubleshooting

### Port 8080 Already in Use
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID)
taskkill /PID <PID> /F

# OR change port in .env:
SERVER_PORT=8081
```

### Cannot Connect to Database
```bash
# Test connection
mysql -h <DB_HOST> -u <DB_USER> -p<DB_PASSWORD> -e "SELECT 1;"

# Check RDS security groups allow your IP
# Verify credentials in .env file
```

### Maven Command Not Found
```bash
# Set JAVA_HOME
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\java\jdk-21.0.1+12", [EnvironmentVariableTarget]::User)

# Add Maven to PATH
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\maven\bin", [EnvironmentVariableTarget]::User)

# Restart terminal
```

### Application Starts but Won't Load Web Pages
```bash
# Check if application is actually running
curl http://localhost:8080/api/bugs

# Check logs for errors
# Review console output for exception messages
```

---

## Common Tasks

### View All Bugs via API
```bash
curl http://localhost:8080/api/bugs
```

### Create New Bug
```bash
curl -X POST http://localhost:8080/api/bugs \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Login button not working",
    "priority": "HIGH",
    "severity": "CRITICAL",
    "detectedOn": "2025-11-19",
    "assignedTo": "John Doe"
  }'
```

### Filter by Status
```bash
curl http://localhost:8080/api/bugs/status/OPEN
curl http://localhost:8080/api/bugs/status/IN_PROGRESS
curl http://localhost:8080/api/bugs/status/FIXED
```

### Build Project
```bash
mvn clean package -DskipTests
```

### Run Tests
```bash
mvn test
```

### Check Dependencies
```bash
mvn dependency:tree
```

---

## Project Structure

```
bug-tracker/
├── .env                 # Local config (NOT in Git)
├── .env.example         # Template (safe to commit)
├── src/
│   └── main/
│       ├── java/        # Java source code
│       └── resources/   # Templates, CSS, SQL
├── target/              # Build output (JAR file)
├── pom.xml              # Maven configuration
├── README.md            # Project documentation
└── SECURITY_CHECKLIST   # Security guidelines
```

---

## Next Steps

1. **Explore the Dashboard**
   - View real-time statistics
   - Report new bugs
   - Update bug status

2. **Try the API**
   - GET all bugs
   - POST new bugs
   - Filter by status

3. **Review Code**
   - Examine `BugController.java` (Routes)
   - Check `BugService.java` (Business logic)
   - Study `BugRepository.java` (Database)

4. **Make Changes**
   - Maven enables hot-reload
   - Edit code → Save → Automatically reloads
   - No need to restart server

5. **Deploy to Production**
   - See ARCHITECTURE.md for deployment options
   - Use environment variables for secrets
   - Follow SECURITY_CHECKLIST.md

---

## Support

- **Documentation:** See README.md
- **Architecture:** See ARCHITECTURE.md
- **Security:** See SECURITY_CHECKLIST.md
- **Contributing:** See CONTRIBUTING.md
- **License:** See LICENSE

---

**Ready to start developing? Happy coding! 🚀**
