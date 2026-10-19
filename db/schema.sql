-- =====================================================================
-- Base de datos: prueba (PostgreSQL)
-- Crear primero la base:  CREATE DATABASE prueba;
-- Luego ejecutar este script conectado a la base "prueba".
-- Nota: la aplicación también crea las tablas automáticamente
-- (spring.jpa.hibernate.ddl-auto=update) y carga data.sql al arrancar.
-- =====================================================================

CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(6)   PRIMARY KEY CHECK (product_id ~ '^[0-9]{6}$'),
    name       VARCHAR(100) NOT NULL,
    type       VARCHAR(10)  NOT NULL CHECK (type IN ('DEBIT', 'CREDIT'))
);

CREATE TABLE IF NOT EXISTS cards (
    card_id        UUID          PRIMARY KEY,
    card_number    VARCHAR(16)   NOT NULL,
    product_id     VARCHAR(6)    NOT NULL,
    holder_name    VARCHAR(100)  NOT NULL,
    issued_at      DATE          NOT NULL,
    expires_at     DATE          NOT NULL,
    status         VARCHAR(10)   NOT NULL CHECK (status IN ('ISSUED', 'ACTIVE', 'BLOCKED')),
    blocked_at     TIMESTAMP,
    blocked_reason VARCHAR(255),
    balance        NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    currency       VARCHAR(3)    NOT NULL DEFAULT 'USD',
    CONSTRAINT uk_cards_card_number UNIQUE (card_number),
    CONSTRAINT fk_cards_product FOREIGN KEY (product_id) REFERENCES products (product_id)
);

-- Productos de prueba
INSERT INTO products (product_id, name, type) VALUES
    ('123456', 'Tarjeta Debito Clasica', 'DEBIT'),
    ('654321', 'Tarjeta Credito Gold', 'CREDIT')
ON CONFLICT (product_id) DO NOTHING;
