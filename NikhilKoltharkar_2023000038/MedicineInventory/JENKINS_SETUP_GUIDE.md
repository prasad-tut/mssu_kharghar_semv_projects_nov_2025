# Jenkins CI/CD Setup Guide
## Medicine Inventory Management System

This guide explains how to set up Jenkins for automated builds of the Medicine Inventory project.

---

## What is Jenkins?

Jenkins is a CI/CD (Continuous Integration/Continuous Deployment) tool that:
- Automatically compiles your code
- Detects compilation errors
- Tracks build history
- Provides build status reports

**Important:** Jenkins is a **separate tool** - it's not part of the application code. Think of it like installing Microsoft Word or MySQL - it's software you install to help with development.

---

## Jenkins Installation

### Step 1: Download Jenkins

1. Visit: https://www.jenkins.io/download/
2. Download **jenkins.war** file (under "Generic Java package (.war)")
3. Save it anywhere (recommended: create a folder `C:\Jenkins`)

**Note:** The jenkins.war file is ~90MB

---

### Step 2: Start Jenkins

1. Open Command Prompt
2. Navigate to where you saved jenkins.war:
```bash
   cd C:\Jenkins
```
3. Run Jenkins:
```bash
   java -jar jenkins.war
```
4. Wait for this message (takes 1-2 minutes):
```
   Jenkins is fully up and running
```

**Important:** Keep this Command Prompt window open! Jenkins is a server that runs continuously. When you close the window, Jenkins stops.

---

### Step 3: Access Jenkins Dashboard

1. Open browser
2. Go to: **http://localhost:8080**
3. You'll see "Unlock Jenkins" page

---

### Step 4: Initial Setup

1. **Get Password:**
   - Open: `C:\Users\YourUsername\.jenkins\secrets\initialAdminPassword`
   - Copy the long password

2. **Paste password** in Jenkins and click Continue

3. **Install Plugins:**
   - Click "Install suggested plugins"
   - Wait 3-5 minutes for installation

4. **Create Admin User:**
   - Username: `admin`
   - Password: `admin` (or your choice)
   - Full Name: Your name
   - Email: Your email
   - Click "Save and Continue"

5. **Jenkins URL:**
   - Keep default: `http://localhost:8080/`
   - Click "Save and Finish"

6. **Click "Start using Jenkins"**

---

## Creating a Build Job

### Step 1: Create New Job

1. Click **"New Item"** (top left)
2. Enter name: **MedicineInventorySystem**
3. Select **"Freestyle project"**
4. Click **OK**

---

### Step 2: Configure Build

1. In the configuration page, scroll to **"Build Steps"**

2. Click **"Add build step"** → **"Execute Windows batch command"**

3. Paste this script:
```batch
@echo off
echo ========================================
echo Building Medicine Inventory System
echo ========================================

REM Change this path to where YOUR project is located
cd /d "C:\Users\YourUsername\Downloads\MedicineInventory"

REM Clean previous compiled files
if exist src\*.class del src\*.class

REM Compile the project
echo Compiling Java files...
javac -cp ".;lib/*" src/*.java

REM Check if compilation succeeded
if %ERRORLEVEL% EQU 0 (
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
) else (
    echo ========================================
    echo BUILD FAILED!
    echo ========================================
    exit /b 1
)
```

4. **IMPORTANT:** Update the `cd /d` line with YOUR actual project path!

5. Click **Save**

---

### Step 3: Run Your First Build

1. Click **"Build Now"** (left sidebar)
2. Watch "Build History" (bottom left)
3. Build #1 appears with a progress bar
4. Wait for completion:
   - ✅ **Green/Blue ball** = Success!
   - ❌ **Red ball** = Failed

5. Click on **#1** → **"Console Output"** to see build log

---

## Using Jenkins

### Starting Jenkins (Every Time)

**You must start Jenkins each time you want to use it:**
```bash
cd C:\Jenkins
java -jar jenkins.war
```

Keep the Command Prompt open while using Jenkins.

---

### Running a Build

1. Go to: http://localhost:8080
2. Click on "MedicineInventorySystem"
3. Click "Build Now"
4. View results in Build History

---

### Viewing Build Logs

