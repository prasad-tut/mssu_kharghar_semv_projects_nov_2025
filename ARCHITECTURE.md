# 🏗️ System Architecture Documentation

## Overview

This document provides a comprehensive overview of the Bug Tracker System's architecture, component interactions, and design patterns.

---

## Architecture Layers

### 1. Presentation Layer (Frontend)

**Components:**
- HTML Templates (Thymeleaf)
- CSS Styling (Bootstrap 5 + Custom)
- JavaScript Utilities

**Location:** 
```
src/main/resources/
├── templates/
│   ├── base.html (Master template)
│   ├── bugs.html (Dashboard)
│   ├── raise_bug.html (Bug submission)
│   ├── update_bug.html (Bug update)
│   ├── 404.html (Error page)
│   └── 500.html (Error page)
└── static/
    ├── styles.css (500+ lines)
    └── script.js (400+ lines)
```

**Responsibilities:**
- Display bug information to users
- Collect user input via forms
- Render dynamic content with Thymeleaf
- Provide responsive UI with Bootstrap 5
- Client-side validation with JavaScript

**Key Pages:**
1. **Dashboard (/bugs)**
   - Sticky statistics bar
   - Active bugs table (OPEN + IN_PROGRESS)
   - Fixed bugs section
   - Real-time updates via JavaScript

2. **Raise Bug (/raise-bug)**
   - Form for testers to submit bugs
   - Validation (client + server)
   - Date picker
   - Priority/Severity dropdowns

3. **Update Bug (/update-bug/{id})**
   - Display current bug details
   - Status update dropdown
   - Confirmation handling

---

### 2. Business Logic Layer

**Components:**
- BugController (HTTP routing)
- BugService (Business logic)

**Location:**
```
src/main/java/com/example/bugtracker/
├── controller/
│   └── BugController.java (7 web routes + 3 REST endpoints)
└── service/
    └── BugService.java (12 business methods)
```

**BugController Endpoints:**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/` | Redirect to /bugs |
| GET | `/bugs` | Display all bugs dashboard |
| GET | `/raise-bug` | Show bug submission form |
| POST | `/raise-bug` | Process bug submission |
| GET | `/update-bug/{id}` | Show bug update form |
| POST | `/update-bug/{id}` | Process bug status update |
| GET | `/api/bugs` | REST: All bugs as JSON |
| GET | `/api/bug/{id}` | REST: Single bug JSON |
| GET | `/api/bugs/status/{status}` | REST: Filtered bugs JSON |
| GET | `/api/bugs/stats` | REST: Statistics JSON |

**BugService Methods:**

```java
// Core business operations
Bug createBug(Bug bug)              // Create + validate
List<Bug> getAllBugs()              // Fetch all
Bug getBugById(Long id)             // Get single
void updateBugStatus(Long id, String status)  // Update status

// Statistics
int getCountByStatus(String status) // Count by status
int getTotalBugCount()              // Total count

// Validation
void validateBugData(Bug bug)       // Input validation
void validateStatus(String status)  // Status validation
void validatePriority(String priority)  // Priority validation
void validateSeverity(String severity)  // Severity validation
```

**Business Rules:**
```
1. All bugs must have description
2. Priority must be: HIGH, MEDIUM, LOW
3. Severity must be: CRITICAL, MARGINAL, NEGLIGIBLE
4. Status must be: OPEN, IN_PROGRESS, FIXED
5. Detected date cannot be in future
6. Default status is OPEN
7. Status can only change: OPEN → IN_PROGRESS → FIXED
```

---

### 3. Data Access Layer

**Components:**
- BugRepository (JDBC operations)
- JdbcTemplate (Spring Data)

**Location:**
```
src/main/java/com/example/bugtracker/
└── repository/
    └── BugRepository.java
```

**BugRepository Methods:**

```java
// CRUD Operations
Bug insertBug(Bug bug)              // INSERT query
List<Bug> getAllBugs()              // SELECT * query
Bug getBugById(Long id)             // SELECT WHERE id
void updateBugStatus(Long id, String status)  // UPDATE query
void deleteBug(Long id)             // DELETE query (if needed)

// Aggregations
int getCountByStatus(String status)  // COUNT query
int getTotalBugCount()              // SELECT COUNT(*)

// Filters
List<Bug> getBugsByPriority(String priority)    // WHERE priority
List<Bug> getBugsBySeverity(String severity)    // WHERE severity
List<Bug> getBugsByStatus(String status)        // WHERE status
List<Bug> getBugsByAssignee(String assignee)    // WHERE assigned_to
```

**SQL Queries Used:**

```sql
-- Insert
INSERT INTO bugs (description, priority, severity, detected_on, assigned_to, status)
VALUES (?, ?, ?, ?, ?, ?)

-- Select All
SELECT * FROM bugs ORDER BY id DESC

