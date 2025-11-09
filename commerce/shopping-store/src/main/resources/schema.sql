CREATE TYPE IF NOT EXISTS enum_quantity_state AS ENUM ('ENDED', 'FEW', 'ENOUGH', 'MANY');
CREATE TYPE IF NOT EXISTS enum_product_state AS ENUM ('ACTIVE', 'DEACTIVATE');
CREATE TYPE IF NOT EXISTS enum_product_category AS ENUM ('LIGHTING', 'CONTROL', 'SENSORS');

CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(36) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(512),
    quantity_state enum_quantity_state NOT NULL,
    product_state enum_product_state NOT NULL DEFAULT 'ACTIVE',
    product_category enum_product_category NOT NULL,
    price NUMERIC(19,2) NOT NULL CHECK (price >= 1)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_products_category ON products(product_category);
