# Expense Management System - Design Document

## Overview

The Expense Management System is a full-stack cloud application consisting of:
- **Backend**: Java Spring Boot REST API with Maven build system
- **Database**: AWS RDS PostgreSQL for data persistence
- **Frontend**: React-based single-page application
- **Authentication**: JWT-based stateless authentication
- **File Storage**: Local file system or AWS S3 for receipt storage

The system follows a three-tier architecture with clear separation between presentation, business logic, and data layers.

## Architecture

### High-Level Architecture

```
┌─────────────────┐
│  React Frontend │
│   (Port 3000)   │
└────────┬────────┘
         │ HTTP/REST
         │
┌────────▼────────┐
│  Spring Boot    │
│   Backend API   │
│   (Port 8080)   │
└────────┬────────┘
         │ JDBC
         │
┌────────▼────────┐
│   AWS RDS       │
│  PostgreSQL     │
└─────────────────┘
```

### Technology Stack

**Backend:**
- Java 17
- Spring Boot 3.x
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL Driver
- Maven 3.x
- Lombok (reduce boilerplate)
- Hibernate Validator
- SpringDoc OpenAPI (API documentation)

**Frontend:**
- React 18
- React Router (navigation)
- Axios (HTTP client)
- Material-UI or Bootstrap (UI components)
- React Hook Form (form management)
- Chart.js (reporting visualizations)

**Database:**
- AWS RDS PostgreSQL 14+

**Build & Deployment:**
- Maven for backend build
- npm/yarn for frontend build
- Environment-based configuration

## Components and Interfaces

### Backend Components

#### 1. Controller Layer

**AuthController**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Authenticate and return JWT token
- `POST /api/auth/refresh` - Refresh expired token

**ExpenseController**
- `GET /api/expenses` - Get all expenses for authenticated user (with pagination, sorting, filtering)
- `GET /api/expenses/{id}` - Get expense by ID
- `POST /api/expenses` - Create new expense
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}` - Delete expense
- `POST /api/expenses/{id}/submit` - Submit expense for approval
- `GET /api/expenses/pending` - Get pending expenses (manager only)
- `POST /api/expenses/{id}/approve` - Approve expense (manager only)
- `POST /api/expenses/{id}/reject` - Reject expense (manager only)

**CategoryController**
- `GET /api/categories` - Get all expense categories

**ReceiptController**
- `POST /api/receipts` - Upload receipt file
- `GET /api/receipts/{id}` - Download receipt file
- `DELETE /api/receipts/{id}` - Delete receipt

**ReportController**
- `GET /api/reports/summary` - Get expense summary with filters (date range, category, status)
- `GET /api/reports/export` - Export report as CSV or PDF

**HealthController**
- `GET /api/health` - Health check endpoint

#### 2. Service Layer

**AuthService**
- User registration with password hashing
- User authentication
- JWT token generation and validation

**ExpenseService**
- CRUD operations for expenses
- Business logic for expense validation
- Status transitions (Draft → Submitted → Approved/Rejected)
- Authorization checks (users can only access their own expenses)

**CategoryService**
- Retrieve predefined categories
- Category validation

**ReceiptService**
- File upload handling
- File storage management
- File validation (type, size)

**ReportService**
- Query expenses with filters
- Calculate aggregations (totals, counts)
- Generate export files

#### 3. Repository Layer

**UserRepository** (extends JpaRepository)
- `findByEmail(String email): Optional<User>`
- `existsByEmail(String email): boolean`

**ExpenseRepository** (extends JpaRepository)
- `findByUserId(Long userId, Pageable pageable): Page<Expense>`
- `findByUserIdAndStatus(Long userId, ExpenseStatus status): List<Expense>`
- `findByStatus(ExpenseStatus status): List<Expense>`
- `findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end): List<Expense>`
- `findByUserIdAndCategory(Long userId, Category category): List<Expense>`

**CategoryRepository** (extends JpaRepository)
- `findByName(String name): Optional<Category>`

**ReceiptRepository** (extends JpaRepository)
- `findByExpenseId(Long expenseId): Optional<Receipt>`

#### 4. Security Components

**JwtTokenProvider**
- Generate JWT tokens
- Validate JWT tokens
- Extract user information from tokens

**JwtAuthenticationFilter**
- Intercept requests
- Extract and validate JWT from Authorization header
- Set authentication in SecurityContext

**SecurityConfig**
- Configure security rules
- Define public and protected endpoints
- Configure CORS

### Frontend Components

#### Pages
- **LoginPage** - User authentication
- **RegisterPage** - User registration
- **DashboardPage** - Overview with statistics
- **ExpenseListPage** - List all expenses with filters
- **ExpenseFormPage** - Create/edit expense
- **ReportPage** - Generate and view reports
- **ApprovalPage** - Manager approval queue (role-based)

#### Shared Components
- **Navbar** - Navigation and user menu
- **ExpenseCard** - Display single expense
- **ExpenseTable** - Tabular expense list
- **CategorySelect** - Category dropdown
- **DateRangePicker** - Date range selection
- **FileUpload** - Receipt upload component
- **StatusBadge** - Visual status indicator
- **LoadingSpinner** - Loading indicator
- **ErrorAlert** - Error message display

#### Services
- **authService** - Authentication API calls
- **expenseService** - Expense CRUD operations
- **categoryService** - Category retrieval
- **receiptService** - Receipt upload/download
- **reportService** - Report generation

#### Context/State Management
- **AuthContext** - Global authentication state
- **ExpenseContext** - Expense data management (optional, can use local state)

## Data Models

### Database Schema

**users**
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
```

