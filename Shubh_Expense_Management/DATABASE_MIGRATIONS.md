# Database Migrations Guide

This guide explains how database migrations work in the Expense Management System and how to manage them.

## Overview

The Expense Management System uses **Flyway** for database schema version control and migrations. Flyway automatically applies database changes in a controlled, versioned manner.

## How Migrations Work

### Automatic Execution

Migrations run automatically when the Spring Boot application starts. Flyway:
1. Checks the `flyway_schema_history` table for applied migrations
2. Compares with migration files in `src/main/resources/db/migration/`
3. Applies any new migrations in order
4. Records successful migrations in the history table

### Migration File Naming Convention

Migration files must follow this naming pattern:

```
V{version}__{description}.sql
```

Examples:
- `V1__create_users_table.sql`
- `V2__create_categories_table.sql`
- `V3__create_expenses_table.sql`
- `V4__create_receipts_table.sql`
- `V5__add_indexes.sql`

**Rules:**
- Prefix: `V` (uppercase)
- Version: Number (1, 2, 3, etc.) or semantic version (1.0, 1.1, 2.0)
- Separator: `__` (double underscore)
- Description: Descriptive name with underscores
- Extension: `.sql`

## Migration Files Location

All migration files are stored in:
```
src/main/resources/db/migration/
```

## Existing Migrations

### V1__create_users_table.sql

Creates the users table for authentication and user management.

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

### V2__create_categories_table.sql

Creates the categories table and populates it with predefined categories.

```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert predefined categories
INSERT INTO categories (name, description) VALUES
    ('Travel', 'Transportation, accommodation, and travel-related expenses'),
    ('Meals', 'Food and beverage expenses'),
    ('Office Supplies', 'Office equipment and supplies'),
    ('Equipment', 'Hardware, software, and equipment purchases'),
    ('Other', 'Miscellaneous expenses');
```

### V3__create_expenses_table.sql

Creates the expenses table with relationships to users and categories.

```sql
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount DECIMAL(10, 2) NOT NULL,
    expense_date DATE NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by BIGINT REFERENCES users(id),
    review_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_amount CHECK (amount > 0)
);

CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_status ON expenses(status);
CREATE INDEX idx_expenses_date ON expenses(expense_date);
CREATE INDEX idx_expenses_category_id ON expenses(category_id);
```

### V4__create_receipts_table.sql

Creates the receipts table for storing receipt file metadata.

```sql
CREATE TABLE receipts (
    id BIGSERIAL PRIMARY KEY,
    expense_id BIGINT UNIQUE NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_receipts_expense_id ON receipts(expense_id);
```

## Running Migrations

### Automatic (Recommended)

Migrations run automatically when you start the application:

```bash
# Development
mvn spring-boot:run

# Production
java -jar -Dspring.profiles.active=prod target/expense-management-system-1.0.0.jar
```

### Manual Execution

You can run migrations manually using Maven:

```bash
# Run migrations
mvn flyway:migrate

# With specific database
mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/expensedb \
  -Dflyway.user=postgres \
  -Dflyway.password=password

# For production database
mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://your-rds-endpoint:5432/expensedb \
  -Dflyway.user=expenseadmin \
  -Dflyway.password=YourSecurePassword
```

### Check Migration Status

```bash
# View migration history
mvn flyway:info

# Output shows:
# - Version
# - Description
# - Type
# - Installed On
# - State (Success, Pending, Failed)
```

### Validate Migrations

```bash
# Validate applied migrations match files
mvn flyway:validate
```

## Creating New Migrations

### Step 1: Create Migration File

Create a new file in `src/main/resources/db/migration/`:

```bash
# Example: Add a new column to expenses table
touch src/main/resources/db/migration/V5__add_expense_notes.sql
```

### Step 2: Write Migration SQL

```sql
-- V5__add_expense_notes.sql
ALTER TABLE expenses ADD COLUMN notes TEXT;
```

### Step 3: Test Migration

```bash
# Test on development database
mvn flyway:migrate

# Verify changes
psql -h localhost -U postgres -d expensedb -c "\d expenses"
```

### Step 4: Commit to Version Control

```bash
git add src/main/resources/db/migration/V5__add_expense_notes.sql
git commit -m "Add notes column to expenses table"
```

## Migration Best Practices

### DO:
- ✅ Use sequential version numbers (V1, V2, V3...)
- ✅ Write descriptive migration names
- ✅ Test migrations on development database first
- ✅ Make migrations idempotent when possible
- ✅ Include rollback scripts in comments
- ✅ Keep migrations small and focused
- ✅ Add indexes for foreign keys and frequently queried columns
- ✅ Use transactions (Flyway does this automatically)

### DON'T:
- ❌ Modify existing migration files after they've been applied
- ❌ Delete migration files
- ❌ Skip version numbers
- ❌ Use the same version number twice
- ❌ Include application logic in migrations
- ❌ Make breaking changes without a migration strategy

## Common Migration Patterns

### Adding a Column

