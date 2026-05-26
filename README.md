# Pay My Buddy

Money transfer REST API built with Java 21 and Spring Boot.

## Prerequisites

- Java 21
- Docker

## Getting Started

Clone the repository

git clone git@github.com:Hyudoro/pay-my-buddy.git

cd pay-my-buddy

Start the database

docker compose up -d

Run the application

DB_PASSWORD=changeme ./gradlew bootRun


The API is available at http://localhost:8080.

## Stack
Java 21,Spring Boot 3.5.11, PostgreSQL 16, Spring Security, Spring DataJPA, Flyway.

## API

Authentication: POST /api/auth/register,
                POST /api/auth/login,
                POST /api/auth/logout

Profile: GET /api/profile,
         PATCH /api/profile,
         PATCH /api/profile/password


Connections: GET /api/connections,
             POST /api/connections


Transactions: GET /api/transactions,
              POST /api/transactions

Authenticated endpoints require a valid session cookie obtained from login.

## Data model 

-- UUID generation (pgcrypto available since PG 8.3)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";


CREATE EXTENSION IF NOT EXISTS "citext";

-- Teardown (order matters: dependents first)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS connections;
DROP TABLE IF EXISTS users;

-- Tables

CREATE TABLE users (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       username VARCHAR(50) NOT NULL,
       email CITEXT NOT NULL UNIQUE CHECK (char_length(email) <= 254), -- we enforce the rule at the DB lvl.
       password_hash TEXT NOT NULL,
       balance DECIMAL(15,2) NOT NULL DEFAULT 0,
       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE connections (
       user_id UUID NOT NULL REFERENCES users(id),
       connection_user_id UUID NOT NULL REFERENCES users(id),

       CONSTRAINT pk_connections
                   PRIMARY KEY (user_id, connection_user_id),

       CONSTRAINT chk_connections_ordering       -- Ensure data integrity.
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
INSERT INTO users (username, email, password_hash,balance)
VALUES ('alice', 'alice@gmail.com', 'aabbcc', 1000)
RETURNING id
),

bob AS (
INSERT INTO users (username, email,password_hash,balance)
VALUES ('bob', 'bob@gmail.com', 'ddeeff', 1000)
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

