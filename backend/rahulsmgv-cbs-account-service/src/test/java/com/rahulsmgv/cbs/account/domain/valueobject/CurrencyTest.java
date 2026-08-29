package com.rahulsmgv.cbs.account.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void shouldCreateValidCurrency() {

        Currency currency = Currency.of("INR");

        assertEquals(
                "INR",
                currency.code());
    }

    @Test
    void shouldNormalizeCurrencyToUpperCase() {

        Currency currency = Currency.of("inr");

        assertEquals(
                "INR",
                currency.code());
    }

    @Test
    void shouldTrimCurrency() {

        Currency currency = Currency.of(" usd ");

        assertEquals(
                "USD",
                currency.code());
    }

    @Test
    void shouldRejectNullCurrency() {

        assertThrows(
                NullPointerException.class,
                () -> Currency.of(null));
    }

    @Test
    void shouldRejectInvalidCurrencyLength() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Currency.of("IN"));
    }

    @Test
    void shouldRejectNumericCurrency() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Currency.of("123"));
    }
}