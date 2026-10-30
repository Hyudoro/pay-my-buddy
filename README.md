# Pay My Buddy

[![CI](https://github.com/Hyudoro/pay-my-buddy/actions/workflows/ci.yml/badge.svg)](https://github.com/Hyudoro/pay-my-buddy/actions/workflows/ci.yml)

Pay My Buddy is a money transfer application between registered users: add
connections, send them money with a description, and follow balances and
transaction history. It has two faces over one service layer, a
server-rendered web interface and a JSON API, both behind the same
session-based login.

## Stack

Java 21, Spring Boot 3.5.11, Spring Security with session authentication,
Spring Data JPA, Flyway, Thymeleaf, PostgreSQL 16, Gradle. Tests use JUnit 5,
Mockito and Testcontainers.

## Running it

You need Docker and JDK 21.

Start PostgreSQL 16:

```sh
docker compose up -d
```

Run the app on http://localhost:8080:

```sh
DB_PASSWORD=changeme ./gradlew bootRun
```

The login page links to registration, so the first account can be created
from the browser. Integration tests need no setup: they start their own
PostgreSQL container.

## Web interface

| Page                  | Action                                            |
|-----------------------|---------------------------------------------------|
| /login, /register     | sign in, create an account                        |
| /transfer             | send money to a connection, see paged history     |
| /connections          | list and add connections by email                 |
| /profile              | update username or email, change password         |

## API

The same features as JSON, authenticated by the session cookie from
POST /api/auth/login.

| Request                        | Action                                   |
|--------------------------------|------------------------------------------|
| POST /api/auth/register        | create an account                        |
| POST /api/auth/login           | sign in, start the session               |
| POST /api/auth/logout          | end the session                          |
| GET /api/profile               | the current user's profile and balance   |
| PATCH /api/profile             | update username or email                 |
| GET /api/connections           | list the current user's connections      |
| POST /api/connections          | add a connection by email                |
| GET /api/transactions          | the user's transfers, paged, newest first|
| POST /api/transactions         | send money to a connection               |

## Data

Flyway creates the schema on first start, from
src/main/resources/db/migration. Three tables:

- users: UUID key, unique case-insensitive email, BCrypt password hash,
  decimal balance
- connections: pairs of users stored once in ascending order, enforced by a
  CHECK constraint, so no connection can exist twice or in both directions
- transactions: sender, receiver, description, strictly positive amount,
  newest first by creation time

![Physical schema of users, connections and transactions](https://github.com/user-attachments/assets/d15241e5-acf6-467e-b2ac-5676c84b83d5)

## Tests

Unit tests for the services, validators and utilities, and one integration
test class per service, running against a real PostgreSQL 16 started by
Testcontainers. CI runs the whole suite on every push and pull request to
main.
