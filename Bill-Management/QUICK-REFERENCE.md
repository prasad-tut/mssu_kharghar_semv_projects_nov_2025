# Quick Reference

## Setup (First Time)

```bash
cp .env.example .env                    # Copy environment file
# Edit .env - update DB_PASSWORD
./generate-frontend-config.bat         # Windows
./generate-frontend-config.sh          # Mac/Linux
```

## Start/Stop

```bash
docker-compose up -d                    # Start all services
docker-compose down                     # Stop all services
docker-compose logs -f backend          # View logs
docker-compose restart backend          # Restart service
```

## URLs

- Frontend: http://localhost
- Backend: http://localhost:8080
- Health: http://localhost:7890/actuator/health
- pgAdmin: http://localhost:5050 (dev mode)

## Common Commands

```bash
# Database
psql -U postgres -d billmanagement
docker exec -it bill-management-db psql -U postgres -d billmanagement

# Test API
curl http://localhost:7890/bills/get
curl http://localhost:7890/actuator/health

# Rebuild
docker-compose up -d --build backend

# Development (manual)
docker-compose -f docker-compose.dev.yml up postgres
cd bill_management && mvn spring-boot:run
```

## Environment Variables (.env)

```env
DB_PASSWORD=your_password              # Required
API_BASE_URL=http://localhost:7890    # Required
BACKEND_PORT=7890                      # Optional
SPRING_PROFILE=dev                     # Optional
```

## Troubleshooting

| Issue | Fix |
|-------|-----|
| Backend won't start | Check `DB_PASSWORD` in `.env` |
| Frontend can't connect | Run `generate-frontend-config` script |
| Port in use | Change `BACKEND_PORT` in `.env` |
| Database error | Verify PostgreSQL is running |

## Files

- `.env` - Your config (don't commit)
- `.env.example` - Template
- `docker-compose.yml` - Production
- `docker-compose.dev.yml` - Development
- `frontend/env-config.js` - Generated config

## More Help

- [README.md](README.md) - Full documentation
- [ENV-SETUP.md](ENV-SETUP.md) - Environment guide
- [DEPLOYMENT.md](DEPLOYMENT.md) - AWS deployment