**categories**
```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**expenses**
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
```

**receipts**
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
```

### Java Entity Models

**User Entity**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Expense> expenses;
}
```

**Expense Entity**
```java
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDate expenseDate;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;
    
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    
    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    
    private String reviewNotes;
    
    @OneToOne(mappedBy = "expense", cascade = CascadeType.ALL)
    private Receipt receipt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Category Entity**
```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    private LocalDateTime createdAt;
}
```

**Receipt Entity**
```java
@Entity
@Table(name = "receipts")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private String fileType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    private LocalDateTime uploadedAt;
}
```

### Enums

**UserRole**
- USER
- MANAGER
- ADMIN

**ExpenseStatus**
- DRAFT
- SUBMITTED
- APPROVED
- REJECTED

### DTOs (Data Transfer Objects)

**Request DTOs:**
- RegisterRequest (email, password, firstName, lastName)
- LoginRequest (email, password)
- ExpenseRequest (categoryId, amount, expenseDate, description)
- ApprovalRequest (approved, reviewNotes)

**Response DTOs:**
- AuthResponse (token, refreshToken, user)
- UserResponse (id, email, firstName, lastName, role)
- ExpenseResponse (id, category, amount, expenseDate, description, status, receipt, createdAt)
- CategoryResponse (id, name, description)
- ReceiptResponse (id, fileName, fileType, fileSize, uploadedAt)
- ReportResponse (expenses, totalAmount, count, filters)
- ErrorResponse (timestamp, status, error, message, path)

## Error Handling

### Backend Error Handling

**Global Exception Handler (@ControllerAdvice)**
- `ResourceNotFoundException` → 404 Not Found
- `UnauthorizedException` → 401 Unauthorized
- `ForbiddenException` → 403 Forbidden
- `ValidationException` → 400 Bad Request
- `FileUploadException` → 400 Bad Request
- `DatabaseException` → 500 Internal Server Error
- `Exception` (catch-all) → 500 Internal Server Error

**Validation**
- Use Bean Validation annotations (@NotNull, @NotBlank, @Email, @Positive, @PastOrPresent)
- Custom validators for business rules
- Return field-specific error messages

### Frontend Error Handling

- Display user-friendly error messages
- Handle network errors gracefully
- Show validation errors on forms
- Implement retry logic for failed requests
- Log errors to console for debugging

## Testing Strategy

### Backend Testing

**Unit Tests**
- Service layer business logic
- Utility classes and helpers
- JWT token generation and validation
- Validation logic

**Integration Tests**
- Controller endpoints with MockMvc
- Repository queries with test database
- Security configuration
- File upload/download

**Test Coverage Goals**
- Service layer: 80%+
- Controller layer: 70%+
- Overall: 75%+

**Testing Tools**
- JUnit 5
- Mockito
- Spring Boot Test
- H2 in-memory database for tests
- TestContainers (optional, for PostgreSQL integration tests)

### Frontend Testing

**Unit Tests**
- Component rendering
- Service functions
- Utility functions

**Integration Tests**
- User flows (login, create expense, submit for approval)
- Form submissions
- API integration with mock server

**Testing Tools**
- Jest
- React Testing Library
- Mock Service Worker (MSW) for API mocking

## Configuration Management

### Backend Configuration

**application.yml (default)**
```yaml
spring:
  application:
    name: expense-management-api
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/expensedb}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB

jwt:
  secret: ${JWT_SECRET:your-secret-key-change-in-production}
  expiration: 86400000 # 24 hours

file:
  upload-dir: ${UPLOAD_DIR:./uploads}

server:
  port: 8080
```

**application-dev.yml**
- Enable SQL logging
- Use H2 or local PostgreSQL
- Relaxed CORS settings

**application-prod.yml**
- Production database URL
- Strict security settings
- Logging configuration

### Frontend Configuration

**.env.development**
```
REACT_APP_API_URL=http://localhost:8080/api
```

**.env.production**
```
REACT_APP_API_URL=https://api.yourdomain.com/api
```

## Security Considerations

1. **Password Security**: Use BCrypt for password hashing
2. **JWT Security**: Use strong secret keys, implement token expiration and refresh
3. **CORS**: Configure allowed origins appropriately
4. **SQL Injection**: Use parameterized queries (JPA handles this)
5. **File Upload**: Validate file types and sizes, sanitize file names
6. **Authorization**: Implement role-based access control, verify resource ownership
7. **HTTPS**: Use HTTPS in production
8. **Environment Variables**: Never commit secrets to version control

## Deployment Considerations

### AWS RDS Setup
1. Create PostgreSQL RDS instance
2. Configure security groups for backend access
3. Enable automated backups
4. Set up monitoring and alerts

### Backend Deployment
1. Build JAR: `mvn clean package`
2. Deploy to EC2, ECS, or Elastic Beanstalk
3. Set environment variables
4. Configure health checks
5. Set up logging (CloudWatch)

### Frontend Deployment
1. Build production bundle: `npm run build`
2. Deploy to S3 + CloudFront or Netlify/Vercel
3. Configure environment variables
4. Set up CDN caching

### Database Migrations
- Use Flyway or Liquibase for version-controlled schema migrations
- Run migrations before deploying new application versions
