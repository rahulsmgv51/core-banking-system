package com.rahulsmgv.cbs.customer.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class EmailAddress {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    public static EmailAddress of(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or blank");
        }

        String normalizedValue = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalizedValue).matches()) {
            throw new IllegalArgumentException(
                    "Invalid email address: " + value);
        }

        return new EmailAddress(normalizedValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof EmailAddress other)) {
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