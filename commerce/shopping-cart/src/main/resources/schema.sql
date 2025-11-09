CREATE TYPE IF NOT EXISTS enum_shopping_cart_state AS ENUM ('ACTIVE', 'DEACTIVATE');

CREATE TABLE IF NOT EXISTS shopping_carts (
    cart_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    cart_state enum_shopping_cart_state NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS shopping_carts_products (
    cart_id VARCHAR(36) NOT NULL REFERENCES shopping_carts(cart_id) ON DELETE CASCADE,
    product_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity !> 0),
    PRIMARY KEY (cart_id, product_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_shopping_carts_username ON shopping_carts(username);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_products_cart_id ON shopping_carts_products(cart_id);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_products_product_id ON shopping_carts_products(product_id);