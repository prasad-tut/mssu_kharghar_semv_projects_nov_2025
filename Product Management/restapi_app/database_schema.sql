-- Product Management Database Schema
-- Create the database
CREATE DATABASE IF NOT EXISTS productdb;
USE productdb;

-- Create the product table
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

-- Sample data (optional)
INSERT INTO product (description, manufacturer, productType, manufacturedOn, expiryDate, unitPrice, tax) 
VALUES 
    ('Laptop Dell XPS 13', 'Dell Inc.', 'ELECTRONIC', '2024-01-15', '2026-01-15', 85000.00, 15300.00),
    ('Paracetamol 500mg', 'Sun Pharma', 'MEDICINE', '2024-03-10', '2025-03-10', 50.00, 2.50),
    ('Toyota Camry Hybrid', 'Toyota Motors', 'AUTOMOBILE', '2024-06-20', '2034-06-20', 2500000.00, 450000.00),
    ('Coca Cola 2L', 'Coca Cola Company', 'CONSUMABLE', '2024-11-01', '2025-05-01', 90.00, 4.50);
