CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    market_hash_name VARCHAR(255) NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    quantity INT NOT NULL
);