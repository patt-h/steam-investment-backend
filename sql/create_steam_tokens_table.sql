CREATE TABLE steam_tokens (
    id SERIAL PRIMARY KEY,
    token VARCHAR(1024) NOT NULL,
    expiration_date TIMESTAMP NOT NULL
);