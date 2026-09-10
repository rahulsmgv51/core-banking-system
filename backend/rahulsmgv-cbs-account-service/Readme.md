# RahulSMGV CBS Account Service

The **RahulSMGV CBS Account Service** manages customer bank accounts within the RahulSMGV Core Banking System.

The service follows a **Domain-Driven Design (DDD)** and **Hexagonal Architecture (Ports and Adapters)** approach and provides REST APIs for account creation, retrieval, and lifecycle management.

It is designed as an independent backend service that will integrate with other CBS services such as Customer, Transaction, Ledger, and GL services.

---

## Overview

### Technology Stack

* Java 21
* Spring Boot 3.5.x
* Spring Web
* Spring Validation
* Spring Data JPA
* PostgreSQL 15
* Maven 3.9+
* Podman for local infrastructure

### Core Capabilities

* Create bank accounts
* Retrieve account by account ID
* Retrieve account by account number
* Activate accounts
* Freeze active accounts
* Mark active accounts as dormant
* Close accounts
* Validate account lifecycle transitions
* Prevent duplicate customer/account-type combinations
* Generate unique account IDs
* Generate unique account numbers
* Persist account state in PostgreSQL

### Account ID

Account IDs are generated as **11-digit numeric values**.

The sequence starts at:

```text
10000000001
```

The database column is mapped to:

```text
BIGINT
```

### Account Number

Account numbers are generated as unique account references.

Example:

```text
ACC-10000000001
```

Account numbers are stored as:

```text
VARCHAR(30)
```

---

# Architecture

The service follows a layered DDD / Hexagonal Architecture.

```text
Controller
    │
    ▼
Application Service
    │
    ▼
Domain Model
    │
    ▼
Application Ports
    │
    ▼
Infrastructure Adapters
    │
    ▼
PostgreSQL
```

The main packages are organized as:

```text
com.rahulsmgv.cbs.account
├── api
├── application
│   ├── dto
│   ├── port
│   └── service
├── controller
├── domain
│   ├── enums
│   ├── model
│   └── valueobject
├── exception
└── infrastructure
    └── persistence
        └── jpa
            ├── adapter
            ├── entity
            ├── mapper
            └── repository
```

---

# Prerequisites

Before running the service, make sure the following are installed and available:

* Java 21 or newer
* Maven 3.9+
* PostgreSQL 15
* Podman, if PostgreSQL is running in a container

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

Verify Podman:

```bash
podman --version
```

---

# Local PostgreSQL Setup

The service requires a PostgreSQL database.

The default datasource configuration is:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cbs
spring.datasource.username=cbs_user
spring.datasource.password=cbs_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

## Using Podman

Check whether the PostgreSQL container is running:

```bash
podman ps
```

If the container exists but is stopped:

```bash
podman start cbs-postgres
```

---

# Database Schema

The Account Service uses a numeric account ID model.

The expected database schema is:

```text
account_id       BIGINT
customer_id      BIGINT
account_number   VARCHAR(30)
account_type     VARCHAR(30)
status           VARCHAR(30)
currency_code    VARCHAR(10)
created_at       TIMESTAMPTZ
updated_at       TIMESTAMPTZ
```

## Reset Legacy UUID Schema

If the database contains stale legacy UUID data or an old UUID-based `accounts` table, reset the account schema using:

```bash
podman exec -i cbs-postgres psql -h localhost -U cbs_user -d cbs <<'SQL'

DROP TABLE IF EXISTS public.accounts;

DROP SEQUENCE IF EXISTS public.account_id_sequence;

DROP SEQUENCE IF EXISTS public.account_number_sequence;

CREATE SEQUENCE public.account_id_sequence
    START WITH 10000000001
    INCREMENT BY 1
    MINVALUE 10000000001
    MAXVALUE 99999999999
    NO CYCLE;

CREATE TABLE public.accounts (
    account_id BIGINT PRIMARY KEY NOT NULL
        DEFAULT nextval('public.account_id_sequence'),

    customer_id BIGINT NOT NULL,

    account_number VARCHAR(30) NOT NULL,

    account_type VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL,

    currency_code VARCHAR(10) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_account_number
        UNIQUE (account_number),

    CONSTRAINT uk_customer_account_type
        UNIQUE (customer_id, account_type)
);

CREATE INDEX idx_account_customer_id
    ON public.accounts (customer_id);

CREATE INDEX idx_account_status
    ON public.accounts (status);

SQL
```

> **Warning:** This operation drops the existing `accounts` table and its data. Use it only for local development/test environments.

---

# Running the Service

Navigate to the Account Service:

