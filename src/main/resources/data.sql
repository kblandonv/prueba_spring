INSERT INTO products (product_id, name, type) VALUES
    ('123456', 'Tarjeta Debito Clasica', 'DEBIT'),
    ('654321', 'Tarjeta Credito Gold', 'CREDIT')
ON CONFLICT (product_id) DO NOTHING;
