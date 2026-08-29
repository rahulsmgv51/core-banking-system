# RahulSMGV Core Banking System

A modular, event-driven **Core Banking System (CBS)** built with Java and modern cloud-native technologies. The platform is designed around banking-grade principles such as **double-entry accounting, idempotent transaction processing, auditability, security, domain-driven design, and reliable integration with external banking systems**.

---

## 📌 Overview

**RahulSMGV CBS** is a backend banking platform that provides the core capabilities required to manage customers, accounts, financial transactions, loans, ledger entries, and banking integrations.

The system is designed as a collection of independently deployable services with shared platform components.

The primary architectural goals are:

* Strong transactional consistency
* Double-entry accounting
* Idempotent financial operations
* Auditable business operations
* Secure API access
* Event-driven communication
* Scalable microservices
* Clear separation of business and infrastructure concerns
* External banking-system integration
* Production-oriented observability

---

## 🏗️ High-Level Architecture

```text
                         ┌──────────────────────────┐
                         │       Client Channels     │
                         │                          │
                         │ Web / Mobile / Branch /  │
                         │ External Banking Systems │
                         └────────────┬─────────────┘
                                      │
                                      ▼
                         ┌──────────────────────────┐
                         │       API Gateway        │
                         │                          │
                         │ Authentication           │
                         │ Authorization            │
                         │ Routing                  │
                         │ Correlation ID           │
                         │ Request Transformation   │
                         └────────────┬─────────────┘
                                      │
             ┌────────────────────────┼────────────────────────┐
             │                        │                        │
             ▼                        ▼                        ▼
      ┌──────────────┐        ┌──────────────┐        ┌──────────────┐
      │   Customer   │        │   Account    │        │     Loan     │
      │   Service    │        │   Service    │        │   Service    │
      └──────┬───────┘        └──────┬───────┘        └──────┬───────┘
             │                       │                       │
             └───────────────────────┼───────────────────────┘
                                     │
                                     ▼
                           ┌───────────────────┐
                           │ Transaction /     │
                           │ Ledger Layer      │
                           └─────────┬─────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    │                │                │
                    ▼                ▼                ▼
              PostgreSQL          Kafka            Redis
                    │                │
                    ▼                ▼
               Banking Data     Domain Events
                    │
                    ▼
             Audit / Reporting
```

---

# 🎯 Core Capabilities

The CBS platform is designed to support the following business capabilities.

### Customer Management

* Customer onboarding
* Customer profile management
* Customer lifecycle
* KYC status tracking
* Customer updates
* Customer identification

### Account Management

* Account creation
* Account activation
* Account status management
* Account blocking/unblocking
* Account ownership
* Account balance management

### Deposits & Withdrawals

* Cash/electronic deposits
* Withdrawals
* Balance validation
* Transaction validation
* Ledger posting
* Transaction history

### Fund Transfers

* Internal account-to-account transfers
* Transaction validation
* Idempotent processing
* Debit and credit entries
* Transaction status tracking
* Audit trail

### Ledger & Accounting

The ledger is the financial foundation of the system.

Every financial transaction follows the double-entry principle:

```text
Total Debit = Total Credit
```

Example:

```text
Transaction: TXN-10001

Account A
    DEBIT   ₹10,000

Account B
    CREDIT  ₹10,000
```

The ledger provides:

* Debit entries
* Credit entries
* Account balances
* Transaction references
* Posting timestamps
* Auditability
* Accounting consistency

### Loan Management

Planned capabilities include:

* Loan application
* Loan origination
* Loan approval
* Loan disbursement
* Repayment schedules
* Installments
* Interest calculation
* Loan servicing
* Outstanding balance tracking

---

# 🔐 Security

Security is designed around modern OAuth 2.0 / OpenID Connect principles.

```text
                   ┌──────────────┐
                   │   Keycloak   │
                   └──────┬───────┘
                          │
                       JWT Token
                          │
                          ▼
Client ───────────────► API Gateway
                          │
                          ▼
                   CBS Microservices
```

Security capabilities include:

