package com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.entity;

import com.rahulsmgv.cbs.account.domain.enums.AccountStatus;
import com.rahulsmgv.cbs.account.domain.enums.AccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "accounts",
        indexes = {
                @Index(name = "idx_account_customer_id", columnList = "customer_id"),
                @Index(name = "idx_account_status", columnList = "status"),
                @Index(name = "idx_account_type", columnList = "account_type")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_number",
                        columnNames = "account_number")
        }
)
public class AccountEntity {

    @Id
    @Column(
            name = "account_id",
            nullable = false,
            updatable = false)
    private Long accountId;

    @Column(
            name = "account_number",
            nullable = false,
            updatable = false,
            length = 18)
    private String accountNumber;

    /**
     * Customer ID belongs to Customer Service.
     * No JPA relationship is created because this is
     * a separate service boundary.
     */
    @Column(
            name = "customer_id",
            nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "account_type",
            nullable = false,
            length = 30)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30)
    private AccountStatus status;

    @Column(
            name = "currency",
            nullable = false,
            length = 3)
    private String currency;

    @Column(
            name = "balance",
            nullable = false,
            precision = 19,
            scale = 4)
    private BigDecimal balance;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false)
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false)
    private Instant updatedAt;

    public AccountEntity() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
