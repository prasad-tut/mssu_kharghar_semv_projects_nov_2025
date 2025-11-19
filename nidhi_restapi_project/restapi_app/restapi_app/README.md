# Visitor Log Management System

A Spring Boot REST API application for managing visitor logs in an organization.

## Features

- Check-in and check-out visitors
- Track visitor information (name, contact, purpose, host)
- Multiple visitor types (Guest, Contractor, Vendor, Interview, Delivery)
- ID proof tracking
- Search visitors by name, host, or date range
- View currently checked-in visitors

## Technology Stack

- Java 21
- Spring Boot 3.5.5
- Spring Data JPA
- MySQL Database
- Maven

## Database Setup

1. Create the database:
```bash
mysql -u root -p < database_schema.sql
```

Or manually:
```sql
CREATE DATABASE IF NOT EXISTS visitordb;
USE visitordb;
```

2. The application will auto-create tables on startup (spring.jpa.hibernate.ddl-auto=update)

## Configuration

Update `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/visitordb
spring.datasource.username=root
spring.datasource.password=root
```

## Running the Application

```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:9080`

## API Endpoints

### Check-in Visitor
```http
POST /api/visitors/checkin
Content-Type: application/json

{
  "visitorName": "John Doe",
  "contactNumber": "9876543210",
  "email": "john@example.com",
  "purpose": "Business Meeting",
  "hostName": "Alice Smith",
  "department": "Sales",
  "visitorType": "GUEST",
  "idProofType": "Driving License",
  "idProofNumber": "DL123456"
}
```

### Check-out Visitor
```http
PUT /api/visitors/checkout/{id}
```

### Get All Visitors
```http
GET /api/visitors
```

### Get Visitor by ID
```http
GET /api/visitors/{id}
```

### Get Currently Checked-in Visitors
```http
GET /api/visitors/current
```

### Get Visitors by Host
```http
GET /api/visitors/host/{hostName}
```

### Search Visitors by Name
```http
GET /api/visitors/search?name=John
```

### Get Visitors by Date Range
```http
GET /api/visitors/daterange?start=2024-11-15T00:00:00&end=2024-11-15T23:59:59
```

### Update Visitor
```http
PUT /api/visitors/{id}
Content-Type: application/json

{
  "visitorName": "John Doe Updated",
  "contactNumber": "9876543210",
  ...
}
```

### Delete Visitor
```http
DELETE /api/visitors/{id}
```

## Visitor Types

- `GUEST` - General visitors
- `CONTRACTOR` - Contract workers
- `VENDOR` - Vendor representatives
- `INTERVIEW` - Job interview candidates
- `DELIVERY` - Delivery personnel

## Project Structure

```
src/main/java/mssu/in/restapi_app/
├── controller/
│   └── VisitorLogController.java
├── entity/
│   ├── VisitorLog.java
│   └── VisitorType.java
├── repository/
│   └── VisitorLogRepository.java
├── service/
│   ├── VisitorLogService.java
│   └── VisitorLogServiceImpl.java
├── exception/
│   └── ResourceNotFoundException.java
└── RestapiAppApplication.java
```

## Frontend Interface

The application includes a modern web interface accessible at:
```
http://localhost:9080
```

### Features:
- ✅ Check-in visitors with complete details
- 🔍 Search visitors by name
- 📋 View all visitors or currently checked-in only
- ⏰ Check-out visitors
- 🗑️ Delete visitor records
- 📱 Fully responsive design

## Testing with cURL

Example with cURL:
```bash
curl -X POST http://localhost:9080/api/visitors/checkin \
  -H "Content-Type: application/json" \
  -d '{"visitorName":"John Doe","contactNumber":"9876543210","purpose":"Meeting","hostName":"Alice","visitorType":"GUEST"}'
```