* OAuth 2.0
* OpenID Connect
* JWT access tokens
* Authentication
* Authorization
* Role-based access control
* Secure service communication
* Token validation
* API-level security

---

# 🔄 Idempotent Transaction Processing

Financial APIs must safely handle retries.

Example:

```text
POST /transfers

Idempotency-Key: 7f8e9d...
```

If the client retries the same request:

```text
Request #1
     │
     ▼
Validate
     │
     ▼
Process Transaction
     │
     ▼
Ledger Posting
     │
     ▼
SUCCESS
     │
     ▼
Store Idempotency Result


Request #2
     │
     ▼
Same Idempotency-Key
     │
     ▼
Existing Transaction Found
     │
     ▼
Return Previous Result
```

This prevents duplicate money movement when clients retry requests because of timeouts or network failures.

---

# 📨 Event-Driven Architecture

Kafka is used as the event backbone for asynchronous communication between services.

Example:

```text
Customer Service
      │
      │ CustomerCreated
      ▼
    Kafka
      │
      ├──────────────► Audit Service
      │
      ├──────────────► Notification Service
      │
      └──────────────► Other Consumers
```

Potential domain events include:

```text
CustomerCreated
CustomerUpdated
CustomerKycUpdated

AccountCreated
AccountActivated
AccountBlocked

TransactionInitiated
TransactionCompleted
TransactionFailed

LedgerPosted

LoanCreated
LoanApproved
LoanDisbursed
LoanRepaymentCompleted
```

Events allow business services to remain loosely coupled while enabling asynchronous processing.

---

# 🧱 Repository Structure

The project is organized into multiple repositories.

```text
core-banking-system/
│
├── rahulsmgv-cbs-parent
│
├── rahulsmgv-cbs-common
│
├── rahulsmgv-cbs-events
│
├── rahulsmgv-cbs-infra
│
├── rahulsmgv-cbs-security
│
├── rahulsmgv-cbs-service-template
│
├── rahulsmgv-cbs-customer-service
│
├── rahulsmgv-cbs-account-service
│
├── rahulsmgv-cbs-transaction-service
│
├── rahulsmgv-cbs-ledger-service
│
└── rahulsmgv-cbs-loan-service
```

> Some services are under active development and may not yet exist in the implementation repository.

---

# 📦 Platform Components

## `rahulsmgv-cbs-parent`

Central Maven parent project.

Responsibilities:

* Java version management
* Dependency management
* Maven plugin configuration
* Shared build configuration
* Common project properties

---

## `rahulsmgv-cbs-common`

Shared library used by CBS services.

Contains reusable components such as:

* Common exceptions
* API response models
* Constants
* Validation utilities
* Correlation handling
* Shared abstractions

---

## `rahulsmgv-cbs-events`

Shared event contracts and event-related infrastructure.

Responsibilities:

* Domain event definitions
* Event schemas
* Kafka integration contracts
* Event metadata

---

## `rahulsmgv-cbs-infra`

Infrastructure and local development environment.

The environment includes:

```text
PostgreSQL
Kafka
Redis
Keycloak
MinIO
Prometheus
Grafana
```

---

## `rahulsmgv-cbs-security`

Security foundation for the CBS platform.

Responsibilities include:

* OAuth2/OIDC integration
* JWT validation
* Authentication
* Authorization
* Security configuration

---

## `rahulsmgv-cbs-service-template`

Base template for creating new CBS microservices.

The goal is to standardize:

```text
Project Structure
Architecture
Configuration
Logging
Error Handling
Testing
Observability
Security
```

---

## `rahulsmgv-cbs-customer-service`

Customer domain service.

Current responsibilities include:

```text
Customer Creation
Customer Retrieval
Customer Update
Customer Persistence
Customer Validation
```

The service follows a layered/hexagonal architecture:

```text
REST Controller
       │
       ▼
Application Service
       │
       ▼
Domain
       │
       ▼
Repository Port
       │
       ▼
Persistence Adapter
       │
       ▼
PostgreSQL
```

---

# 🧩 Architectural Style

The CBS platform follows principles from:

* Domain-Driven Design
* Hexagonal Architecture
* Clean Architecture
* Microservices Architecture
* Event-Driven Architecture

