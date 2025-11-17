# Simple GitHub Deployment Guide

## Step-by-Step Instructions (No SSH/Token Required Initially)

### Step 1: Create GitHub Repository (5 minutes)

1. Open your web browser and go to: **https://github.com**

2. Log in with username: **Nimay-Jadhav**

3. Click the **"+"** button in the top-right corner

4. Click **"New repository"**

5. Fill in the details:
   - Repository name: **course-management-api**
   - Description: **RESTful API for course management built with Spring Boot**
   - Choose: **Public** (or Private if you prefer)
   - **IMPORTANT**: Leave all checkboxes UNCHECKED:
     - ❌ Do NOT check "Add a README file"
     - ❌ Do NOT check "Add .gitignore"
     - ❌ Do NOT check "Choose a license"

6. Click **"Create repository"**

### Step 2: Install GitHub Desktop (Easiest Method - No Token Needed!)

This is the simplest way to push your code without dealing with tokens or SSH keys.

1. Download GitHub Desktop:
   - Go to: **https://desktop.github.com/**
   - Click "Download for Windows"
   - Install the application

2. Open GitHub Desktop and sign in:
   - Click "Sign in to GitHub.com"
   - Enter your GitHub credentials (Nimay-Jadhav)
   - Authorize GitHub Desktop

3. Add your existing repository:
   - Click "File" → "Add local repository"
   - Click "Choose..." and navigate to: **C:\Users\Shubh\omkar cc project**
   - Click "Add repository"

4. Publish to GitHub:
   - Click "Publish repository" button
   - Repository name: **course-management-api**
   - Uncheck "Keep this code private" (if you want it public)
   - Click "Publish repository"

**Done!** Your code is now on GitHub at:
```
https://github.com/Nimay-Jadhav/course-management-api
```

---

## Alternative: Use Git Credential Manager (Command Line)

If you prefer using the command line without GitHub Desktop:

### Step 1: Install Git Credential Manager

Git Credential Manager should already be installed with Git for Windows. Let's verify:

```bash
git credential-manager --version
```

If not installed, download from: https://github.com/git-ecosystem/git-credential-manager/releases

### Step 2: Configure Git to Use Credential Manager

```bash
git config --global credential.helper manager
```

### Step 3: Push to GitHub

```bash
git push -u origin main
```

When prompted:
- A browser window will open automatically
- Sign in to GitHub with username: **Nimay-Jadhav**
- Authorize the application
- The push will complete automatically

---

## Verify Your Deployment

After deployment, visit:
```
https://github.com/Nimay-Jadhav/course-management-api
```

You should see:
- ✅ README.md with full documentation
- ✅ All source code files
- ✅ Frontend files (HTML, CSS, JS)
- ✅ Maven configuration (pom.xml)

---

## What's Already Done

Your project is ready to push:
- ✅ Git initialized
- ✅ All files committed
- ✅ Remote configured
- ✅ Professional README created

You just need to authenticate once using either method above!

---

## Recommended: GitHub Desktop Method

**Why GitHub Desktop is easiest:**
- ✅ No command line needed
- ✅ No tokens or SSH keys to manage
- ✅ Visual interface
- ✅ One-click authentication
- ✅ Automatic credential management
- ✅ Easy to use for future updates

**Time required:** 5-10 minutes total