```bash
cd /path/to/core-banking-system/backend/rahulsmgv-cbs-account-service
```

Start the application:

```bash
mvn spring-boot:run
```

The default application port is:

```text
8080
```

Health check:

```bash
curl -i http://localhost:8080/actuator/health
```

---

# API Endpoints

Base path:

```text
/api/v1/accounts
```

---

## 1. Create Account

### Endpoint

```text
POST /api/v1/accounts
```

### Request

```bash
curl -i -X POST "http://localhost:8080/api/v1/accounts" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 10000000001,
    "accountType": "SAVINGS",
    "currency": "INR"
  }'
```

### Expected Response

```text
201 Created
```

Example:

```json
{
  "accountId": 10000000001,
  "customerId": 10000000001,
  "accountNumber": "ACC-10000000001",
  "accountType": "SAVINGS",
  "status": "PENDING",
  "currency": "INR",
  "createdAt": "2026-08-29T12:59:24.000Z",
  "updatedAt": "2026-08-29T12:59:24.000Z"
}
```

New accounts are initially created with:

```text
PENDING
```

---

# 2. Get Account by ID

### Endpoint

```text
GET /api/v1/accounts/{accountId}
```

### Example

```bash
curl -i \
  "http://localhost:8080/api/v1/accounts/10000000001"
```

### Expected Response

```text
200 OK
```

---

# 3. Get Account by Account Number

### Endpoint

```text
GET /api/v1/accounts/number/{accountNumber}
```

### Example

```bash
curl -i \
  "http://localhost:8080/api/v1/accounts/number/ACC-10000000001"
```

### Expected Response

```text
200 OK
```

---

# 4. Activate Account

### Endpoint

```text
POST /api/v1/accounts/{accountId}/activate
```

### Example

```bash
curl -i -X POST \
  "http://localhost:8080/api/v1/accounts/10000000001/activate"
```

### Expected Response

```text
200 OK
```

Valid activation transitions:

```text
PENDING  → ACTIVE
DORMANT  → ACTIVE
```

The following transitions are rejected:

```text
FROZEN   → ACTIVE
CLOSED   → ACTIVE
ACTIVE   → ACTIVE
```

A frozen account must use the dedicated unfreeze business operation if/when exposed by the API.

---

# 5. Freeze Account

### Endpoint

```text
POST /api/v1/accounts/{accountId}/freeze
```

### Example

```bash
curl -i -X POST \
  "http://localhost:8080/api/v1/accounts/10000000001/freeze"
```

### Expected Response

```text
200 OK
```

Valid transition:

```text
ACTIVE → FROZEN
```

Other source states are rejected.

---

# 6. Mark Account Dormant

### Endpoint

```text
POST /api/v1/accounts/{accountId}/dormant
```

### Example

```bash
curl -i -X POST \
  "http://localhost:8080/api/v1/accounts/10000000001/dormant"
```

### Expected Response

```text
200 OK
```

Valid transition:

```text
ACTIVE → DORMANT
```

Other source states are rejected.

---

# 7. Close Account

### Endpoint

```text
POST /api/v1/accounts/{accountId}/close
```

### Example

```bash
curl -i -X POST \
  "http://localhost:8080/api/v1/accounts/10000000001/close"
```

### Expected Response

```text
200 OK
```

Valid transitions include:

```text
ACTIVE  → CLOSED
FROZEN  → CLOSED
DORMANT → CLOSED
```

The following transitions are rejected:

```text
PENDING → CLOSED
CLOSED  → CLOSED
```

---

# Complete API List

| Method | Endpoint                                  | Purpose               |
| ------ | ----------------------------------------- | --------------------- |
| POST   | `/api/v1/accounts`                        | Create account        |
| GET    | `/api/v1/accounts/{accountId}`            | Get account by ID     |
| GET    | `/api/v1/accounts/number/{accountNumber}` | Get account by number |
| POST   | `/api/v1/accounts/{accountId}/activate`   | Activate account      |
| POST   | `/api/v1/accounts/{accountId}/freeze`     | Freeze account        |
| POST   | `/api/v1/accounts/{accountId}/dormant`    | Mark account dormant  |
| POST   | `/api/v1/accounts/{accountId}/close`      | Close account         |

---

# Account Lifecycle

The current account lifecycle is:

```text
                 ┌──────────────┐
                 │   PENDING    │
                 └──────┬───────┘
                        │ activate
                        ▼
                 ┌──────────────┐
          ┌──────│    ACTIVE    │──────┐
          │      └──────────────┘      │
          │ freeze              dormant│
          ▼                            ▼
   ┌──────────────┐             ┌──────────────┐
   │    FROZEN    │             │   DORMANT    │
   └──────┬───────┘             └──────┬───────┘
          │                             │
          │ close                       │ close
          └──────────────┬──────────────┘
                         ▼
                  ┌──────────────┐
                  │    CLOSED    │
                  └──────────────┘
```