A typical service is structured around:

```text
┌────────────────────────────────────┐
│            Interface               │
│                                    │
│ REST Controllers / Messaging       │
└──────────────────┬─────────────────┘
                   │
                   ▼
┌────────────────────────────────────┐
│          Application Layer         │
│                                    │
│ Use Cases / Application Services   │
└──────────────────┬─────────────────┘
                   │
                   ▼
┌────────────────────────────────────┐
│             Domain                 │
│                                    │
│ Entities / Value Objects / Rules   │
└──────────────────┬─────────────────┘
                   │
                   ▼
┌────────────────────────────────────┐
│          Infrastructure            │
│                                    │
│ JPA / PostgreSQL / Kafka / Redis   │
└────────────────────────────────────┘
```

This keeps the core business logic independent from frameworks and infrastructure.

---

# 🔌 External Banking Integration

The platform is designed to integrate with existing banking systems such as **Finacle** and other external financial infrastructure.

A typical integration flow is:

```text
CBS API
   │
   ▼
Integration / Gateway Layer
   │
   ├── Authentication
   ├── Correlation ID
   ├── Routing
   ├── JSON → XML
   └── XML → JSON
   │
   ▼
External Banking System
   │
   ▼
Response Transformation
   │
   ▼
CBS API
```

The integration layer can support dynamic external API routing such as:

```text
/finacle-rest-api/**
```

and transformation between JSON-based CBS APIs and XML-based external banking APIs.

---

# 🔁 JSON ↔ XML Transformation

The integration layer supports transformation between modern REST JSON APIs and legacy/external XML-based banking systems.

Example:

```text
CBS Request
    │
    ▼
JSON
    │
    ▼
JSON → XML Transformer
    │
    ▼
XML
    │
    ▼
External Banking System
```

Response:

```text
External Banking System
    │
    ▼
XML
    │
    ▼
XML → JSON Transformer
    │
    ▼
JSON
    │
    ▼
CBS Client
```

This allows modern services to communicate with legacy banking infrastructure without exposing XML complexity to consumers.

---

# 🗄️ Data Architecture

PostgreSQL is the primary relational database.

The data model is designed around clear ownership boundaries between services.

Important concepts include:

```text
Customer
Account
Transaction
Ledger
LedgerEntry
Loan
LoanRepayment
AuditRecord
```

Financial data requires particular attention to:

* ACID transactions
* Consistency
* Referential integrity
* Transaction boundaries
* Immutable financial records
* Auditability
* Concurrency control

---

# 💾 Redis

Redis is used for use cases such as:

* Caching
* Idempotency keys
* Short-lived state
* Distributed coordination where appropriate

Redis should not become the system of record for financial transactions.

The authoritative financial state remains in the transactional database/ledger.

---

# 📦 Kafka

Kafka provides asynchronous event communication.

```text
Producer
   │
   ▼
 Kafka Topic
   │
   ├────────► Consumer A
   ├────────► Consumer B
   └────────► Consumer C
```

This allows the platform to scale consumers independently.

---

# 📊 Observability

The infrastructure includes:

```text
Prometheus
     │
     ▼
 Metrics
     │
     ▼
 Grafana
```

Observability is intended to cover:

* Request metrics
* JVM metrics
* Database metrics
* Kafka metrics
* Error rates
* Latency
* Throughput
* Service health

Correlation IDs are used to trace requests across services.

Example:

```text
X-Correlation-ID: 8b3f1d7c-...
```

The same correlation ID can be propagated through:

```text
Gateway
   ↓
Customer Service
   ↓
Account Service
   ↓
Transaction Service
   ↓
Kafka
```

---

# 🧪 Testing Strategy

The project follows a layered testing approach.

### Unit Tests

Business and application logic are tested independently.

```text
Application Service
Domain Logic
Validation
Mapping
```

### Integration Tests

Integration tests validate interactions between components.

Examples:

```text
REST Controller
JPA Repository
PostgreSQL
Application Context
```

### End-to-End Testing

The long-term goal is to validate complete business workflows.

Example:

