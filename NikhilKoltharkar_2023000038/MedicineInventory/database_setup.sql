-- Medicine Inventory Management System
-- Database Setup Script

-- Create database
CREATE DATABASE IF NOT EXISTS medicine_inventory;

-- Use the database
USE medicine_inventory;

-- Create medicines table
CREATE TABLE medicines (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    medicine_name VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    expiry_date DATE NOT NULL,
    category VARCHAR(50) NOT NULL
);

-- Insert sample data
INSERT INTO medicines (medicine_name, manufacturer, quantity, price, expiry_date, category) 
VALUES 
('Paracetamol', 'PharmaCorp', 500, 5.50, '2025-12-31', 'Pain Relief'),
('Amoxicillin', 'MediLife', 200, 15.75, '2026-06-30', 'Antibiotic'),
('Vitamin C', 'HealthPlus', 1000, 8.25, '2027-03-15', 'Supplement');
commit;  --For saving the inserted data even if you close the app(For MYSQL Workbench 8.0).

-- Verify data
SELECT * FROM medicines;