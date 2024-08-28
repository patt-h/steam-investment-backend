CREATE TABLE user_items (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    market_hash_name VARCHAR(255) NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    quantity INT NOT NULL,
    item_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (item_id) REFERENCES item_names(id)
);
