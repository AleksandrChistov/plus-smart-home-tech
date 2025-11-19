CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    shopping_cart_id VARCHAR(36),
    payment_id VARCHAR(36),
    delivery_id VARCHAR(36),
    state VARCHAR(20) NOT NULL DEFAULT 'NEW',
    delivery_weight FLOAT,
    delivery_volume FLOAT,
    fragile BOOLEAN,
    total_price DECIMAL(19,2),
    delivery_price DECIMAL(19,2),
    product_price DECIMAL(19,2)
);

-- Create order_products table to store products in each order
CREATE TABLE IF NOT EXISTS order_products (
    order_id VARCHAR(36) REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_orders_username ON orders(username);
CREATE INDEX IF NOT EXISTS idx_order_products_order_id ON order_products(order_id);
