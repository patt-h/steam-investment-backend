CREATE TABLE item_history (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL,
    date DATE NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (item_id) REFERENCES item_names(id)
);