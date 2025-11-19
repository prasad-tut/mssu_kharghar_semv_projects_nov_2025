-- Create the database
CREATE DATABASE Bug_tracker;

-- Use the database
USE Bug_tracker;

-- Create the bug table
CREATE TABLE bug (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    priority ENUM('HIGH', 'LOW', 'MEDIUM') NOT NULL,
    severity ENUM('CRITICAL', 'MARGINAL', 'NEGLIGIBLE') NOT NULL,
    detected_on DATE NOT NULL,
    assigned_to VARCHAR(255)
);


INSERT INTO bug (description, priority, severity, detected_on, assigned_to) VALUES
('Homepage 404 error on mobile devices', 'HIGH', 'CRITICAL', '2025-11-15', 'Sarah Lee'),
('Reset password link is broken', 'MEDIUM', 'MARGINAL', '2025-11-16', 'Tom Chen'),
('Misspelled label on the contact form', 'LOW', 'NEGLIGIBLE', '2025-11-17', NULL);