package com.rahulsmgv.cbs.customer.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class MobileNumber {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^\\+?[1-9][0-9]{9,14}$");

    private final String value;

    private MobileNumber(String value) {
        this.value = value;
    }

    public static MobileNumber of(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Mobile number cannot be null or blank");
        }

        String normalizedValue = value.trim().replaceAll("\\s+", "");

        if (!MOBILE_PATTERN.matcher(normalizedValue).matches()) {
            throw new IllegalArgumentException(
                    "Invalid mobile number: " + value);
        }

        return new MobileNumber(normalizedValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof MobileNumber other)) {
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