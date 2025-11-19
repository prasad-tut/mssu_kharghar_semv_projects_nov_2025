-- Create expenses table with indexes
-- This table stores expense records with approval workflow support
-- Requirements: 7.4, 7.5

CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount DECIMAL(10, 2) NOT NULL,
    expense_date DATE NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by BIGINT REFERENCES users(id),
    review_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_amount CHECK (amount > 0)
);

-- Create indexes for better query performance
-- Index on user_id for filtering expenses by user
CREATE INDEX idx_expenses_user_id ON expenses(user_id);

-- Index on status for filtering by approval status
CREATE INDEX idx_expenses_status ON expenses(status);

-- Index on expense_date for date range queries and sorting
CREATE INDEX idx_expenses_date ON expenses(expense_date);

-- Index on category_id for filtering by category
CREATE INDEX idx_expenses_category_id ON expenses(category_id);

-- Composite index for common query patterns (user + status)
CREATE INDEX idx_expenses_user_status ON expenses(user_id, status);

-- Composite index for date range queries by user
CREATE INDEX idx_expenses_user_date ON expenses(user_id, expense_date);
