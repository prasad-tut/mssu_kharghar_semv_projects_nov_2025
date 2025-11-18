# Product Management System

## Overview
This project has been migrated from a Ticket Management System to a **Product Management System** based on the new database schema.

## Changes Made

### 1. Entity Layer
- **Created**: `Product.java` - Main entity with fields:
  - `id` (Integer, Primary Key, Auto-increment)
  - `description` (String, max 100 chars, not null)
  - `manufacturer` (String, max 100 chars, not null)
  - `productType` (ProductType enum, not null)
  - `manufacturedOn` (LocalDate, not null)
  - `expiryDate` (LocalDate, not null)
  - `unitPrice` (Double, not null, must be > 0)
  - `tax` (Double, not null, must be > 0)

- **Created**: `ProductType.java` - Enum with values:
  - CONSUMABLE
  - ELECTRONIC
  - AUTOMOBILE
  - MEDICINE

### 2. Repository Layer
- **Created**: `ProductRepository.java` with methods:
  - `getAllProducts()` - Retrieves all products
  - `addNewProduct(Product)` - Adds a new product
  - `editProduct(Product)` - Updates an existing product
  - `deleteProduct(Integer)` - Deletes a product by ID
  - `getProductById(Integer)` - Retrieves a single product by ID

### 3. Service Layer
- **Created**: `ProductService.java` with methods:
  - `getAllProducts()` - Business logic for retrieving all products
  - `addNewProduct(Product)` - Business logic for adding new product

### 4. Controller Layer
- **Created**: `ProductController.java` with endpoints:
  - `GET /products/get` - Get all products
  - `POST /products/add` - Add a new product

### 5. Configuration
- **Updated**: `application.properties`
  - Changed database from `ticketdb` to `productdb`
  - Connection URL: `jdbc:mysql://localhost:3306/productdb`

### 6. Database Schema
- **Created**: `database_schema.sql` - Contains the complete database setup script

## Database Setup

### Step 1: Create the Database
```sql
CREATE DATABASE IF NOT EXISTS productdb;
USE productdb;
```

### Step 2: Create the Product Table
```sql
CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    productType VARCHAR(10) NOT NULL 
        CHECK (productType IN ('CONSUMABLE', 'ELECTRONIC', 'AUTOMOBILE', 'MEDICINE')),
    manufacturedOn DATE NOT NULL,
    expiryDate DATE NOT NULL,
    unitPrice DOUBLE NOT NULL CHECK (unitPrice > 0),
    tax DOUBLE NOT NULL CHECK (tax > 0)
);
```

### Step 3: Run the SQL Script
Execute the `database_schema.sql` file in your MySQL database:
```bash
mysql -u root -p < database_schema.sql
```

## Running the Application

1. **Ensure MySQL is running** on localhost:3306
2. **Create the database** using the SQL script provided
3. **Update credentials** in `application.properties` if needed:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=root
   ```
4. **Run the Spring Boot application**:
   ```bash
   mvn spring-boot:run
   ```
5. **Access the API** at `http://localhost:9080`

## API Endpoints

### Get All Products
```http
GET http://localhost:9080/products/get
```

**Response Example**:
```json
[
  {
    "id": 1,
    "description": "Laptop Dell XPS 13",
    "manufacturer": "Dell Inc.",
    "productType": "ELECTRONIC",
    "manufacturedOn": "2024-01-15",
    "expiryDate": "2026-01-15",
    "unitPrice": 85000.00,
    "tax": 15300.00
  }
]
```

### Add New Product
```http
POST http://localhost:9080/products/add
Content-Type: application/json

{
  "description": "Samsung Galaxy S24",
  "manufacturer": "Samsung Electronics",
  "productType": "ELECTRONIC",
  "manufacturedOn": "2024-11-01",
  "expiryDate": "2026-11-01",
  "unitPrice": 75000.00,
  "tax": 13500.00
}
```

## Product Types

The system supports four product types:
1. **CONSUMABLE** - Food, beverages, and consumable goods
2. **ELECTRONIC** - Electronic devices and gadgets
3. **AUTOMOBILE** - Vehicles and automotive products
4. **MEDICINE** - Pharmaceutical products

## Sample Data

The `database_schema.sql` includes sample data for testing:
- Laptop Dell XPS 13 (ELECTRONIC)
- Paracetamol 500mg (MEDICINE)
- Toyota Camry Hybrid (AUTOMOBILE)
- Coca Cola 2L (CONSUMABLE)

## Notes

- All date fields use `LocalDate` type (YYYY-MM-DD format)
- Price fields (`unitPrice` and `tax`) must be greater than 0
- Product types are case-sensitive and must match enum values exactly
- The `id` field is auto-generated and should not be provided when creating new products

## Cleanup (Optional)

If you see old Ticket-related files in your IDE, you can safely delete:
- `Ticket.java`, `Priority.java`, `Severity.java`
- `TicketController.java`, `TicketService.java`, `TicketRepository.java`

The new Product-based files are the active ones.