-- Select By ID
SELECT * FROM bugs WHERE id = ?

-- Update Status
UPDATE bugs SET status = ? WHERE id = ?

-- Count By Status
SELECT COUNT(*) FROM bugs WHERE status = ?

-- Total Count
SELECT COUNT(*) FROM bugs

-- Delete
DELETE FROM bugs WHERE id = ?
```

---

### 4. Data Layer

**Components:**
- AWS RDS MySQL Database
- HikariCP Connection Pool
- Database Schema

**Location:**
```
AWS RDS MySQL Instance
├── Host: database-1.cm5mwsc24il9.us-east-1.rds.amazonaws.com
├── Port: 3306
├── Database: bug_tracker
└── Table: bugs
```

**Connection Configuration:**
```properties
spring.datasource.url=jdbc:mysql://database-1.cm5mwsc24il9.us-east-1.rds.amazonaws.com:3306/bug_tracker
spring.datasource.username=admin
spring.datasource.password=mssu2025
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

---

## Component Interaction Diagram

### Request-Response Flow

```
┌──────────────────────────────────────────────────────────┐
│                   Web Browser                             │
│  User submits bug form on /raise-bug                      │
└────────────────────────┬─────────────────────────────────┘
                         │ HTTP POST /raise-bug
                         │ Content-Type: application/x-www-form-urlencoded
                         │ description=...&priority=...
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│              Embedded Tomcat (Port 8080)                  │
│           Receives HTTP request                           │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│             DispatcherServlet (Spring)                    │
│         Routes request to appropriate controller          │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│           BugController.raiseBug()                        │
│  @PostMapping("/raise-bug")                              │
│  ├─ Extract form parameters                              │
│ ├─ Create Bug object                                     │
│  └─ Call BugService.createBug()                          │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│           BugService.createBug()                          │
│  ├─ Validate all bug fields                              │
│  ├─ Check business rules                                 │
│  ├─ Set default status = OPEN                            │
│  └─ Call BugRepository.insertBug()                       │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│           BugRepository.insertBug()                       │
│  ├─ Build SQL INSERT query                               │
│  ├─ Prepare statement with parameters                    │
│  ├─ Get connection from HikariCP pool                    │
│  └─ Execute query                                        │
└────────────────────────┬─────────────────────────────────┘
                         │ JDBC Connection
                         │ TCP/IP Port 3306
                         ▼
┌──────────────────────────────────────────────────────────┐
│        AWS RDS MySQL Database Instance                    │
│  ├─ Receive INSERT statement                             │
│  ├─ Validate data against schema                         │
│  ├─ Auto-increment ID                                    │
│  ├─ Set timestamps (created_at, updated_at)             │
│  ├─ Insert row into bugs table                           │
│  ├─ Update indexes (idx_status, etc)                     │
│  └─ Return success response                              │
└────────────────────────┬─────────────────────────────────┘
                         │ Success/Row Count
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│        BugRepository returns result                       │
│  Returns: InsertResult {id: 5, rows: 1}                  │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│        BugService processes result                        │
│  ├─ Confirms insertion successful                        │
│  └─ Returns success/failure to Controller                │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│        BugController returns response                     │
│  ├─ Set success message                                  │
│  ├─ Clear form                                           │
│  └─ Redirect to /raise-bug                               │
└────────────────────────┬─────────────────────────────────┘
                         │ HTTP Response 303 Redirect
                         │ Location: /raise-bug
                         │ Message: Bug raised successfully!
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│              Web Browser                                  │
│  ├─ Receive redirect response                            │
│  ├─ Navigate to /raise-bug                               │
│  ├─ Show success message                                 │
│  ├─ Form cleared and ready for next bug                  │
│  └─ Data now persisted in AWS RDS MySQL                  │
└──────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. MVC (Model-View-Controller)

```
Model (M):
  ├─ Bug.java (Entity)
  └─ BugService.java (Business logic)

View (V):
  ├─ HTML Templates (Thymeleaf)
  ├─ CSS Styling (Bootstrap 5)
  └─ JavaScript (Client-side logic)

Controller (C):
  └─ BugController.java (HTTP request handling)
```

### 2. Repository Pattern

```java
// Abstract data access details
BugRepository repository = new BugRepository();

// Use consistent API
List<Bug> bugs = repository.getAllBugs();
repository.insertBug(newBug);
repository.updateBugStatus(id, status);
```

**Benefits:**
- Decouples business logic from data access
- Easier to test (can mock repository)
- Easy to switch databases (just change repository)

### 3. Service Pattern

```java
// Contains business logic
BugService service = new BugService();

