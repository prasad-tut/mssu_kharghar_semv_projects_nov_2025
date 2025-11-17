# First Time Setup Guide

Welcome! Follow these steps to get the Bill Management System running on your machine.

## Prerequisites

Install these before starting:

- **Java 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **PostgreSQL 15 or higher** - [Download](https://www.postgresql.org/download/)
- **Docker Desktop** (optional but recommended) - [Download](https://www.docker.com/products/docker-desktop/)

## Quick Setup (5 minutes)

### Step 1: Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/Bill-Management.git
cd Bill-Management
```

### Step 2: Configure Environment

```bash
# Copy the example environment file
cp .env.example .env

# Open .env and update these values:
# - DB_PASSWORD=your_postgres_password
# - Other values can stay as defaults
```

### Step 3: Generate Frontend Config

```bash
# Windows
generate-frontend-config.bat

# Mac/Linux
chmod +x generate-frontend-config.sh
./generate-frontend-config.sh
```

### Step 4: Start Application

**Option A: Using Docker (Easiest)**
```bash
docker-compose up -d
```

**Option B: Manual Setup**
```bash
# Terminal 1: Start database
docker-compose -f docker-compose.dev.yml up postgres

# Terminal 2: Start backend
cd bill_management
mvn spring-boot:run

# Terminal 3: Open frontend
# Double-click frontend/index.html
```

### Step 5: Access Application

- **Frontend**: http://localhost (Docker) or open `frontend/index.html`
- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:7890/actuator/health

## Verify Installation

Test the API:
```bash
curl http://localhost:7890/bills/get
```

You should see an empty array `[]` or list of bills.

## Troubleshooting

### "Database connection failed"
- Check PostgreSQL is running
- Verify `DB_PASSWORD` in `.env` matches your PostgreSQL password
- Test connection: `psql -U postgres -c "SELECT 1;"`

### "Port already in use"
- Change `BACKEND_PORT` in `.env` to a different port (e.g., 8090)
- Regenerate frontend config
- Restart application

### "Frontend can't connect to backend"
- Verify backend is running: `curl http://localhost:7890/actuator/health`
- Regenerate frontend config: `./generate-frontend-config.sh`
- Clear browser cache (Ctrl+Shift+Delete)

## Next Steps

1. Read [README.md](README.md) for full documentation
2. Check [QUICK-REFERENCE.md](QUICK-REFERENCE.md) for common commands
3. See [ENV-SETUP.md](ENV-SETUP.md) for advanced configuration

## Need Help?

- Check [DOCS-INDEX.md](DOCS-INDEX.md) for all documentation
- Open an issue on GitHub
- Check existing issues for solutions

## Success!

If you see the dashboard with statistics, you're all set! 🎉

Start adding bills and exploring the features.
