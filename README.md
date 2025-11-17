# Course Management API

A RESTful API built with Spring Boot for managing courses. This application provides full CRUD operations for course management with a modern web interface.

## Features

- ✅ Create, Read, Update, and Delete courses
- ✅ RESTful API endpoints
- ✅ Input validation
- ✅ Global exception handling
- ✅ H2 in-memory database
- ✅ Modern responsive web UI
- ✅ Real-time course management

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **Java Version**: 17
- **Frontend**: HTML, CSS, JavaScript

### Dependencies
- Spring Web
- Spring Data JPA
- Spring Validation
- H2 Database
- Lombok

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven (or use the included Maven wrapper)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/Nimay-Jadhav/course-management-api.git
cd course-management-api
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

| Method | Endpoint           | Description              |
|--------|--------------------|--------------------------|
| POST   | /api/courses       | Create a new course      |
| GET    | /api/courses       | Get all courses          |
| GET    | /api/courses/{id}  | Get course by ID         |
| PUT    | /api/courses/{id}  | Update course            |
| DELETE | /api/courses/{id}  | Delete course            |

## API Usage Examples

### Create a Course
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Boot Fundamentals",
    "description": "Learn the basics of Spring Boot",
    "instructor": "John Doe",
    "duration": 40
  }'
```

### Get All Courses
```bash
curl http://localhost:8080/api/courses
```

### Get Course by ID
```bash
curl http://localhost:8080/api/courses/1
```

### Update a Course
```bash
curl -X PUT http://localhost:8080/api/courses/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Advanced Spring Boot",
    "description": "Deep dive into Spring Boot",
    "instructor": "Jane Smith",
    "duration": 60
  }'
```

### Delete a Course
```bash
curl -X DELETE http://localhost:8080/api/courses/1
```

## Web Interface

Access the web interface at `http://localhost:8080/` after starting the application.

The UI provides:
- Form to add new courses
- Grid view of all courses
- Edit and delete functionality
- Responsive design for mobile devices

## H2 Database Console

Access the H2 console at `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:coursedb`
- **Username**: `sa`
- **Password**: (leave empty)

## Project Structure

```
course-management-api/
├── src/
│   ├── main/
│   │   ├── java/com/coursemanagement/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/              # Data transfer objects
│   │   │   └── exception/        # Custom exceptions
│   │   └── resources/
│   │       ├── static/           # Frontend files
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Error Handling

The API returns appropriate HTTP status codes:

- `200 OK` - Successful GET/PUT requests
- `201 Created` - Successful POST requests
- `204 No Content` - Successful DELETE requests
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate course title
- `500 Internal Server Error` - Unexpected errors

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is open source and available under the MIT License.

## Author

Nimay Jadhav

## Acknowledgments

- Spring Boot Documentation
- Spring Data JPA
- H2 Database
