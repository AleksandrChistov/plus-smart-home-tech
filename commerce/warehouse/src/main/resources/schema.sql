CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id VARCHAR(36) PRIMARY KEY, -- UUID format
    fragile BOOLEAN DEFAULT FALSE,
    width NUMERIC(5,2) NOT NULL CHECK (width >= 1),
    height NUMERIC(5,2) NOT NULL CHECK (height >= 1),
    depth NUMERIC(5,2) NOT NULL CHECK (depth >= 1),
    weight NUMERIC(4,2) NOT NULL CHECK (weight >= 1)
);

CREATE TABLE IF NOT EXISTS warehouse_stock (
    product_id VARCHAR(36) PRIMARY KEY, -- UUID format
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    FOREIGN KEY (product_id) REFERENCES warehouse_products(product_id)
);

CREATE TABLE IF NOT EXISTS warehouse_orders (
    order_id VARCHAR(36) PRIMARY KEY,
    delivery_id VARCHAR(36) UNIQUE
);

CREATE TABLE IF NOT EXISTS warehouse_orders_products (
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES warehouse_orders(order_id),
    FOREIGN KEY (product_id) REFERENCES warehouse_products(product_id)
);
