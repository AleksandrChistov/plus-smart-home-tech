CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL UNIQUE,
    delivery_state VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    from_country VARCHAR(100),
    from_city VARCHAR(100),
    from_street VARCHAR(200),
    from_house VARCHAR(50),
    from_flat VARCHAR(50),
    to_country VARCHAR(100),
    to_city VARCHAR(100),
    to_street VARCHAR(200),
    to_house VARCHAR(50),
    to_flat VARCHAR(50)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_delivery_state ON deliveries(delivery_state);
