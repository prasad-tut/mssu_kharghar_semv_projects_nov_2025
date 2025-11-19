-- Create categories table and insert predefined categories
-- This table stores expense categories for classification
-- Requirements: 7.4, 7.5

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert predefined expense categories as specified in Requirement 3.1
INSERT INTO categories (name, description, created_at) VALUES
    ('Travel', 'Transportation, accommodation, and travel-related expenses', CURRENT_TIMESTAMP),
    ('Meals', 'Food and beverage expenses including client meals', CURRENT_TIMESTAMP),
    ('Office Supplies', 'Stationery, office equipment, and supplies', CURRENT_TIMESTAMP),
    ('Equipment', 'Computer hardware, software, and technical equipment', CURRENT_TIMESTAMP),
    ('Other', 'Miscellaneous expenses not covered by other categories', CURRENT_TIMESTAMP);
