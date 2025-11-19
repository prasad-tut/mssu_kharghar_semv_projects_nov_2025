-- Visitor Log Management Database Schema
-- Create the database
CREATE DATABASE IF NOT EXISTS visitordb;
USE visitordb;

-- Create the visitor_log table
CREATE TABLE IF NOT EXISTS visitor_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    visitor_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    purpose VARCHAR(200) NOT NULL,
    host_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    check_in_time DATETIME NOT NULL,
    check_out_time DATETIME,
    visitor_type VARCHAR(20) NOT NULL 
        CHECK (visitor_type IN ('GUEST', 'CONTRACTOR', 'VENDOR', 'INTERVIEW', 'DELIVERY')),
    id_proof_type VARCHAR(50),
    id_proof_number VARCHAR(50),
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sample data (optional)
INSERT INTO visitor_log (visitor_name, contact_number, email, purpose, host_name, department, 
                         check_in_time, check_out_time, visitor_type, id_proof_type, id_proof_number) 
VALUES 
    ('John Doe', '9876543210', 'john@example.com', 'Business Meeting', 'Alice Smith', 'Sales', 
     '2024-11-15 09:00:00', '2024-11-15 11:30:00', 'GUEST', 'Driving License', 'DL123456'),
    ('Jane Wilson', '9876543211', 'jane@example.com', 'Job Interview', 'Bob Johnson', 'HR', 
     '2024-11-15 10:00:00', '2024-11-15 12:00:00', 'INTERVIEW', 'Passport', 'P987654'),
    ('Mike Brown', '9876543212', 'mike@vendor.com', 'Equipment Delivery', 'Carol White', 'IT', 
     '2024-11-15 14:00:00', NULL, 'VENDOR', 'Aadhar Card', 'AADH123456789');
