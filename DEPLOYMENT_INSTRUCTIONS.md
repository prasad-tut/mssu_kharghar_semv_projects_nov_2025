# GitHub Deployment Instructions

Your Course Management API project is ready to be deployed to GitHub! Follow these steps:

## Step 1: Create GitHub Repository

1. Go to [GitHub](https://github.com) and log in with username: **Nimay-Jadhav**
2. Click the "+" icon in the top right corner
3. Select "New repository"
4. Repository name: **course-management-api**
5. Description: "RESTful API for course management built with Spring Boot"
6. Choose "Public" or "Private" as per your preference
7. **DO NOT** initialize with README, .gitignore, or license (we already have these)
8. Click "Create repository"

## Step 2: Push Your Code

Your local Git repository is already initialized and committed. Now you need to push it to GitHub.

### Option A: Using HTTPS (Requires Personal Access Token)

1. Create a Personal Access Token:
   - Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Click "Generate new token (classic)"
   - Give it a name like "course-management-api"
   - Select scopes: `repo` (full control of private repositories)
   - Click "Generate token"
   - **Copy the token immediately** (you won't see it again!)

2. Push to GitHub:
```bash
git push -u origin main
```
   - Username: Nimay-Jadhav
   - Password: [paste your personal access token]

### Option B: Using SSH (Recommended)

1. Generate SSH key (if you don't have one):
```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```

2. Add SSH key to GitHub:
   - Copy your public key:
```bash
type %USERPROFILE%\.ssh\id_ed25519.pub
```
   - Go to GitHub Settings → SSH and GPG keys → New SSH key
   - Paste your key and save

3. Change remote URL to SSH:
```bash
git remote set-url origin git@github.com:Nimay-Jadhav/course-management-api.git
```

4. Push to GitHub:
```bash
git push -u origin main
```

## Step 3: Verify Deployment

Once pushed, visit:
```
https://github.com/Nimay-Jadhav/course-management-api
```

You should see all your project files including:
- README.md with project documentation
- Source code in src/ directory
- pom.xml for Maven configuration
- Frontend files in src/main/resources/static/

## Current Git Status

✅ Git repository initialized
✅ All files committed
✅ Branch renamed to 'main'
✅ Remote origin configured
⏳ Waiting for authentication to push

## Quick Commands Reference

```bash
# Check current status
git status

# View commit history
git log --oneline

# View remote configuration
git remote -v

# Push to GitHub (after authentication setup)
git push -u origin main
```

## Need Help?

If you encounter any issues:
1. Make sure you're logged into GitHub with username: Nimay-Jadhav
2. Ensure you have the correct permissions for the repository
3. Verify your authentication method (token or SSH key) is properly configured

## Next Steps After Deployment

1. Add repository topics on GitHub: `spring-boot`, `rest-api`, `java`, `course-management`
2. Enable GitHub Pages if you want to host documentation
3. Set up GitHub Actions for CI/CD (optional)
4. Add collaborators if working in a team
