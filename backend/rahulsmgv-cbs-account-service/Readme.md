build Account Management properly

Since Customer Service is already working end-to-end, we should follow the same architecture rather than rushing into controller code.

Our order should be:

Account domain model
Account
AccountId
AccountType
AccountStatus
Currency
account lifecycle rules
Application layer
CreateAccountCommand
AccountResponse
AccountRepository
AccountIdGenerator
AccountApplicationService
Persistence
AccountEntity
AccountJpaRepository
AccountEntityMapper
AccountRepositoryAdapter
PostgreSQL schema
REST API
POST /api/v1/accounts
GET /api/v1/accounts/{accountId}
Account lifecycle APIs
activate
suspend
block
close
Tests
domain unit tests
application-service tests
repository integration tests
controller integration tests
Real curl validation
create account
retrieve account
lifecycle transition
invalid transition
duplicate account/business-rule validation