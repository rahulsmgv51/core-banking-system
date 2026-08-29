package com.rahulsmgv.cbs.account.domain.valueobject;

import java.util.Objects;

public record AccountNumber(String value) {

    public AccountNumber {
        Objects.requireNonNull(
                value,
                "Account number cannot be null");

        value = value.trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "Account number cannot be blank");
        }

        if (!value.matches("\\d{10,18}")) {
            throw new IllegalArgumentException(
                    "Account number must contain 10 to 18 digits");
        }
    }

    public static AccountNumber of(String value) {
        return new AccountNumber(value);
    }
}