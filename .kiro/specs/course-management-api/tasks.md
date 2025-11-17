# Implementation Plan

- [x] 1. Set up Spring Boot project structure and dependencies



  - Create Maven-based Spring Boot project with required dependencies (Spring Web, Spring Data JPA, H2 Database, Lombok, Validation)
  - Configure application.properties with H2 database settings
  - Set up base package structure (controller, service, repository, entity, dto, exception)
  - _Requirements: All requirements depend on proper project setup_



- [ ] 2. Implement Course entity and repository layer
  - [x] 2.1 Create Course entity class with JPA annotations


    - Define Course entity with id, title, description, instructor, and duration fields
    - Add JPA annotations for table mapping, primary key generation, and constraints
    - Include Lombok annotations for getters, setters, and constructors
    - _Requirements: 1.1, 2.1, 2.2, 3.1, 4.1_


  - [ ] 2.2 Create CourseRepository interface
    - Extend JpaRepository with Course entity and Long ID type
    - Add custom query methods for existsByTitle and findByTitle
    - _Requirements: 1.3, 2.1, 2.2, 3.1, 4.1_



- [ ] 3. Implement DTOs and validation
  - [x] 3.1 Create CourseDTO class


    - Define CourseDTO with all course fields
    - Add Bean Validation annotations (@NotBlank, @NotNull, @Size, @Min)
    - Include Lombok annotations for data class functionality
    - _Requirements: 1.1, 1.2, 2.1, 3.1, 3.3_



- [ ] 4. Implement custom exceptions
  - [ ] 4.1 Create CourseNotFoundException
    - Define custom exception for course not found scenarios
    - Extend RuntimeException with appropriate constructor
    - _Requirements: 2.3, 3.2, 4.2_
  - [ ] 4.2 Create DuplicateCourseException
    - Define custom exception for duplicate course title scenarios


    - Extend RuntimeException with appropriate constructor
    - _Requirements: 1.3_

- [ ] 5. Implement global exception handler
  - [ ] 5.1 Create GlobalExceptionHandler with @ControllerAdvice
    - Implement exception handler methods for CourseNotFoundException (404)
    - Implement exception handler for DuplicateCourseException (409)
    - Implement exception handler for MethodArgumentNotValidException (400)



    - Implement exception handler for HttpMessageNotReadableException (400)
    - Implement generic exception handler for unexpected errors (500)
    - Create standardized error response format with timestamp, status, error, message, and path
    - _Requirements: 1.2, 1.3, 2.3, 3.2, 3.3, 4.2, 5.1, 5.2, 5.3_

- [ ] 6. Implement CourseService with business logic
  - [ ] 6.1 Create CourseService interface and implementation
    - Implement createCourse method with duplicate title check
    - Implement getAllCourses method to retrieve all courses
    - Implement getCourseById method with not found handling
    - Implement updateCourse method with existence check
    - Implement deleteCourse method with existence check
    - Add DTO to Entity and Entity to DTO conversion logic
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 4.1, 4.2_

- [ ] 7. Implement REST controller endpoints
  - [ ] 7.1 Create CourseController with REST endpoints
    - Implement POST /api/courses endpoint for creating courses
    - Implement GET /api/courses endpoint for retrieving all courses
    - Implement GET /api/courses/{id} endpoint for retrieving course by ID
    - Implement PUT /api/courses/{id} endpoint for updating courses
    - Implement DELETE /api/courses/{id} endpoint for deleting courses
    - Add @Valid annotation for request body validation
    - Return appropriate ResponseEntity with correct HTTP status codes
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 4.1, 4.2_

- [ ] 8. Create main application class and verify configuration
  - [ ] 8.1 Ensure Spring Boot main application class exists
    - Verify @SpringBootApplication annotation is present
    - Confirm application starts successfully
    - _Requirements: All requirements_
  - [ ] 8.2 Write integration tests for REST endpoints
    - Create test class with @SpringBootTest and MockMvc
    - Write tests for successful course creation (201)
    - Write tests for validation failures (400)
    - Write tests for duplicate course creation (409)
    - Write tests for retrieving all courses (200)
    - Write tests for retrieving course by ID (200, 404)
    - Write tests for updating courses (200, 404, 400)
    - Write tests for deleting courses (204, 404)
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 4.1, 4.2, 5.1, 5.2, 5.3_
