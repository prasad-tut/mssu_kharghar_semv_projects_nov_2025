# Order Management System

A comprehensive cloud-based Order Management System built with Spring Boot REST API and MySQL database. This project provides a complete solution for managing orders, tracking their status, and handling customer information efficiently.

## 👥 Team Members

This project was developed as part of a cloud computing course by:

- **Suyash Jadhav**
- **Chinmay Kudalkar**
- **Hrithik Chafe**
- **Hitesh Choudhary**

## 📋 Table of Contents

- [Team Members](#-team-members)
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Frontend](#frontend)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

The Order Management System is a cloud computing project designed to streamline order processing workflows. It enables businesses to efficiently manage customer orders, track order status, handle payments, and maintain comprehensive order records. The system provides both RESTful API endpoints for backend operations and a user-friendly web interface for frontend interactions.

## ✨ Features

- **Order Management**: Create, read, update, and delete orders
- **Status Tracking**: Track orders through various statuses (Pending, Processing, Shipped, Delivered, Cancelled)
- **Payment Management**: Support for multiple payment types (COD, Credit Card, Debit Card, UPI, Net Banking)
- **Timeline Filtering**: Filter orders by creation date, shipping date, or delivery date
- **Advanced Search**: Search orders by status, payment type, or timeline
- **Customer Information**: Store and manage customer details including name, contact, and delivery address
- **Order Notes**: Add and manage notes for each order
- **Responsive Web Interface**: Modern, user-friendly frontend for order management

## 🛠 Technology Stack

### Backend
- **Java 21**: Modern Java programming language
- **Spring Boot 3.5.5**: Framework for building enterprise applications
- **Spring Web**: RESTful web services
- **Spring JDBC**: Database connectivity
- **MySQL Connector/J 9.4.0**: MySQL database driver
- **Maven**: Dependency management and build tool

### Frontend
- **HTML5**: Structure and content
- **CSS3**: Styling and layout
- **JavaScript**: Client-side interactivity

### Database
- **MySQL**: Relational database management system

### Development Tools
- **Spring Boot DevTools**: Development-time tools for faster iteration

## 📁 Project Structure

```
orderdb1/
├── restapi_app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── mssu/in/restapi_app/
│   │   │   │       ├── controller/
│   │   │   │       │   ├── HomeController.java
│   │   │   │       │   └── OrderController.java
│   │   │   │       ├── dto/
│   │   │   │       │   └── OrderListResponse.java
│   │   │   │       ├── entity/
│   │   │   │       │   ├── Order.java
│   │   │   │       │   ├── OrderStatus.java
│   │   │   │       │   ├── OrderTimelineType.java
│   │   │   │       │   ├── PaymentType.java
│   │   │   │       │   ├── Priority.java
│   │   │   │       │   └── Severity.java
│   │   │   │       ├── repository/
│   │   │   │       │   └── OrderRepository.java
│   │   │   │       ├── service/
│   │   │   │       │   └── OrderService.java
│   │   │   │       └── RestapiAppApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── schema.sql
│   │   │       ├── data.sql
│   │   │       └── static/
│   │   │           ├── index.html
│   │   │           ├── css/
│   │   │           │   └── style.css
│   │   │           └── js/
│   │   │               └── app.js
│   │   └── test/
│   │       └── java/
│   │           └── mssu/in/restapi_app/
│   │               ├── OrderServiceTest.java
│   │               └── RestapiAppApplicationTests.java
│   └── pom.xml
└── README.md
```

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.6+** (or use the included Maven wrapper)
- **MySQL 8.0+** database server
- **Git** (for version control)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/chinmaykudalkar1/orderManagement.git
cd orderManagement
```

### 2. Database Setup

1. Start your MySQL server
2. Create a database (or let the application create it automatically):
   ```sql
   CREATE DATABASE order_management;
   ```
3. Update database credentials in `application.properties` (see Configuration section)

### 3. Build the Project

Using Maven wrapper (recommended):
```bash
cd restapi_app
./mvnw clean install
```

Or using system Maven:
```bash
cd restapi_app
mvn clean install
```

## ⚙️ Configuration

Edit `restapi_app/src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=9080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/order_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

# SQL Initialization
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
```

**Important**: Update the `spring.datasource.username` and `spring.datasource.password` with your MySQL credentials.

## 🔌 API Endpoints

### Order Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/orders/get` | Get all orders |
| `GET` | `/orders/get/{id}` | Get order by ID |
| `POST` | `/orders/add` | Create a new order |
| `PUT` | `/orders/edit` | Update an existing order |
| `DELETE` | `/orders/delete/{id}` | Delete an order by ID |
| `GET` | `/orders/status/{status}` | Get orders by status |
| `GET` | `/orders/payment/{paymentType}` | Get orders by payment type |
| `GET` | `/orders/timeline?type={type}&date={date}` | Get orders by timeline |

### Order Status Values
- `PENDING`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

### Payment Type Values
- `COD` (Cash on Delivery)
- `CREDIT_CARD`
- `DEBIT_CARD`
- `UPI`
- `NET_BANKING`

### Timeline Type Values
- `CREATED` - Filter by order creation date
- `SHIPPED` - Filter by shipping date
- `DELIVERED` - Filter by delivery date

### Example API Requests

#### Create Order
```bash
POST http://localhost:9080/orders/add
Content-Type: application/json

{
  "customerName": "John Doe",
  "customerContact": "+1234567890",
  "description": "Laptop Computer",
  "orderDate": "2024-01-15",
  "shippingDate": "2024-01-20",
  "deliveryDate": "2024-01-25",
  "status": "PENDING",
  "deliveryAddress": "123 Main St, City, State 12345",
  "quantity": 1,
  "totalPrice": 999.99,
  "paymentType": "CREDIT_CARD",
  "orderNotes": "Handle with care"
}
```

#### Get Orders by Status
```bash
GET http://localhost:9080/orders/status/PENDING
```

#### Get Orders by Timeline
```bash
GET http://localhost:9080/orders/timeline?type=CREATED&date=2024-01-15
```

## 🗄️ Database Schema

### Orders Table

```sql
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_contact VARCHAR(50),
    description TEXT NOT NULL,
    order_date DATE,
    shipping_date DATE,
    delivery_date DATE,
    status VARCHAR(32) NOT NULL DEFAULT 'pending',
    delivery_address TEXT NOT NULL,
    quantity INT,
    total_price DECIMAL(12, 2),
    payment_type VARCHAR(32) NOT NULL DEFAULT 'COD',
    order_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_orders_status (status),
    INDEX idx_orders_payment_type (payment_type),
    INDEX idx_orders_order_date (order_date),
    INDEX idx_orders_shipping_date (shipping_date),
    INDEX idx_orders_delivery_date (delivery_date)
);
```

The database schema includes indexes on frequently queried columns for optimal performance.

## 🎨 Frontend

The application includes a web-based frontend accessible at `http://localhost:9080/` after starting the server. The frontend provides:

- Order listing and management interface
- Form for creating new orders
- Order editing capabilities
- Filtering and search functionality
- Responsive design for various screen sizes

## ▶️ Running the Application

### Using Maven Wrapper

```bash
cd restapi_app
./mvnw spring-boot:run
```

### Using System Maven

```bash
cd restapi_app
mvn spring-boot:run
```

### Using IDE

1. Import the project into your IDE
2. Locate `RestapiAppApplication.java`
3. Run the main method

The application will start on `http://localhost:9080`

## 🧪 Testing

Run unit tests using Maven:

```bash
cd restapi_app
./mvnw test
```

Or using system Maven:

```bash
cd restapi_app
mvn test
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is part of an academic cloud computing course. Please refer to your institution's guidelines for usage and distribution.

## 📞 Support

For issues, questions, or contributions, please open an issue on the GitHub repository.

## 🔮 Future Enhancements

- User authentication and authorization
- Email notifications for order status updates
- Order analytics and reporting dashboard
- Integration with payment gateways
- Multi-tenant support
- REST API documentation with Swagger/OpenAPI
- Docker containerization
- Cloud deployment (AWS, Azure, GCP)

---

**Note**: This is a cloud computing project developed for educational purposes. Ensure proper security measures are implemented before deploying to production environments.
