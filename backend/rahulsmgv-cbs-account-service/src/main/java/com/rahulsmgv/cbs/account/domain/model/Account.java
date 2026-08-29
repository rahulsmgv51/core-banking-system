package com.rahulsmgv.cbs.account.domain.model;

import com.rahulsmgv.cbs.account.domain.enums.AccountStatus;
import com.rahulsmgv.cbs.account.domain.enums.AccountType;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.Balance;
import com.rahulsmgv.cbs.account.domain.valueobject.Currency;

import java.time.Instant;
import java.util.Objects;

public class Account {

    private final AccountId accountId;

    private final AccountNumber accountNumber;

    private final Long customerId;

    private final AccountType accountType;

    private final Currency currency;

    private Balance balance;

    private AccountStatus status;

    private final Instant createdAt;

    private Instant updatedAt;

    private Account(
            AccountId accountId,
            AccountNumber accountNumber,
            Long customerId,
            AccountType accountType,
            Currency currency,
            Balance balance,
            AccountStatus status,
            Instant createdAt,
            Instant updatedAt) {

        this.accountId = Objects.requireNonNull(accountId);
        this.accountNumber = Objects.requireNonNull(accountNumber);
        this.customerId = Objects.requireNonNull(customerId);
        this.accountType = Objects.requireNonNull(accountType);
        this.currency = Objects.requireNonNull(currency);
        this.balance = Objects.requireNonNull(balance);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Account create(
            AccountId accountId,
            AccountNumber accountNumber,
            Long customerId,
            AccountType accountType,
            Currency currency) {

        Objects.requireNonNull(customerId);

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than zero");
        }

        Instant now = Instant.now();

        return new Account(
                accountId,
                accountNumber,
                customerId,
                accountType,
                currency,
                Balance.zero(),
                AccountStatus.PENDING,
                now,
                now);
    }

    public AccountId accountId() {
        return accountId;
    }

    public AccountNumber accountNumber() {
        return accountNumber;
    }

    public Long customerId() {
        return customerId;
    }

    public AccountType accountType() {
        return accountType;
    }

    public Currency currency() {
        return currency;
    }

    public Balance balance() {
        return balance;
    }

    public AccountStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void activate() {

        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed account cannot be activated");
        }

        if (status == AccountStatus.FROZEN) {
            throw new IllegalStateException(
                    "Frozen account cannot be activated directly");
        }

        status = AccountStatus.ACTIVE;

        touch();
    }

    public void freeze() {

        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed account cannot be frozen");
        }

        status = AccountStatus.FROZEN;

        touch();
    }

    public void makeDormant() {

        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed account cannot become dormant");
        }

        status = AccountStatus.DORMANT;

        touch();
    }

    public void close() {

        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException(
                    "Account is already closed");
        }

        status = AccountStatus.CLOSED;

        touch();
    }

    public void credit(java.math.BigDecimal amount) {

        ensureActive();

        balance = balance.credit(amount);

        touch();
    }

    public void debit(java.math.BigDecimal amount) {

        ensureActive();

        balance = balance.debit(amount);

        touch();
    }

    private void ensureActive() {

        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Account must be active for financial transactions");
        }
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    /**
     * Reconstructs an Account aggregate from persisted data.
     *
     * Used by the persistence layer when loading an
     * existing account from the database.
     */
    public static Account rehydrate(
            AccountId accountId,
            AccountNumber accountNumber,
            Long customerId,
            AccountType accountType,
            Currency currency,
            Balance balance,
            AccountStatus status,
            Instant createdAt,
            Instant updatedAt) {

        return new Account(
                accountId,
                accountNumber,
                customerId,
                accountType,
                currency,
                balance,
                status,
                createdAt,
                updatedAt);
    }
}