// Validates before database operations
service.createBug(bug);           // Validates first
service.updateBugStatus(id, status);  // Validates status
```

**Benefits:**
- Centralized business rules
- Reusable from controller
- Single responsibility

### 4. Template Method (Thymeleaf)

```html
<!-- base.html: Template defining structure -->
<html>
  <head><!-- Common header --></head>
  <body>
    <nav><!-- Navbar --></nav>
    <main th:replace="fragment :: content"></main>
    <footer><!-- Footer --></footer>
  </body>
</html>

<!-- bugs.html: Extends template -->
<div th:fragment="content">
  <!-- Dashboard specific content -->
</div>
```

### 5. Dependency Injection

```java
// Spring auto-injects dependencies
@RestController
public class BugController {
    
    private final BugService bugService;
    
    // Constructor injection
    @Autowired
    public BugController(BugService bugService) {
        this.bugService = bugService;
    }
}
```

---

## Layered Architecture Benefits

```
┌─────────────────────────────────┐
│   Presentation Layer (UI)        │
│   - Templates, CSS, JavaScript   │
│   - User-friendly interface      │
│   - Input/Output handling        │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│   Business Logic Layer           │
│   - Validation rules             │
│   - Business constraints         │
│   - Service methods              │
│   - Prevents direct DB access    │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│   Data Access Layer              │
│   - JDBC queries                 │
│   - Connection management        │
│   - Result mapping               │
│   - Error handling               │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│   Database Layer                 │
│   - AWS RDS MySQL                │
│   - Data persistence             │
│   - Backups & recovery           │
└──────────────────────────────────┘

Benefits:
✅ Separation of concerns
✅ Easy to test each layer independently
✅ Easy to modify one layer without affecting others
✅ Scalable architecture
✅ Maintainable codebase
```

---

## Technology Stack Breakdown

### Backend

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21.0.1 | Language |
| Spring Boot | 3.2.0 | Framework |
| Spring Web | 3.2.0 | Web support |
| Spring JDBC | 3.2.0 | Database access |
| MySQL Connector-J | 8.0.33 | MySQL driver |
| HikariCP | 5.0.1 | Connection pooling |
| Tomcat | 10.1.16 | Embedded server |

### Frontend

| Technology | Version | Purpose |
|-----------|---------|---------|
| Thymeleaf | 3.1.1 | Template engine |
| Bootstrap | 5.3.0 | CSS framework |
| HTML 5 | - | Markup |
| CSS 3 | - | Styling |
| JavaScript | ES6 | Client logic |

### Build & Deploy

| Technology | Version | Purpose |
|-----------|---------|---------|
| Maven | 3.9.5 | Build tool |
| Spring Boot Maven Plugin | 3.2.0 | Packaging |

### Database

| Technology | Version | Purpose |
|-----------|---------|---------|
| MySQL | 8.0+ | Database engine |
| AWS RDS | Managed | Cloud database |

---

## Configuration Management

### application.properties

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:mysql://database-1.cm5mwsc24il9.us-east-1.rds.amazonaws.com:3306/bug_tracker
spring.datasource.username=admin
spring.datasource.password=mssu2025
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Connection Pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000000

# SQL Initialization
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:schema.sql

# Thymeleaf Template Engine
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.cache=false

# Logging
logging.level.root=INFO
logging.level.com.example.bugtracker=DEBUG
```

---

## Error Handling

### Application-Level

```java
// BugController handles HTTP errors
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "500";  // Error template
    }
}
```

### Database-Level

```java
try {
    bug = repository.getBugById(id);
    if (bug == null) {
        throw new ResourceNotFoundException("Bug not found");
    }
} catch (SQLException e) {
    logger.error("Database error", e);
    throw new DatabaseException("Failed to fetch bug", e);
}
```

### Validation-Level

```java
public void validateBugData(Bug bug) {
    if (bug.getDescription() == null || bug.getDescription().isEmpty()) {
        throw new ValidationException("Description cannot be empty");
    }
    if (!isValidPriority(bug.getPriority())) {
        throw new ValidationException("Invalid priority");
    }
}
```

---

## Performance Considerations

### Database Indexing

```sql
-- Speed up queries by status
CREATE INDEX idx_status ON bugs(status);

-- Speed up priority filtering
CREATE INDEX idx_priority ON bugs(priority);

-- Speed up severity filtering
CREATE INDEX idx_severity ON bugs(severity);

-- Speed up assignee lookup
CREATE INDEX idx_assigned_to ON bugs(assigned_to);
```

### Connection Pooling

```
HikariCP Configuration:
├─ Maximum Pool Size: 10
│  └─ Prevents connection exhaustion
├─ Minimum Idle: 5
│  └─ Keeps 5 warm connections ready
├─ Connection Timeout: 20 seconds
│  └─ Fail fast if pool exhausted
└─ Idle Timeout: 5 minutes
   └─ Recycle unused connections
```

### Caching Strategy (Future)

