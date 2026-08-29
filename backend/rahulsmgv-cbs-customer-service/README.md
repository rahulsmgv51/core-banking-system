# RahulSMGV CBS Customer Service

This service manages customer records for the Core Banking System. It exposes REST APIs for creating, reading, updating, deleting, and transitioning customer lifecycle states.

## Overview

- Java 21
- Spring Boot 3.5.x
- Spring Web + Validation + Data JPA
- PostgreSQL 15
- Customer IDs are generated as 11-digit numeric values starting at `10000000001`
- The `customers.customer_id` column is mapped as `BIGINT`

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
DROP TABLE IF EXISTS public.customers;
DROP SEQUENCE IF EXISTS public.customer_id_sequence;

CREATE SEQUENCE public.customer_id_sequence
    START WITH 10000000001
    INCREMENT BY 1
    MINVALUE 10000000001
    MAXVALUE 99999999999
    NO CYCLE;

CREATE TABLE public.customers (
    customer_id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('public.customer_id_sequence'),
    name VARCHAR(150) NOT NULL,
    customer_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    email_address VARCHAR(254) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_customer_email UNIQUE (email_address),
    CONSTRAINT uk_customer_mobile UNIQUE (mobile_number)
);

CREATE INDEX idx_customer_status ON public.customers (status);
SQL
```

## Running the service

```bash
cd /home/os00570/core-banking-system/backend/rahulsmgv-cbs-customer-service
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

Base path: `/api/v1/customers`

### 1. Create Customer

`POST /api/v1/customers`

```bash
curl -i -X POST "http://localhost:8080/api/v1/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rahul Kumar Vishwakarma",
    "customerType": "INDIVIDUAL",
    "emailAddress": "rahul.test1@example.com",
    "mobileNumber": "+919876543210",
    "addressLine1": "Belapur",
    "addressLine2": "Navi Mumbai",
    "city": "Navi Mumbai",
    "state": "Maharashtra",
    "postalCode": "400614",
    "country": "India"
  }'
```

Expected status: `201 Created`

Example response:

```json
{
  "customerId": 10000000001,
  "name": "Rahul Kumar Vishwakarma",
  "customerType": "INDIVIDUAL",
  "status": "PROSPECT",
  "emailAddress": "rahul.test1@example.com",
  "mobileNumber": "+919876543210",
  "addressLine1": "Belapur",
  "addressLine2": "Navi Mumbai",
  "city": "Navi Mumbai",
  "state": "Maharashtra",
  "postalCode": "400614",
  "country": "India",
  "createdAt": "2026-08-29T12:59:24.000Z",
  "updatedAt": "2026-08-29T12:59:24.000Z"
}
```

### 2. Get Customer

`GET /api/v1/customers/{customerId}`

```bash
curl -i "http://localhost:8080/api/v1/customers/10000000001"
```

Expected status: `200 OK`

### 3. Update Customer

`PATCH /api/v1/customers/{customerId}`

```bash
curl -i -X PATCH "http://localhost:8080/api/v1/customers/10000000001" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rahul Kumar Updated",
    "customerType": "INDIVIDUAL",
    "emailAddress": "rahul.updated@example.com",
    "mobileNumber": "+919876543211",
    "addressLine1": "Sector 15",
    "addressLine2": "CBD Belapur",
    "city": "Navi Mumbai",
    "state": "Maharashtra",
    "postalCode": "400614",
    "country": "India"
  }'
```

Expected status: `200 OK`

> The current implementation requires all fields to be supplied on update, so it behaves like a full update rather than a partial patch.

### 4. Delete Customer

`DELETE /api/v1/customers/{customerId}`

```bash
curl -i -X DELETE "http://localhost:8080/api/v1/customers/10000000001"
```

Expected status: `204 No Content`

### 5. Activate Customer

`POST /api/v1/customers/{customerId}/activate`

```bash
curl -i -X POST "http://localhost:8080/api/v1/customers/10000000001/activate"
```

Expected status: `200 OK`

### 6. Suspend Customer

`POST /api/v1/customers/{customerId}/suspend`

```bash
curl -i -X POST "http://localhost:8080/api/v1/customers/10000000001/suspend"
```

Expected status: `200 OK`

### 7. Block Customer

`POST /api/v1/customers/{customerId}/block`

