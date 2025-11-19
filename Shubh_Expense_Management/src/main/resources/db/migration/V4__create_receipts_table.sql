-- Create receipts table
-- This table stores receipt file metadata linked to expenses
-- Requirements: 7.4, 7.5

CREATE TABLE receipts (
    id BIGSERIAL PRIMARY KEY,
    expense_id BIGINT UNIQUE NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on expense_id for faster lookups
CREATE INDEX idx_receipts_expense_id ON receipts(expense_id);
