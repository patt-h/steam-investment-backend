CREATE TABLE user_settings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    goal_name VARCHAR(255),
    goal_item_id INT,
    currency VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (goal_item_id) REFERENCES item_names(id)
);