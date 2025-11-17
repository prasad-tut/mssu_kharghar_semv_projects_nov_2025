# Design Document - Course Management API

## Overview

The Course Management System is a Spring Boot REST API application that provides CRUD operations for managing courses. The application follows a layered architecture pattern with clear separation of concerns: Controller layer for HTTP handling, Service layer for business logic, Repository layer for data persistence, and Entity/DTO layers for data representation.

## Architecture

The application uses a standard Spring Boot layered architecture:

```
┌─────────────────────────────────────┐
│         REST Controller             │  ← HTTP Endpoints
├─────────────────────────────────────┤
│         Service Layer               │  ← Business Logic
├─────────────────────────────────────┤
│       Repository Layer              │  ← Data Access
├─────────────────────────────────────┤
│         Database (H2/MySQL)         │  ← Persistence
└─────────────────────────────────────┘
```

**Technology Stack:**
- Spring Boot 3.x
- Spring Web (REST API)
- Spring Data JPA (Data Access)
- H2 Database (Development) / MySQL (Production-ready option)
- Bean Validation (Input validation)
- Lombok (Boilerplate reduction)
- Maven (Build tool)

## Components and Interfaces

### 1. Course Entity

The core domain model representing a course in the database.

```java
@Entity
@Table(name = "courses")
class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String instructor;
    
    @Column(nullable = false)
    private Integer duration; // in hours
}
```

### 2. Course DTO (Data Transfer Object)

Used for API requests and responses to decouple the API contract from the internal entity structure.

```java
class CourseDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Instructor is required")
    private String instructor;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer duration;
}
```

### 3. Course Repository

Spring Data JPA repository interface for database operations.

```java
@Repository
interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitle(String title);
    Optional<Course> findByTitle(String title);
}
```

### 4. Course Service

Business logic layer that orchestrates operations between controller and repository.

```java
@Service
interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO);
    List<CourseDTO> getAllCourses();
    CourseDTO getCourseById(Long id);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);
}
```

### 5. Course Controller

REST API endpoints for course management.

```java
@RestController
@RequestMapping("/api/courses")
class CourseController {
    
    @PostMapping
    ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO);
    
    @GetMapping
    ResponseEntity<List<CourseDTO>> getAllCourses();
    
    @GetMapping("/{id}")
    ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id);
    
    @PutMapping("/{id}")
    ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO);
    
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCourse(@PathVariable Long id);
}
```

## Data Models

### Course Data Model

| Field       | Type    | Constraints                    | Description                    |
|-------------|---------|--------------------------------|--------------------------------|
| id          | Long    | Primary Key, Auto-generated    | Unique course identifier       |
| title       | String  | Not null, Unique               | Course title                   |
| description | String  | Max 1000 chars                 | Course description             |
| instructor  | String  | Not null                       | Instructor name                |
| duration    | Integer | Not null, Min 1                | Course duration in hours       |

### API Request/Response Format

**Create/Update Course Request:**
```json
{
  "title": "Spring Boot Fundamentals",
  "description": "Learn the basics of Spring Boot framework",
  "instructor": "John Doe",
  "duration": 40
}
```

**Course Response:**
```json
{
  "id": 1,
  "title": "Spring Boot Fundamentals",
  "description": "Learn the basics of Spring Boot framework",
  "instructor": "John Doe",
  "duration": 40
}
```

## Error Handling

### Global Exception Handler

A centralized exception handler using `@ControllerAdvice` to manage all API errors consistently.

**Exception Types:**
- `CourseNotFoundException` - When a course ID doesn't exist (404)
- `DuplicateCourseException` - When attempting to create a course with duplicate title (409)
- `MethodArgumentNotValidException` - Bean validation failures (400)
- `HttpMessageNotReadableException` - Malformed JSON (400)
- `Exception` - Unexpected errors (500)

**Error Response Format:**
```json
{
  "timestamp": "2025-11-17T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Course not found with id: 123",
  "path": "/api/courses/123"
}
```

## Testing Strategy

### Unit Tests
- Service layer tests with mocked repositories
- Validation tests for DTOs
- Business logic verification

### Integration Tests
- Controller tests using MockMvc
- End-to-end API endpoint testing
- Database integration tests with test containers or H2

### Test Coverage Focus
- All CRUD operations
- Validation scenarios (missing fields, invalid data)
- Error handling paths (not found, duplicates)
- Edge cases (empty lists, boundary values)

## Configuration

### Application Properties

**Development (application.properties):**
```properties
spring.application.name=course-management-api
spring.datasource.url=jdbc:h2:mem:coursedb
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
```

### API Endpoints Summary

| Method | Endpoint           | Description              | Status Codes        |
|--------|--------------------|--------------------------|---------------------|
| POST   | /api/courses       | Create new course        | 201, 400, 409       |
| GET    | /api/courses       | Get all courses          | 200                 |
| GET    | /api/courses/{id}  | Get course by ID         | 200, 404            |
| PUT    | /api/courses/{id}  | Update course            | 200, 400, 404       |
| DELETE | /api/courses/{id}  | Delete course            | 204, 404            |

## Design Decisions

1. **H2 In-Memory Database**: Chosen for simplicity and quick setup. Can be easily switched to MySQL or PostgreSQL for production.

2. **DTO Pattern**: Separates API contract from internal entity structure, allowing independent evolution of both.

3. **Bean Validation**: Uses JSR-380 annotations for declarative validation, keeping validation logic close to the data model.

4. **Global Exception Handling**: Centralizes error handling logic for consistent API responses and easier maintenance.

5. **RESTful Conventions**: Follows REST best practices with appropriate HTTP methods and status codes for intuitive API usage.
