package com.rahulsmgv.cbs.customer.domain.valueobject;

import java.util.Objects;

public final class CustomerName {

    private final String value;

    private CustomerName(String value) {
        this.value = value;
    }

    public static CustomerName of(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer name cannot be null or blank");
        }

        String normalizedValue = value.trim().replaceAll("\\s+", " ");

        if (normalizedValue.length() < 2) {
            throw new IllegalArgumentException(
                    "Customer name must contain at least 2 characters");
        }

        if (normalizedValue.length() > 150) {
            throw new IllegalArgumentException(
                    "Customer name cannot exceed 150 characters");
        }

        return new CustomerName(normalizedValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof CustomerName other)) {
            return false;
        }

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}