```text
Create Customer
      ↓
Create Account
      ↓
Deposit Money
      ↓
Transfer Money
      ↓
Ledger Posting
      ↓
Transaction Completed
```

---

# 🐳 Local Infrastructure

The local development environment is based on containerized infrastructure.

Expected platform components:

```text
┌────────────────────────────────────┐
│          Local CBS Platform        │
├────────────────────────────────────┤
│ PostgreSQL                         │
│ Kafka                              │
│ Redis                              │
│ Keycloak                           │
│ MinIO                              │
│ Prometheus                         │
│ Grafana                            │
└────────────────────────────────────┘
```

Container tooling can be used through Docker/Podman depending on the development environment.

---

# 🛠️ Technology Stack

| Category             | Technology                     |
| -------------------- | ------------------------------ |
| Language             | Java 21                        |
| Backend              | Spring Boot                    |
| Architecture         | Microservices                  |
| Architecture Pattern | Hexagonal / Clean Architecture |
| Database             | PostgreSQL                     |
| Messaging            | Apache Kafka                   |
| Cache                | Redis                          |
| Authentication       | Keycloak                       |
| Security             | OAuth 2.0 / OIDC / JWT         |
| Object Storage       | MinIO                          |
| Metrics              | Prometheus                     |
| Dashboards           | Grafana                        |
| Containers           | Docker / Podman                |
| Orchestration        | Kubernetes / OpenShift         |
| Build                | Maven                          |
| API                  | REST                           |
| Integration          | JSON / XML                     |
| External Banking     | Finacle / Banking APIs         |

---

# 📋 Product Specification

The CBS platform is being developed around several specification areas:

```text
CBS-PS   Product Specification
CBS-DS   Domain Specification
CBS-TAS  Technical Architecture Specification
CBS-API  API Specification
CBS-BPM  Business Process Model
CBS-DB   Database Specification
CBS-EVT  Event Specification
CBS-SEC  Security Specification
CBS-UX   User Experience
```

These specifications provide a separation between:

```text
Business Requirements
        ↓
Domain Model
        ↓
Architecture
        ↓
API Contracts
        ↓
Database
        ↓
Events
        ↓
Security
        ↓
Implementation
```

---

# 🔄 Example: Customer Onboarding

A simplified customer onboarding workflow:

```text
Client
  │
  │ POST /customers
  ▼
API Gateway
  │
  │ Authentication
  │ Correlation ID
  ▼
Customer Service
  │
  ▼
Validate Customer
  │
  ▼
Create Customer
  │
  ▼
PostgreSQL
  │
  ▼
CustomerCreated Event
  │
  ▼
Kafka
  │
  ├──────► Audit
  ├──────► Notification
  └──────► Other Services
```

---

# 💸 Example: Internal Fund Transfer

```text
Client
  │
  │ Transfer ₹10,000
  ▼
API Gateway
  │
  ▼
Transaction Service
  │
  ├── Validate request
  ├── Validate accounts
  ├── Check balance
  ├── Check idempotency
  │
  ▼
Transaction
  │
  ├───────────────┐
  ▼               ▼
Debit           Credit
Account A       Account B
  │               │
  └───────┬───────┘
          ▼
     Ledger Posting
          │
          ▼
    Debit = Credit
          │
          ▼
       Commit
          │
          ▼
   Transaction Event
          │
          ▼
        Kafka
```

---

# 🔒 Banking Design Principles

The project follows several important financial-system principles.

### 1. Never lose a financial transaction

Financial operations must be durable and recoverable.

### 2. Never process the same request twice

Idempotency must protect retryable APIs.

### 3. Every financial movement must be accounted for

Transactions should result in balanced ledger entries.

```text
Debit = Credit
```

### 4. Financial history should be auditable

Important financial records should not simply be overwritten.

### 5. Business logic should not depend on infrastructure

Domain rules should remain independent of:

```text
Database
Kafka
HTTP
Spring
Redis
```

where practical.

### 6. Services should have clear ownership

Each microservice should own its business domain and data.

### 7. Events should be treated as contracts

Events need versioning and backward compatibility.

---

# 🚧 Current Development Status

The project is being developed incrementally.

