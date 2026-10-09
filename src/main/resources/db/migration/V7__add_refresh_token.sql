CREATE TABLE refresh_token(
    id UUID PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id)
)