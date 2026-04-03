-- PostgreSQL 16, Schema: public, strategy : Drop + recreate

-- UUID generation (pgcrypto available since PG 8.3)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Teardown (order matters: dependents first)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS connections;
DROP TABLE IF EXISTS users;

-- Tables

CREATE TABLE users (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       username VARCHAR(50) NOT NULL,
       email VARCHAR(255) NOT NULL UNIQUE, -- we enforce the rule od the DB lvl.
       password_hash TEXT NOT NULL
);

CREATE TABLE connections (
       user_id UUID NOT NULL REFERENCES users(id),
       connection_user_id UUID NOT NULL REFERENCES users(id),

       CONSTRAINT pk_connections
                   PRIMARY KEY (user_id, connection_user_id),

       CONSTRAINT chk_connections_ordering       -- Enusre data integrity.
                   CHECK (user_id < connection_user_id)
);

CREATE INDEX idx_connections_connection_user_id
       ON connections(connection_user_id);

-- transactions

CREATE TABLE transactions (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       sender_id UUID NOT NULL REFERENCES users(id),
       receiver_id UUID NOT NULL REFERENCES users(id),
       description TEXT,
       amount DECIMAL(15,2) NOT NULL,
       created_at TIMESTAMP NOT NULL DEFAULT NOW(),

       CONSTRAINT chk_transactions_amount_positive
                  CHECK (amount > 0)
);

-- seed data (CTE)

WITH

alice AS (
INSERT INTO users (username, email, password_hash)
VALUES ('alice', 'alice@gmail.com', 'aabbcc')
RETURNING id
),

bob AS (
INSERT INTO users (username, email,password_hash)
VALUES ('bob', 'bob@gmail.com', 'ddeeff')
RETURNING id
),

connection_alice_bob AS (
INSERT INTO connections (user_id, connection_user_id)
SELECT LEAST(alice.id, bob.id),
       GREATEST(alice.id, bob.id)
       FROM alice, bob
),

tx_alice_to_bob AS (
INSERT INTO transactions (sender_id, receiver_id, description, amount)
SELECT alice.id, bob.id, 'quick loan', 100.00
FROM alice, bob
),

tx_bob_to_alice AS (
INSERT INTO transactions (sender_id, receiver_id, description, amount)
SELECT bob.id, alice.id, 'loan repay', 100.00
FROM bob, alice
)

SELECT 'seed completed' AS status;
