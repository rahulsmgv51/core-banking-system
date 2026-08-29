package com.rahulsmgv.cbs.customer.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailAddressTest {

    @Test
    void shouldNormalizeEmail() {

        EmailAddress email = EmailAddress.of("  RAHUL@Example.COM ");

        assertEquals(
                "rahul@example.com",
                email.value());
    }

    @Test
    void shouldRejectInvalidEmail() {

        assertThrows(
                IllegalArgumentException.class,
                () -> EmailAddress.of("invalid-email"));
    }

    @Test
    void shouldRejectBlankEmail() {

        assertThrows(
                IllegalArgumentException.class,
                () -> EmailAddress.of(" "));
    }
}