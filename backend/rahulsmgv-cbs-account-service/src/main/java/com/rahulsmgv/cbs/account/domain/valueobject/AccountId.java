package com.rahulsmgv.cbs.account.domain.valueobject;

import java.util.Objects;

public record AccountId(Long value) {

    public AccountId {
        Objects.requireNonNull(value, "Account ID cannot be null");

        if (value <= 0) {
            throw new IllegalArgumentException(
                    "Account ID must be greater than zero");
        }
    }

    public static AccountId of(Long value) {
        return new AccountId(value);
    }
}