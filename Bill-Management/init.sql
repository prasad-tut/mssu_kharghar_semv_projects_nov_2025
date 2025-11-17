-- Initialize database schema for Bill Management System
CREATE TABLE IF NOT EXISTS bill (
    id SERIAL PRIMARY KEY,
    biller VARCHAR(255) NOT NULL,
    description TEXT,
    amount VARCHAR(50) NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paymentmode VARCHAR(50) NOT NULL,
    paymentstatus VARCHAR(50) NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_bill_status ON bill(paymentstatus);
CREATE INDEX IF NOT EXISTS idx_bill_date ON bill(bill_date);
CREATE INDEX IF NOT EXISTS idx_bill_mode ON bill(paymentmode);

-- Insert sample data for testing (optional)
INSERT INTO bill (biller, description, amount, paymentmode, paymentstatus) VALUES
('Electric Company', 'Monthly electricity bill', '150.00', 'CARD', 'COMPLETED'),
('Water Department', 'Water bill for November', '45.50', 'UPI', 'PENDING'),
('Internet Provider', 'Broadband service', '899.00', 'NETBANKING', 'COMPLETED'),
('Mobile Carrier', 'Phone bill', '299.00', 'UPI', 'PENDING'),
('Gas Company', 'Gas connection', '650.00', 'CASH', 'FAILED')
ON CONFLICT DO NOTHING;
