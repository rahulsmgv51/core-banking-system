# RahulSMGV CBS Account Service

This service manages customer bank accounts for the Core Banking System. It follows the same structure and API style as the reference service in [rahulsmgv-cbs-customer-service/README.md](../rahulsmgv-cbs-customer-service/README.md).

## Overview

- Java 21
- Spring Boot 3.5.x
- Spring Web + Validation + Data JPA
- PostgreSQL 15
- Account IDs are generated as 11-digit numeric values starting at `10000000001`
- Account numbers are generated as unique account references
- The `accounts.account_id` column is mapped as `BIGINT`

## Prerequisites

Before running the service, make sure you have:

- Java 21 or newer
- Maven 3.9+
- PostgreSQL 15 running locally or via Podman

## Local PostgreSQL setup

A working local database is required for the application to start successfully.

### Using Podman

```bash
podman ps
podman start cbs-postgres
```

If the database has stale legacy UUID data, reset it to the expected numeric schema:

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
    account_id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('public.account_id_sequence'),
    customer_id BIGINT NOT NULL,
    account_number VARCHAR(30) NOT NULL,
    account_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_account_number UNIQUE (account_number),
    CONSTRAINT uk_customer_account_type UNIQUE (customer_id, account_type)
);

CREATE INDEX idx_account_customer_id ON public.accounts (customer_id);
CREATE INDEX idx_account_status ON public.accounts (status);
SQL
```

## Running the service

```bash
cd /home/os00570/core-banking-system/backend/rahulsmgv-cbs-account-service
mvn spring-boot:run
```

By default the app uses the datasource in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cbs
spring.datasource.username=cbs_user
spring.datasource.password=cbs_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

## API endpoints

Base path: `/api/v1/accounts`

### 1. Create account

`POST /api/v1/accounts`

```bash
curl -i -X POST "http://localhost:8080/api/v1/accounts" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 10000000001,
    "accountType": "SAVINGS",
    "currency": "INR"
  }'
```

Expected status: `201 Created`

Example response:

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

### 2. Get account by ID

`GET /api/v1/accounts/{accountId}`

```bash
curl -i "http://localhost:8080/api/v1/accounts/10000000001"
```

Expected status: `200 OK`

### 3. Get account by number

`GET /api/v1/accounts/number/{accountNumber}`

```bash
curl -i "http://localhost:8080/api/v1/accounts/number/ACC-10000000001"
```

Expected status: `200 OK`

### 4. Activate account

`POST /api/v1/accounts/{accountId}/activate`

```bash
curl -i -X POST "http://localhost:8080/api/v1/accounts/10000000001/activate"
```

Expected status: `200 OK`

### 5. Freeze account

`POST /api/v1/accounts/{accountId}/freeze`

```bash
curl -i -X POST "http://localhost:8080/api/v1/accounts/10000000001/freeze"
```

Expected status: `200 OK`

### 6. Mark account dormant

`POST /api/v1/accounts/{accountId}/dormant`

```bash
curl -i -X POST "http://localhost:8080/api/v1/accounts/10000000001/dormant"
```

Expected status: `200 OK`

### 7. Close account

`POST /api/v1/accounts/{accountId}/close`

```bash
curl -i -X POST "http://localhost:8080/api/v1/accounts/10000000001/close"
```

Expected status: `200 OK`

## Complete API list

| Method | Endpoint |
| --- | --- |
| POST | `/api/v1/accounts` |
| GET | `/api/v1/accounts/{accountId}` |
| GET | `/api/v1/accounts/number/{accountNumber}` |
| POST | `/api/v1/accounts/{accountId}/activate` |
| POST | `/api/v1/accounts/{accountId}/freeze` |
| POST | `/api/v1/accounts/{accountId}/dormant` |
| POST | `/api/v1/accounts/{accountId}/close` |

## Supported account types

```text
SAVINGS
CURRENT
FIXED_DEPOSIT
RECURRING_DEPOSIT
```

## Supported account status values

```text
PENDING
ACTIVE
DORMANT
FROZEN
CLOSED
```

## Recommended test flow

To exercise lifecycle behavior cleanly, use this sequence:

1. Create account
2. Get account by ID
3. Get account by account number
4. Activate account
5. Freeze account
6. Mark account dormant
7. Activate account again if the business flow allows it
8. Close account

## Load test data

To seed sample account records quickly for local testing, run:

```bash
cd /home/os00570/core-banking-system/backend/rahulsmgv-cbs-account-service
podman exec -i cbs-postgres psql -h localhost -U cbs_user -d cbs < scripts/load-test-data.sql
```

This script:

- clears the existing `public.accounts` rows
- resets the `account_id_sequence` to start at `10000000001`
- resets the `account_number_sequence` to start at `100000000001`
- inserts sample accounts linked to customer IDs in the customer service dataset

Example values loaded:

- `ACC-100000000001`
- `ACC-100000000002`
- `ACC-100000000003`
- `ACC-100000000004`

You can verify the rows with:

```bash
podman exec -i cbs-postgres psql -h localhost -U cbs_user -d cbs -c "SELECT account_id, customer_id, account_number, account_type, status, currency FROM public.accounts ORDER BY account_id;"
```

## Validation

The project has a verified test suite and can be run with:

```bash
mvn test
```

## Troubleshooting

### Application fails to start

Check the following:

- PostgreSQL container is running
- database credentials match `application.properties`
- `accounts` table schema matches the numeric ID model
- no stale UUID values remain in `account_id`

Common error pattern:

```text
column "account_id" is of type uuid but expression is of type bigint
```

This indicates the database still has the legacy UUID schema.

### Port already in use

If port `8080` is already taken, stop the conflicting service or change the port manually in `application.properties`:

```properties
server.port=8081
```

### Duplicate account creation

The API intentionally rejects duplicate customer + account type combinations and duplicate account numbers with validation errors.

## Search for the database check

Use this query to inspect the live schema:

```bash
podman exec -i cbs-postgres psql -h localhost -U cbs_user -d cbs -c "SELECT column_name, data_type FROM information_schema.columns WHERE table_name='accounts' ORDER BY ordinal_position;"
```

The expected output includes:

```text
 account_id | bigint
```

## Useful development commands

```bash
# run the app
mvn spring-boot:run

# run tests
mvn test

# clean and rebuild
mvn clean test

# check if the app responds
curl -i http://localhost:8080/actuator/health
```

## Summary

This service is designed to be run locally with PostgreSQL and a numeric account ID strategy. The most important setup rule is that the database must use `BIGINT` for `account_id`; otherwise Hibernate will fail while reading or writing account records.