\c futurama
SET ROLE futurama_secrets;
SET search_path TO secrets, public;

CREATE TABLE customers (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    firstname TEXT,
    lastname TEXT,
    email TEXT NOT NULL,
    localized_labels JSONB
);
CREATE UNIQUE INDEX idx_customers_email_uniqueness ON customers(email);