### Lifecycle Rules

| Current Status | Operation               | Result   |
| -------------- | ----------------------- | -------- |
| PENDING        | Activate                | ACTIVE   |
| ACTIVE         | Freeze                  | FROZEN   |
| ACTIVE         | Dormant                 | DORMANT  |
| ACTIVE         | Close                   | CLOSED   |
| DORMANT        | Activate                | ACTIVE   |
| DORMANT        | Close                   | CLOSED   |
| FROZEN         | Close                   | CLOSED   |
| CLOSED         | Any lifecycle operation | Rejected |
| PENDING        | Freeze                  | Rejected |
| PENDING        | Dormant                 | Rejected |
| PENDING        | Close                   | Rejected |
| FROZEN         | Activate directly       | Rejected |
| FROZEN         | Dormant                 | Rejected |
| DORMANT        | Freeze                  | Rejected |

---

# Supported Account Types

```text
SAVINGS
CURRENT
FIXED_DEPOSIT
RECURRING_DEPOSIT
```

---

# Supported Currencies

The current API supports currency values such as:

```text
INR
```

The currency is represented using the service's `Currency` value object.

Additional currencies can be introduced as the CBS expands.

---

# Supported Account Status Values

```text
PENDING
ACTIVE
DORMANT
FROZEN
CLOSED
```

---

# Business Rules

## Unique Customer + Account Type

A customer cannot create multiple accounts of the same account type.

For example:

```text
Customer: 10000000001
Account Type: SAVINGS
```

A second SAVINGS account for the same customer is rejected.

Different account types remain independently possible:

```text
Customer 10000000001
├── SAVINGS
├── CURRENT
└── FIXED_DEPOSIT
```

---

## Unique Account Number

Every account number must be unique.

The database enforces this using:

```text
uk_account_number
```

---

## Account ID

Account IDs are generated by the Account Service and are not supplied by the API client.

The database uses:

```text
BIGINT
```

with the sequence beginning at:

```text
10000000001
```

---

# Error Handling

The service provides centralized exception handling through `GlobalExceptionHandler`.

Important error categories include:

| HTTP Status | Error                      | Meaning                      |
| ----------- | -------------------------- | ---------------------------- |
| 400         | `INVALID_REQUEST`          | Invalid request data         |
| 404         | `ACCOUNT_NOT_FOUND`        | Account does not exist       |
| 409         | `ACCOUNT_ALREADY_EXISTS`   | Duplicate account            |
| 422         | `INVALID_ACCOUNT_STATE`    | Invalid lifecycle transition |
| 422         | `ACCOUNT_OPERATION_FAILED` | Account operation failed     |
| 500         | `INTERNAL_SERVER_ERROR`    | Unexpected server error      |

The API error response also supports correlation/trace identification through:

```text
X-Correlation-Id
```

or:

```text
traceId
```

---

# Recommended Test Flows

## New Account Flow

```text
1. Create account
2. Get account by ID
3. Get account by account number
4. Activate account
5. Verify ACTIVE status
```

## Freeze Flow

```text
1. Create account
2. Activate account
3. Freeze account
4. Verify FROZEN status
```

## Dormant Flow

```text
1. Create account
2. Activate account
3. Mark account dormant
4. Verify DORMANT status
5. Activate account again
6. Verify ACTIVE status
```

## Close Flow

```text
1. Create account
2. Activate account
3. Close account
4. Verify CLOSED status
```

## Invalid Lifecycle Flow

Test invalid operations such as:

```text
PENDING  → FROZEN
PENDING  → DORMANT
PENDING  → CLOSED
ACTIVE   → ACTIVE
FROZEN   → ACTIVE
FROZEN   → DORMANT
DORMANT  → FROZEN
CLOSED   → ACTIVE
CLOSED   → FROZEN
CLOSED   → DORMANT
CLOSED   → CLOSED
```

---

# Load Test Data

Sample account records can be loaded for local testing using:

```bash
cd /path/to/core-banking-system/backend/rahulsmgv-cbs-account-service

podman exec -i cbs-postgres psql \
  -h localhost \
  -U cbs_user \
  -d cbs \
  < scripts/load-test-data.sql
```

The script:

* Clears existing `public.accounts` rows
* Resets the account ID sequence
* Resets the account number sequence
* Inserts sample accounts
* Links accounts to customer IDs from the Customer Service dataset

Example account numbers:

