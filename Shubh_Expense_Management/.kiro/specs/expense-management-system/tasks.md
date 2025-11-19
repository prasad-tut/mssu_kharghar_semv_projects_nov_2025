# Implementation Plan

## Backend Implementation

- [x] 1. Set up Maven project structure and dependencies





  - Create Maven project with Spring Boot parent
  - Add dependencies: Spring Web, Spring Data JPA, Spring Security, PostgreSQL driver, Lombok, JWT library, validation, SpringDoc OpenAPI
  - Configure application.yml with database connection properties and JWT settings
  - Create package structure: controller, service, repository, model, dto, security, exception, config
  - _Requirements: 7.3, 10.1, 10.2, 10.3_

- [x] 2. Implement database entities and enums





  - Create UserRole enum (USER, MANAGER, ADMIN)
  - Create ExpenseStatus enum (DRAFT, SUBMITTED, APPROVED, REJECTED)
  - Create User entity with JPA annotations and relationships
  - Create Category entity with JPA annotations
  - Create Expense entity with JPA annotations and relationships to User and Category
  - Create Receipt entity with JPA annotations and relationship to Expense
  - Add @PrePersist and @PreUpdate callbacks for timestamp management
  - _Requirements: 1.1, 2.1, 3.1, 4.3, 5.1, 7.5_

- [x] 3. Create repository interfaces





  - Create UserRepository extending JpaRepository with custom query methods
  - Create CategoryRepository extending JpaRepository
  - Create ExpenseRepository extending JpaRepository with custom query methods for filtering
  - Create ReceiptRepository extending JpaRepository
  - _Requirements: 2.3, 3.2, 7.5_

- [x] 4. Implement JWT authentication infrastructure





  - Create JwtTokenProvider class for token generation and validation
  - Create JwtAuthenticationFilter to intercept requests and validate tokens
  - Create SecurityConfig to configure Spring Security with JWT
  - Configure CORS settings in SecurityConfig
  - Create custom UserDetailsService implementation
  - _Requirements: 1.2, 1.3, 1.4, 8.5_

- [x] 5. Create DTO classes for requests and responses





  - Create RegisterRequest DTO with validation annotations
  - Create LoginRequest DTO with validation annotations
  - Create ExpenseRequest DTO with validation annotations
  - Create ApprovalRequest DTO
  - Create AuthResponse, UserResponse, ExpenseResponse, CategoryResponse, ReceiptResponse DTOs
  - Create ReportResponse DTO
  - Create ErrorResponse DTO for standardized error handling
  - _Requirements: 2.2, 8.3, 8.4_

- [x] 6. Implement AuthService and AuthController





  - Create AuthService with user registration method (password hashing with BCrypt)
  - Implement login method with authentication and JWT token generation
  - Create AuthController with /api/auth/register endpoint
  - Create /api/auth/login endpoint
  - Add token refresh endpoint
  - _Requirements: 1.1, 1.2, 1.5_

- [x] 7. Implement CategoryService and CategoryController





  - Create CategoryService to retrieve all categories
  - Implement method to validate category existence
  - Create CategoryController with /api/categories GET endpoint
  - Create database migration or initialization script to populate predefined categories
  - _Requirements: 3.1, 3.2, 3.3_

- [x] 8. Implement ExpenseService for CRUD operations





  - Create ExpenseService with method to create expense with validation
  - Implement method to retrieve all expenses for authenticated user with pagination
  - Implement method to retrieve single expense by ID with authorization check
  - Implement method to update expense with validation and authorization
  - Implement method to delete expense with authorization check
  - Add business logic to ensure users can only access their own expenses
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 9. Implement ExpenseController for CRUD endpoints





  - Create ExpenseController with GET /api/expenses endpoint (with pagination, sorting, filtering)
  - Create GET /api/expenses/{id} endpoint
  - Create POST /api/expenses endpoint
  - Create PUT /api/expenses/{id} endpoint
  - Create DELETE /api/expenses/{id} endpoint
  - Add proper HTTP status codes and error handling
  - _Requirements: 2.3, 2.4, 2.5, 2.6, 2.7, 8.1, 8.2_

