# Database Migration Scripts

This directory contains Flyway migration scripts for the Expense Management System database schema.

## Migration Order

Flyway executes migrations in version order:

1. **V1__create_users_table.sql** - Creates the users table for authentication and user profiles
2. **V2__create_categories_table.sql** - Creates the categories table and inserts predefined expense categories
3. **V3__create_expenses_table.sql** - Creates the expenses table with indexes for performance
4. **V4__create_receipts_table.sql** - Creates the receipts table for storing receipt file metadata

## Configuration

Flyway is configured in `application.yml`:
- Location: `classpath:db/migration`
- Baseline on migrate: enabled
- Database: PostgreSQL

## Running Migrations

Migrations run automatically when the Spring Boot application starts. To run migrations manually:

```bash
mvn flyway:migrate
```

To check migration status:

```bash
mvn flyway:info
```

To validate migrations:

```bash
mvn flyway:validate
```

## Requirements

These migrations satisfy requirements 7.4 and 7.5:
- 7.4: Implement database migrations for schema management
- 7.5: Store all user data, expense records, and metadata in AWS RDS
