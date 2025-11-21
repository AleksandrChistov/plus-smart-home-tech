CREATE TYPE IF NOT EXISTS enum_payment_status AS ENUM ('PENDING', 'SUCCESS', 'FAILED');

CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(36) PRIMARY KEY,
    product_cost DECIMAL(19,2) NOT NULL,
    delivery_cost DECIMAL(19,2) NOT NULL,
    tax_cost DECIMAL(19,2) NOT NULL,
    total_cost DECIMAL(19,2) NOT NULL,
    `status` enum_payment_status NOT NULL DEFAULT 'PENDING'
);

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);