```bash
curl -i -X POST "http://localhost:8080/api/v1/customers/10000000001/block"
```

Expected status: `200 OK`

### 8. Deactivate Customer

`POST /api/v1/customers/{customerId}/deactivate`

```bash
curl -i -X POST "http://localhost:8080/api/v1/customers/10000000001/deactivate"
```

Expected status: `200 OK`

### 9. Close Customer

`POST /api/v1/customers/{customerId}/close`

```bash
curl -i -X POST "http://localhost:8080/api/v1/customers/10000000001/close"
```

Expected status: `200 OK`

## Recommended test flow

To exercise lifecycle behavior cleanly, use this sequence:

1. Create customer
2. Get customer
3. Update customer
4. Activate customer
5. Suspend customer
6. Activate customer again if the business flow allows it
7. Block customer
8. Deactivate customer
9. Close customer
10. Delete customer

## Complete API list

| Method | Endpoint |
| --- | --- |
| POST | `/api/v1/customers` |
| GET | `/api/v1/customers/{customerId}` |
| PATCH | `/api/v1/customers/{customerId}` |
| DELETE | `/api/v1/customers/{customerId}` |
| POST | `/api/v1/customers/{customerId}/activate` |
| POST | `/api/v1/customers/{customerId}/suspend` |
| POST | `/api/v1/customers/{customerId}/block` |
| POST | `/api/v1/customers/{customerId}/deactivate` |
| POST | `/api/v1/customers/{customerId}/close` |

## Validation

The project has a verified test suite and can be run with:

```bash
mvn test
```

For a quick smoke check without full integration coverage, you can also run the focused unit tests:

```bash
mvn test -Dtest=CustomerApplicationServiceTest,CustomerTest
```

This project expects the database schema to match the numeric ID model; a legacy UUID-based `customers.customer_id` column will fail with Hibernate type conversion errors.

## Quick start for a new user

Follow these steps in order:

1. Start PostgreSQL in Podman:

   ```bash
   podman start cbs-postgres
   ```

2. Confirm the database is reachable:

   ```bash
   podman exec -i cbs-postgres psql -h localhost -U cbs_user -d cbs -c "SELECT 1;"
   ```

3. Ensure the `customers` table uses the right schema:

   ```bash
   podman exec -i cbs-postgres psql -h localhost -U cbs_user -d cbs -c "\d customers"
   ```

4. If the table is still using a UUID-based `customer_id`, reset it with the SQL shown in the Local PostgreSQL setup section.

5. Run the application:

   ```bash
   cd /home/os00570/core-banking-system/backend/rahulsmgv-cbs-customer-service
   mvn spring-boot:run
   ```

6. Check the app is listening on the expected port:

   ```bash
   curl -i http://localhost:8080/actuator/health
   ```

7. Test the customer API using one of the examples above.

## Environment details

### Application configuration

The service reads PostgreSQL configuration from:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cbs
spring.datasource.username=cbs_user
spring.datasource.password=cbs_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Expected database behavior

- `customer_id` is a `BIGINT`
- IDs begin at `10000000001`
- IDs increase by `1`
- email and mobile are unique
- customer status is constrained to allowed lifecycle values

## Troubleshooting

### Application fails to start

Check the following:

- PostgreSQL container is running
- database credentials match `application.properties`
- `customers` table schema matches the numeric ID model
- no stale UUID values remain in `customer_id`

Common error pattern:

```text
column "customer_id" is of type uuid but expression is of type bigint
```

This indicates the database still has the legacy UUID schema.

### Port already in use

If port `8080` is already taken, stop the conflicting service or change the port manually in `application.properties`:

```properties
server.port=8081
```

### Duplicate email or mobile number

The API intentionally rejects duplicate email/mobile values with a `409 Conflict` response.

### Search for the database check

Use this query to inspect the live schema:

```bash
podman exec -i cbs-postgres psql -h localhost -U cbs_user -d cbs -c "SELECT column_name, data_type FROM information_schema.columns WHERE table_name='customers' ORDER BY ordinal_position;"
```

The expected output includes:

```text
 customer_id | bigint
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

This service is designed to be run locally with PostgreSQL and a numeric customer ID strategy. The most important setup rule is that the database must use `BIGINT` for `customer_id`; otherwise Hibernate will fail while reading or writing customer records.
