package com.rahulsmgv.cbs.account.domain.valueobject;

import java.util.Objects;

public record CustomerId(Long value) {

    public CustomerId {
        Objects.requireNonNull(value, "Customer ID must not be null");

        if (value <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than zero");
        }
    }

    public static CustomerId of(Long value) {
        return new CustomerId(value);
    }
}
