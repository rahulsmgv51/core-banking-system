package com.rahulsmgv.cbs.customer.domain.valueobject;

import java.util.Objects;

public final class CustomerId {

    public static final long MIN_VALUE = 10_000_000_000L;
    public static final long MAX_VALUE = 99_999_999_999L;

    private final Long value;

    private CustomerId(Long value) {
        this.value = Objects.requireNonNull(value, "Customer ID cannot be null");
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Customer ID must be an 11-digit number");
        }
    }

    public static CustomerId of(Long value) {
        return new CustomerId(value);
    }

    public Long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CustomerId other)) {
            return false;
        }

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}