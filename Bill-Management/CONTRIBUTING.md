# Contributing Guide

## Getting Started

1. Fork the repository
2. Clone your fork
3. Create a feature branch
4. Make your changes
5. Test thoroughly
6. Submit a pull request

## Development Setup

```bash
# Setup
cp .env.example .env
./generate-frontend-config.sh

# Start development environment
docker-compose -f docker-compose.dev.yml up postgres
cd bill_management && mvn spring-boot:run
```

## Code Style

### Java
- Follow Spring Boot conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep methods small and focused

### JavaScript
- Use ES6+ features
- Use const/let (not var)
- Add JSDoc comments
- Keep functions pure when possible

### SQL
- Use uppercase for keywords
- Use lowercase for table/column names
- Add indexes for frequently queried columns

## Testing

```bash
# Run backend tests
cd bill_management
mvn test

# Manual testing
# 1. Start application
# 2. Test all CRUD operations
# 3. Test error scenarios
```

## Pull Request Process

1. Update documentation if needed
2. Add tests for new features
3. Ensure all tests pass
4. Update CHANGELOG.md
5. Request review from maintainers

## Commit Messages

```
feat: Add export to PDF feature
fix: Resolve database connection timeout
docs: Update deployment guide
refactor: Simplify bill repository queries
test: Add unit tests for bill service
```

## Questions?

Open an issue or contact maintainers.
