INSERT INTO orders (
    customer_name,
    customer_contact,
    description,
    order_date,
    shipping_date,
    delivery_date,
    status,
    delivery_address,
    quantity,
    total_price,
    payment_type,
    order_notes
) VALUES
('Ava Johnson', '555-1200', '50 premium gadget components', '2025-11-01', '2025-11-02', '2025-11-08', 'dispatched', '221B Baker Street, London', 50, 12500.00, 'paid', 'Expedite delivery'),
('Liam Patel', '555-2255', 'Bulk wellness kits', '2025-11-03', NULL, NULL, 'pending', '742 Evergreen Terrace, Springfield', 80, 18400.00, 'COD', 'Customer will confirm shipping date'),
('Sofia Chen', '555-7788', 'Custom apparel order', '2025-10-28', '2025-10-30', '2025-11-05', 'delivered', '1600 Amphitheatre Parkway, Mountain View', 120, 27600.00, 'paid', 'Delivered to reception');

