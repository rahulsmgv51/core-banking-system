package com.rahulsmgv.cbs.account.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountNumberTest {

    @Test
    void shouldCreateValidAccountNumber() {

        AccountNumber accountNumber =
                AccountNumber.of("123456789012");

        assertEquals(
                "123456789012",
                accountNumber.value());
    }

    @Test
    void shouldTrimAccountNumber() {

        AccountNumber accountNumber =
                AccountNumber.of("  123456789012  ");

        assertEquals(
                "123456789012",
                accountNumber.value());
    }

    @Test
    void shouldRejectNullAccountNumber() {

        assertThrows(
                NullPointerException.class,
                () -> AccountNumber.of(null));
    }

    @Test
    void shouldRejectShortAccountNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountNumber.of("123456789"));
    }

    @Test
    void shouldRejectNonNumericAccountNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountNumber.of("123456ABCDEF"));
    }
}