- [x] 10. Implement expense approval workflow





  - Add method in ExpenseService to submit expense for approval (change status to SUBMITTED)
  - Add method to approve expense (manager only, change status to APPROVED)
  - Add method to reject expense (manager only, change status to REJECTED)
  - Add method to retrieve pending expenses for managers
  - Create POST /api/expenses/{id}/submit endpoint in ExpenseController
  - Create GET /api/expenses/pending endpoint (manager only)
  - Create POST /api/expenses/{id}/approve endpoint (manager only)
  - Create POST /api/expenses/{id}/reject endpoint (manager only)
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 11. Implement ReceiptService and ReceiptController





  - Create ReceiptService with file upload method (validate type and size)
  - Implement file storage logic (save to configured directory)
  - Implement method to retrieve receipt file by ID
  - Implement method to delete receipt file
  - Create ReceiptController with POST /api/receipts endpoint
  - Create GET /api/receipts/{id} endpoint to download file
  - Create DELETE /api/receipts/{id} endpoint
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 12. Implement ReportService and ReportController





  - Create ReportService with method to filter expenses by date range
  - Implement method to filter expenses by category
  - Implement method to filter expenses by status
  - Implement method to calculate total amounts for filtered results
  - Implement CSV export functionality
  - Create ReportController with GET /api/reports/summary endpoint
  - Create GET /api/reports/export endpoint with format parameter
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.7_

- [x] 13. Implement global exception handling





  - Create custom exception classes (ResourceNotFoundException, UnauthorizedException, etc.)
  - Create @ControllerAdvice class for global exception handling
  - Map exceptions to appropriate HTTP status codes
  - Return ErrorResponse DTOs with meaningful messages
  - _Requirements: 8.2, 8.4, 9.4_

- [x] 14. Add API documentation and health check





  - Configure SpringDoc OpenAPI for automatic API documentation
  - Create HealthController with GET /api/health endpoint
  - Add Swagger UI configuration
  - _Requirements: 8.6, 10.4_

- [x] 15. Create database migration scripts





  - Set up Flyway or Liquibase for database migrations
  - Create initial migration script for users table
  - Create migration script for categories table with seed data
  - Create migration script for expenses table with indexes
  - Create migration script for receipts table
  - _Requirements: 7.4, 7.5_

- [x] 16. Write backend unit and integration tests






  - Write unit tests for AuthService (registration, login, token generation)
  - Write unit tests for ExpenseService (CRUD operations, validation, authorization)
  - Write unit tests for ReportService (filtering, aggregation)
  - Write integration tests for AuthController endpoints
  - Write integration tests for ExpenseController endpoints
  - Write integration tests for security configuration
  - _Requirements: All backend requirements_

## Frontend Implementation

- [x] 17. Set up React project structure





  - Create React app using Create React App or Vite
  - Install dependencies: React Router, Axios, Material-UI or Bootstrap, React Hook Form, Chart.js
  - Create folder structure: pages, components, services, context, utils, styles
  - Configure environment variables for API URL
  - Set up Axios instance with base URL and interceptors
  - _Requirements: 9.1, 10.5_

- [x] 18. Implement authentication context and services





  - Create AuthContext for global authentication state
  - Create authService with register, login, logout, and token management functions
  - Implement token storage in localStorage or sessionStorage
  - Create Axios interceptor to add JWT token to requests
  - Create Axios interceptor to handle 401 responses and redirect to login
  - _Requirements: 1.5, 9.4_

- [x] 19. Create authentication pages





  - Create LoginPage component with email and password form
  - Implement form validation and error display
  - Create RegisterPage component with registration form
  - Add navigation between login and register pages
  - Implement redirect to dashboard after successful authentication
  - _Requirements: 1.1, 1.2, 1.5, 9.3, 9.4, 9.5, 9.6_

- [x] 20. Create shared UI components





  - Create Navbar component with navigation links and user menu
  - Create LoadingSpinner component
  - Create ErrorAlert component for displaying error messages
  - Create StatusBadge component for expense status display
  - Create ProtectedRoute component for route authorization
  - _Requirements: 9.3, 9.4, 9.5_

