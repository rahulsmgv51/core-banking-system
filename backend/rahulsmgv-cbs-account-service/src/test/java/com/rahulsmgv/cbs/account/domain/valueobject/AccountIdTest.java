package com.rahulsmgv.cbs.account.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountIdTest {

    @Test
    void shouldCreateValidAccountId() {

        AccountId accountId = AccountId.of(10000000001L);

        assertEquals(
                10000000001L,
                accountId.value());
    }

    @Test
    void shouldRejectNullAccountId() {

        assertThrows(
                NullPointerException.class,
                () -> AccountId.of(null));
    }

    @Test
    void shouldRejectZeroAccountId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountId.of(0L));
    }

    @Test
    void shouldRejectNegativeAccountId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountId.of(-1L));
    }
}