### Foundation

* [x] Maven parent project
* [x] Common library
* [x] Event foundation
* [x] Infrastructure foundation
* [x] Security foundation
* [x] Service template

### Customer Domain

* [x] Customer service skeleton
* [x] Application layer
* [x] Domain layer
* [x] Persistence adapter
* [x] REST controller
* [x] Integration tests
* [x] Repository tests

### Account Domain

* [ ] Account service
* [ ] Account lifecycle
* [ ] Account balance
* [ ] Account status management

### Transaction Domain

* [ ] Transaction service
* [ ] Idempotency
* [ ] Transfer processing
* [ ] Transaction state machine

### Ledger

* [ ] Ledger service
* [ ] Double-entry posting
* [ ] Ledger reconciliation
* [ ] Balance calculation

### Loan

* [ ] Loan origination
* [ ] Loan approval
* [ ] Loan disbursement
* [ ] Repayment
* [ ] Interest calculation

### Platform

* [ ] API Gateway
* [ ] Centralized observability
* [ ] Complete event contracts
* [ ] Production Kubernetes deployment
* [ ] CI/CD pipeline
* [ ] Disaster recovery strategy

---

# 🗺️ Development Roadmap

```text
Phase 1
Platform Foundation
    │
    ├── Parent
    ├── Common
    ├── Events
    ├── Security
    └── Infrastructure
             │
             ▼
Phase 2
Customer Management
             │
             ▼
Phase 3
Account Management
             │
             ▼
Phase 4
Transaction Processing
             │
             ▼
Phase 5
Double-Entry Ledger
             │
             ▼
Phase 6
Loans
             │
             ▼
Phase 7
External Banking Integration
             │
             ▼
Phase 8
Observability + Kubernetes
             │
             ▼
Phase 9
Production Hardening
```

---

# 🎓 Engineering Goals

This project is intended to demonstrate practical experience with:

* Java backend development
* Spring Boot
* Microservices
* Domain-driven design
* Clean/Hexagonal architecture
* REST API design
* Event-driven architecture
* Kafka
* PostgreSQL
* Redis
* OAuth2/OIDC
* JWT
* Distributed systems
* Transaction management
* Idempotency
* Double-entry accounting
* Banking integrations
* Observability
* Containerization
* Kubernetes/OpenShift
* Automated testing

---

# 🚀 Getting Started

Clone the project repositories and start the infrastructure services.

Example:

```bash
git clone <repository-url>
cd core-banking-system
```

Start the local infrastructure using the project's container configuration:

```bash
docker compose up -d
```

or, when using Podman:

```bash
podman compose up -d
```

Verify the infrastructure:

```bash
docker ps
```

Build an individual Maven service:

```bash
mvn clean verify
```

Run a service locally:

```bash
mvn spring-boot:run
```

> Exact commands may vary depending on the individual repository and current implementation.

---

# 📁 Recommended Repository Organization

The overall platform should maintain a clear distinction between:

```text
Platform
├── Build / Dependency Management
├── Common Libraries
├── Security
├── Events
└── Infrastructure

Business Domains
├── Customer
├── Account
├── Transaction
├── Ledger
└── Loan

Integration
├── API Gateway
├── Finacle Integration
└── External Banking APIs

Operations
├── Docker
├── Kubernetes
├── Helm
├── Monitoring
└── CI/CD
```

---

# 🤝 Development Philosophy

The project is built incrementally rather than attempting to implement the entire CBS at once.

Each capability should follow:

```text
Requirement
    ↓
Domain Model
    ↓
Business Rules
    ↓
API Contract
    ↓
Persistence Model
    ↓
Implementation
    ↓
Tests
    ↓
Events
    ↓
Observability
    ↓
Deployment
```

This approach keeps the system maintainable as additional banking capabilities are introduced.

---

# 📜 License

This project is currently intended as a personal engineering and portfolio project.

License information can be added when the project is prepared for public distribution.

---

# 👨‍💻 Author

**RahulSMGV**

Core Banking System designed and developed as a practical exploration of modern banking backend architecture, distributed systems, financial transaction processing, and cloud-native Java engineering.