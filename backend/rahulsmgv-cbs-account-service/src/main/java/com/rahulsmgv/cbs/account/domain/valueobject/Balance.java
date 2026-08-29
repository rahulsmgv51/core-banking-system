package com.rahulsmgv.cbs.account.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public record Balance(BigDecimal amount) {

    public Balance {
        Objects.requireNonNull(amount, "Balance cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Balance cannot be negative");
        }
    }

    public static Balance zero() {
        return new Balance(BigDecimal.ZERO);
    }

    public static Balance of(BigDecimal amount) {
        return new Balance(amount);
    }

    public Balance credit(BigDecimal amount) {

        Objects.requireNonNull(amount, "Credit amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Credit amount must be greater than zero");
        }

        return new Balance(this.amount.add(amount));
    }

    public Balance debit(BigDecimal amount) {

        Objects.requireNonNull(amount, "Debit amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Debit amount must be greater than zero");
        }

        if (this.amount.compareTo(amount) < 0) {
            throw new IllegalStateException(
                    "Insufficient account balance");
        }

        return new Balance(this.amount.subtract(amount));
    }
}