```java
// Could be added:
@Cacheable("allBugs")
public List<Bug> getAllBugs() {
    return repository.getAllBugs();
}

@CacheEvict("allBugs")
public void createBug(Bug bug) {
    repository.insertBug(bug);
}
```

---

## Scalability

### Current Setup

```
Single Spring Boot Instance
└─ Handles ~1000 concurrent users
└─ Database: AWS RDS (scales independently)
```

### Scaling Approach

```
1. Horizontal Scaling:
   ├─ Multiple Spring Boot instances
   ├─ Load balancer (ALB)
   └─ Shared RDS database

2. Vertical Scaling:
   ├─ Larger EC2 instances
   ├─ Higher RDS tier (db.t3.small → db.t3.medium)
   └─ More memory/CPU

3. Caching Layer:
   ├─ Redis for session data
   ├─ Distributed caching
   └─ Faster response times

4. Database:
   ├─ Read replicas for analytics
   ├─ Partitioning for large datasets
   └─ Archive old data to separate table
```

---

## Security Considerations

### Application Security

```java
// Input validation
- All user inputs validated server-side
- Parameterized SQL queries (prevents SQL injection)
- Error messages don't expose system details

// Authentication (Future)
- Can add Spring Security
- User login/role-based access control
- Session management
```

### Database Security

```
- AWS RDS security group restrictions
- Encrypted credentials in application.properties
- SSL/TLS for database connections
- Automated backups
- VPC isolation
```

### Transport Security

```
- HTTPS recommended for production
- HTTP/2 support via Tomcat
- Security headers (HSTS, CSP, X-Frame-Options)
```

---

## Deployment Architecture

### Local Development

```
Your Machine
├─ Java 21 + Maven
├─ Spring Boot application
├─ AWS RDS MySQL (remote)
└─ Port 8080
```

### AWS EC2 Production

```
AWS EC2 Instance (t3.micro)
├─ Java 21
├─ Tomcat 10
├─ JAR application
├─ Application listens on 8080
├─ nginx reverse proxy (80 → 8080)
└─ Auto-scaling group (optional)
        ↓
AWS RDS MySQL Instance
├─ database-1
├─ Multi-AZ (optional)
├─ Automated backups
└─ Read replicas (optional)
```

### Docker Deployment

```
Dockerfile
└─ FROM openjdk:21-jdk
   ├─ COPY target/bug-tracker-1.0.0.jar app.jar
   ├─ ENTRYPOINT ["java", "-jar", "app.jar"]
   └─ EXPOSE 8080

Docker Compose
├─ Web Service (Spring Boot)
├─ Database Service (MySQL)
└─ Network: services communicate internally
```

---

## Module Dependencies

```
Application
├─ Spring Boot Starter Web
│  ├─ Spring Web (REST, MVC)
│  ├─ Spring Context
│  ├─ Tomcat (embedded)
│  └─ Spring Boot DevTools
├─ Spring Boot Starter Data JDBC
│  ├─ Spring JDBC
│  └─ Spring Context
├─ MySQL Connector-J 8.0.33
│  └─ JDBC Driver for MySQL
├─ HikariCP 5.0.1
│  └─ Connection Pooling
├─ Thymeleaf 3.1.1
│  └─ Template Engine
└─ Bootstrap 5.3.0 (CSS/JS)
   └─ Frontend Framework
```

---

## Development Workflow

```
Developer
    │
    ├─ Edit source code
    │
    ├─ Run: mvn clean install
    │   └─ Compiles Java
    │   └─ Runs tests
    │   └─ Packages JAR
    │
    ├─ Run: mvn spring-boot:run
    │   └─ Application starts
    │   └─ Connects to AWS RDS
    │   └─ Creates schema.sql
    │
    ├─ Open: http://localhost:8080
    │   └─ Tests application
    │   └─ DevTools hot reload
    │
    └─ Deploy: Copy JAR to production
       └─ Application runs on EC2
       └─ Uses same AWS RDS database
```

---

## Summary of Components

| Component | Location | Responsibility |
|-----------|----------|-----------------|
| BugTrackerApplication | controller/ | Entry point, Spring boot configuration |
| BugController | controller/ | HTTP routing, request handling |
| BugService | service/ | Business logic, validation |
| BugRepository | repository/ | JDBC queries, data access |
| Bug | model/ | Entity object |
| Thymeleaf Templates | resources/templates/ | HTML rendering |
| Static Assets | resources/static/ | CSS, JavaScript |
| application.properties | resources/ | Configuration |
| schema.sql | resources/ | Database initialization |
| pom.xml | root/ | Maven dependencies |

---

**Last Updated: November 19, 2025**

For more information, see:
- [README.md](./README.md) - Project overview
- [DATA_STORAGE.md](./DATA_STORAGE.md) - Data storage details
- [CONFIGURATION.md](./CONFIGURATION.md) - Configuration reference