- [x] 21. Implement expense service and category service





  - Create expenseService with functions for all CRUD operations
  - Add functions for submitting, approving, and rejecting expenses
  - Create categoryService with function to fetch all categories
  - Implement error handling in service functions
  - _Requirements: 2.3, 2.4, 2.5, 3.2_

- [x] 22. Create DashboardPage with summary statistics






  - Create DashboardPage component
  - Fetch and display total expenses, pending expenses, approved expenses
  - Display recent expenses list
  - Add charts for expense breakdown by category (using Chart.js)
  - Implement responsive layout
  - _Requirements: 9.1, 9.2, 9.3_

- [x] 23. Create ExpenseListPage with filtering and sorting




  - Create ExpenseListPage component
  - Fetch and display all user expenses in a table or card layout
  - Implement pagination for expense list
  - Add filters for date range, category, and status
  - Add sorting options (by date, amount, status)
  - Add action buttons (edit, delete, submit) for each expense
  - Display expense status badges
  - _Requirements: 2.7, 5.6, 9.1, 9.3_

- [x] 24. Create ExpenseFormPage for creating and editing expenses





  - Create ExpenseFormPage component with form fields
  - Implement form validation using React Hook Form
  - Create CategorySelect dropdown component
  - Create FileUpload component for receipt attachment
  - Implement create expense functionality
  - Implement edit expense functionality (pre-populate form)
  - Add navigation back to expense list after save
  - _Requirements: 2.6, 3.4, 4.5, 9.3, 9.6_

- [x] 25. Implement receipt upload and display functionality




  - Integrate FileUpload component in ExpenseFormPage
  - Create receiptService with upload and download functions
  - Display receipt thumbnail or link in ExpenseListPage
  - Implement receipt preview/download functionality
  - Add file validation on frontend (type, size)
  - _Requirements: 4.1, 4.2, 4.5, 4.6_

- [x] 26. Create ApprovalPage for managers





  - Create ApprovalPage component (visible only to managers)
  - Fetch and display pending expenses
  - Add approve and reject buttons for each expense
  - Implement approval/rejection with optional notes
  - Update expense list after approval/rejection
  - Add role-based route protection
  - _Requirements: 5.4, 5.5, 5.6, 5.7_

- [x] 27. Create ReportPage with filtering and export





  - Create ReportPage component
  - Add date range picker for filtering
  - Add category filter dropdown
  - Add status filter dropdown
  - Fetch and display filtered expense report
  - Display total amount and expense count
  - Implement CSV export functionality
  - Add charts for visual representation of report data
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7_

- [x] 28. Implement responsive design and styling





  - Apply responsive CSS for mobile and desktop views
  - Ensure all forms are mobile-friendly
  - Test navigation on different screen sizes
  - Add loading states for all async operations
  - Ensure consistent styling across all pages
  - _Requirements: 9.1, 9.5_

- [x] 29. Write frontend unit and integration tests





  - Write unit tests for authentication service functions
  - Write unit tests for expense service functions
  - Write component tests for LoginPage
  - Write component tests for ExpenseFormPage
  - Write integration tests for complete user flows (login, create expense, submit)
  - Set up Mock Service Worker for API mocking in tests
  - _Requirements: All frontend requirements_

## Integration and Deployment

- [x] 30. Set up AWS RDS PostgreSQL database





  - Create RDS PostgreSQL instance in AWS
  - Configure security groups to allow backend access
  - Create database and user credentials
  - Test connection from local environment
  - _Requirements: 7.1, 7.2, 10.1, 10.2_

- [x] 31. Configure backend for production deployment





  - Create application-prod.yml with production settings
  - Set up environment variables for database credentials and JWT secret
  - Build production JAR file using Maven
  - Test health check endpoint
  - _Requirements: 10.1, 10.2, 10.3, 10.4_

- [x] 32. Configure frontend for production deployment







  - Create production build of React app
  - Configure production API URL
  - Test production build locally
  - _Requirements: 10.5_

- [x] 33. Create deployment documentation






  - Document backend deployment steps
  - Document frontend deployment steps
  - Document environment variable configuration
  - Document database setup and migration process
  - Create README with project overview and setup instructions
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_
