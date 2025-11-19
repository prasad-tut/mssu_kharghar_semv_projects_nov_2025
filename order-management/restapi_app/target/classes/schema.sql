CREATE TABLE IF NOT EXISTS orders (
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