1. Click on build number (e.g., #1, #2)
2. Click "Console Output"
3. See detailed compilation log

---

## Port Configuration

**Jenkins and the application use different ports:**

| Service | Port | URL |
|---------|------|-----|
| Jenkins Dashboard | 8080 | http://localhost:8080 |
| Medicine Inventory App | 8081 | http://localhost:8081 |

**Why?**
- Two applications can't use the same port
- Jenkins (8080) is for CI/CD builds
- Application (8081) is the actual website

---

## Understanding the Workflow

### Traditional Way (Without Jenkins):
```
1. Write code
2. Open Command Prompt
3. Type: javac -cp ".;lib/*" src/*.java
4. Check for errors
5. Fix errors if any
6. Repeat steps 2-5
```

### With Jenkins:
```
1. Write code
2. Push to Git (or just save locally)
3. Click "Build Now" in Jenkins
4. Jenkins compiles automatically
5. See green checkmark (success) or red X (failure)
6. View detailed log if needed
```

---

## Troubleshooting

### Problem: "Jenkins won't start"
**Solution:**
- Ensure Java is installed: `java -version`
- Check if port 8080 is free
- Run Command Prompt as Administrator

### Problem: "Connection refused" when accessing Jenkins
**Solution:**
- Jenkins server is not running
- Start it: `java -jar jenkins.war`
- Wait for "fully up and running" message

### Problem: Build fails (red ball)
**Solution:**
- Click build number → "Console Output"
- Read the error message
- Common issues:
  - Wrong project path in build script
  - Missing JAR files in lib/
  - Java syntax errors in code

### Problem: "Port 8080 already in use"
**Solution:**
- Another application is using port 8080
- Stop that application
- OR run Jenkins on different port:
```bash
  java -jar jenkins.war --httpPort=9090
```

---

## Benefits of Using Jenkins

1. **Automated Compilation**
   - No need to manually type javac commands
   - One-click builds

2. **Immediate Feedback**
   - Instantly see if code compiles
   - Green = good, Red = fix needed

3. **Build History**
   - Track all builds over time
   - See when things broke

4. **Professional Practice**
   - Industry-standard CI/CD tool
   - Used in real software companies

5. **Error Detection**
   - Catches compilation errors immediately
   - Detailed console output for debugging

---

## What Jenkins Does NOT Do

Jenkins is for **building/compiling** code, not running the application:

- ❌ Jenkins does NOT run your application server
- ❌ Jenkins does NOT open your website
- ❌ Jenkins does NOT replace `java -cp ...` command for running

You still need to:
1. Build with Jenkins (compiles code)
2. Run application manually: `java -cp ".;lib/*;src" RestAPIServer`
3. Open browser to http://localhost:8081

---

## Advanced: GitHub Integration (Optional)

Jenkins can automatically build when you push to GitHub:

1. In Jenkins job configuration, go to "Source Code Management"
2. Select "Git"
3. Enter repository URL
4. Under "Build Triggers", select "Poll SCM"
5. Set schedule (e.g., `H/5 * * * *` = every 5 minutes)

Now Jenkins will:
- Check GitHub for new changes
- Automatically build if changes detected
- Report build status

---

## Summary

**What you learned:**
- Jenkins is a separate tool (jenkins.war) that you install
- It automates the compilation process
- It must be running to use it (like a server)
- It uses port 8080 (different from your app's 8081)
- It provides build history and status tracking

**Key takeaway:** Jenkins is optional but makes you look professional. Your application works fine without it, but Jenkins adds automation and demonstrates modern DevOps practices.

---

## Quick Reference

**Start Jenkins:**
```bash
cd C:\Jenkins
java -jar jenkins.war
```

**Access Jenkins:**
```
http://localhost:8080
```

**Build Project:**
```
Click "Build Now" in Jenkins dashboard
```

**Stop Jenkins:**
```
Press Ctrl+C in Command Prompt
```

---

For questions about the main application (not Jenkins), see README.md
```

---

## So Here's Your Final Answer:

### What to Do:

1. ✅ **Use SHORT README.md** (above) - Easy for anyone to run the app
2. ✅ **Create JENKINS_SETUP_GUIDE.md** (above) - Detailed Jenkins instructions
3. ✅ **Keep PROJECT_DOCUMENTATION.txt** - Your existing complete docs

### What Goes to GitHub:
```
MedicineInventory/
├── lib/                           ✅ Upload
├── src/                           ✅ Upload
├── frontend/                      ✅ Upload
├── database_setup.sql             ✅ Upload
├── README.md                      ✅ Upload (SHORT version)
├── JENKINS_SETUP_GUIDE.md         ✅ Upload (DETAILED Jenkins)
├── PROJECT_DOCUMENTATION.txt      ✅ Upload (Complete docs)
├── .gitignore                     ✅ Upload
└── (NO jenkins.war or .jenkins/)  ❌ DON'T Upload