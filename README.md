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
