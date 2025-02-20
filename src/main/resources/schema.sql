CREATE TABLE IF NOT EXISTS products (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        description TEXT,
        price DECIMAL(10,2) NOT NULL,
        stock_quantity INTEGER NOT NULL,
        is_active BOOLEAN DEFAULT TRUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add some sample data
INSERT INTO products (name, description, price, stock_quantity) VALUES
        ('Laptop', 'High-performance laptop', 999.99, 50),
        ('Smartphone', 'Latest smartphone model', 599.99, 100),
        ('Headphones', 'Wireless noise-canceling headphones', 199.99, 75);
