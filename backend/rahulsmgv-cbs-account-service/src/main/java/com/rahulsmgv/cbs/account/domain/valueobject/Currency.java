package com.rahulsmgv.cbs.account.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public record Currency(String code) {

    public Currency {

        Objects.requireNonNull(
                code,
                "Currency code cannot be null");

        code = code.trim().toUpperCase(Locale.ROOT);

        if (!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Currency code must be a 3-letter ISO currency code");
        }
    }

    public static Currency of(String code) {
        return new Currency(code);
    }

    public static Currency inr() {
        return new Currency("INR");
    }
}