```text
ACC-10000000001
ACC-10000000002
ACC-10000000003
ACC-10000000004
```

Verify the loaded records:

```bash
podman exec -i cbs-postgres psql \
  -h localhost \
  -U cbs_user \
  -d cbs \
  -c "SELECT account_id, customer_id, account_number, account_type, status, currency_code FROM public.accounts ORDER BY account_id;"
```

---

# Testing

Run the complete test suite:

```bash
mvn test
```

The current verified test suite:

```text
AccountApplicationServiceTest              10/10 PASS
AccountControllerIntegrationTest            9/9 PASS
AccountTest                                 20/20 PASS
AccountIdTest                                4/4 PASS
AccountNumberTest                            5/5 PASS
CurrencyTest                                  6/6 PASS
CustomerIdTest                                4/4 PASS
AccountRepositoryAdapterIntegrationTest       7/7 PASS
------------------------------------------------------
TOTAL                                        65/65 PASS
```

Current verification:

```text
Tests run: 65
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Run a clean verification:

```bash
mvn clean test
```

---

# Troubleshooting

## Application Fails to Start

Check:

```text
1. PostgreSQL container is running
2. Database credentials are correct
3. Database "cbs" exists
4. accounts table exists
5. account_id is BIGINT
6. No legacy UUID schema remains
```

Check the PostgreSQL container:

```bash
podman ps
```

---

## UUID / BIGINT Error

A common legacy-schema error is:

```text
column "account_id" is of type uuid but expression is of type bigint
```

This means the database still contains the old UUID-based account schema.

Reset the local `accounts` table using the database reset script described above.

---

## Port Already in Use

If port `8080` is already occupied:

```bash
ss -ltnp | grep :8080
```

Alternatively, change the application port in:

```text
src/main/resources/application.properties
```

For example:

```properties
server.port=8081
```

---

## Duplicate Account Creation

Duplicate customer/account-type combinations are intentionally rejected.

Example:

```text
Customer ID: 10000000001
Account Type: SAVINGS
```

Attempting to create another SAVINGS account for the same customer results in:

```text
409 Conflict
```

Duplicate account numbers are also rejected.

---

# Database Schema Verification

Use the following command to inspect the live database schema:

```bash
podman exec -i cbs-postgres psql \
  -h localhost \
  -U cbs_user \
  -d cbs \
  -c "SELECT column_name, data_type FROM information_schema.columns WHERE table_name='accounts' ORDER BY ordinal_position;"
```

Expected output includes:

```text
account_id | bigint
```

The important requirement is:

```text
account_id = BIGINT
```

---

# Useful Development Commands

## Run application

```bash
mvn spring-boot:run
```

## Run tests

```bash
mvn test
```

## Clean and test

```bash
mvn clean test
```

## Check application health

```bash
curl -i http://localhost:8080/actuator/health
```

## Check database container

```bash
podman ps
```

## Inspect account table

```bash
podman exec -i cbs-postgres psql \
  -h localhost \
  -U cbs_user \
  -d cbs \
  -c "SELECT * FROM public.accounts ORDER BY account_id;"
```

---

# Relationship with Other CBS Services

The Account Service is responsible for **account ownership and account lifecycle**.

It should not become responsible for transaction processing or accounting.

The intended high-level CBS flow is:

```text
Customer Service
       │
       ▼
Account Service
       │
       ▼
Transaction Service
       │
       ▼
Ledger Service
       │
       ▼
GL Service
```

Responsibilities remain separated:

```text
Customer Service
    → Customer identity and customer lifecycle

Account Service
    → Bank account and account lifecycle

Transaction Service
    → Financial transaction orchestration

Ledger Service
    → Double-entry accounting records

GL Service
    → General ledger and financial reporting
```

This separation allows the Account Service to remain focused on account management while financial posting is handled by the appropriate downstream services.

---

# Current Development Status

```text
Service: RahulSMGV CBS Account Service

Status: COMPLETED

Java: 21
Spring Boot: 3.5.4
Database: PostgreSQL 15

Tests:
65/65 PASS

Failures: 0
Errors: 0
Skipped: 0
```

The service is considered complete for the current Account Service scope.

---

# Summary

The RahulSMGV CBS Account Service provides the core account-management capability of the Core Banking System.

The service currently supports:

* Account creation
* Account retrieval
* Unique account numbering
* Numeric BIGINT account IDs
* Account lifecycle management
* Account state validation
* Duplicate-account protection
* PostgreSQL persistence
* REST APIs
* Centralized error handling
* Domain and integration testing

The service is now ready to act as the account-management foundation for the next CBS capability: **Transaction Service**.