```sql
-- V6__add_user_phone.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

### Adding a Column with Default Value

```sql
-- V7__add_expense_priority.sql
ALTER TABLE expenses ADD COLUMN priority VARCHAR(20) DEFAULT 'NORMAL' NOT NULL;
```

### Creating an Index

```sql
-- V8__add_expense_amount_index.sql
CREATE INDEX idx_expenses_amount ON expenses(amount);
```

### Adding a Foreign Key

```sql
-- V9__add_expense_department.sql
ALTER TABLE expenses ADD COLUMN department_id BIGINT;
ALTER TABLE expenses ADD CONSTRAINT fk_expense_department 
    FOREIGN KEY (department_id) REFERENCES departments(id);
```

### Renaming a Column

```sql
-- V10__rename_expense_description.sql
ALTER TABLE expenses RENAME COLUMN description TO details;
```

### Adding a Constraint

```sql
-- V11__add_email_format_check.sql
ALTER TABLE users ADD CONSTRAINT email_format 
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
```

### Data Migration

```sql
-- V12__migrate_old_categories.sql
-- Update old category names to new standard
UPDATE categories SET name = 'Business Travel' WHERE name = 'Travel';
UPDATE categories SET name = 'Meals & Entertainment' WHERE name = 'Meals';
```

## Troubleshooting

### Migration Failed

If a migration fails:

1. **Check the error message**
   ```bash
   mvn flyway:info
   # Look for failed migrations
   ```

2. **Review the migration SQL**
   - Check for syntax errors
   - Verify table/column names
   - Ensure constraints are valid

3. **Repair the migration**
   ```bash
   # Mark failed migration as resolved
   mvn flyway:repair
   ```

4. **Fix and retry**
   - Fix the SQL in the migration file
   - Run migration again
   ```bash
   mvn flyway:migrate
   ```

### Checksum Mismatch

If you see "Migration checksum mismatch":

**Cause:** Migration file was modified after being applied

**Solution:**
```bash
# Option 1: Repair (if change was intentional and safe)
mvn flyway:repair

# Option 2: Create a new migration instead
# Don't modify existing migrations!
```

### Out of Order Migrations

If you need to add a migration with an earlier version:

```bash
# Enable out-of-order migrations in application.yml
spring:
  flyway:
    out-of-order: true
```

**Note:** This is not recommended for production!

### Baseline Existing Database

If you're adding Flyway to an existing database:

```bash
# Create baseline at version 1
mvn flyway:baseline -Dflyway.baselineVersion=1

# Future migrations will start from version 2
```

## Rollback Strategies

Flyway doesn't support automatic rollbacks, but you can handle them manually:

### Option 1: Create Undo Migration

```sql
-- V13__add_expense_tags.sql (forward)
ALTER TABLE expenses ADD COLUMN tags TEXT[];

-- V14__undo_add_expense_tags.sql (rollback)
ALTER TABLE expenses DROP COLUMN tags;
```

### Option 2: Document Rollback in Comments

```sql
-- V15__add_user_status.sql
-- Rollback: ALTER TABLE users DROP COLUMN status;

ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL;
```

### Option 3: Database Backup and Restore

```bash
# Before migration
pg_dump -h localhost -U postgres expensedb > backup_before_v16.sql

# If rollback needed
psql -h localhost -U postgres expensedb < backup_before_v16.sql
```

## Production Migration Strategy

### Pre-Deployment

1. **Test migrations on staging**
   ```bash
   # Deploy to staging environment
   # Verify migrations succeed
   # Test application functionality
   ```

2. **Backup production database**
   ```bash
   # AWS RDS: Create snapshot
   aws rds create-db-snapshot \
     --db-instance-identifier expense-management-db \
     --db-snapshot-identifier pre-migration-$(date +%Y%m%d)
   ```

3. **Review migration plan**
   ```bash
   mvn flyway:info
   # Verify pending migrations
   ```

### Deployment

1. **Enable maintenance mode** (optional)
   - Display maintenance page to users
   - Prevent new data modifications

2. **Deploy new application version**
   - Migrations run automatically on startup
   - Monitor logs for errors

3. **Verify migrations**
   ```bash
   # Check migration status
   mvn flyway:info
   
   # Verify database schema
   psql -h your-rds-endpoint -U expenseadmin -d expensedb -c "\dt"
   ```

4. **Test application**
   - Run smoke tests
   - Verify critical functionality

5. **Disable maintenance mode**

### Post-Deployment

1. **Monitor application logs**
2. **Check for database errors**
3. **Verify performance metrics**
4. **Keep backup for 24-48 hours**

## Configuration

### application.yml

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    sql-migration-prefix: V
    sql-migration-separator: __
    sql-migration-suffixes: .sql
    validate-on-migrate: true
```

### Disable Flyway (if needed)

```yaml
spring:
  flyway:
    enabled: false
```

Or via environment variable:
```bash
export SPRING_FLYWAY_ENABLED=false
```

## Monitoring

### Check Migration History

```sql
-- Connect to database
psql -h your-rds-endpoint -U expenseadmin -d expensedb

-- View migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- Check latest migration
SELECT version, description, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 1;
```

### CloudWatch Logs

Monitor Flyway logs in CloudWatch:
- Search for "Flyway" in application logs
- Look for "Migrating schema" messages
- Check for errors or warnings

## Additional Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Flyway Maven Plugin](https://flywaydb.org/documentation/usage/maven/)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## Support

For migration issues:
1. Check Flyway logs in application output
2. Review `flyway_schema_history` table
3. Consult this guide
4. Check Flyway documentation
5. Create a GitHub issue with migration details
