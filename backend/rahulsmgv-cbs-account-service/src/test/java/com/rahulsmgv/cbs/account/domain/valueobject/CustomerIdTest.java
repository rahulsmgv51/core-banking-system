package com.rahulsmgv.cbs.account.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerIdTest {

    @Test
    void shouldCreateValidCustomerId() {

        CustomerId customerId = CustomerId.of(10000000001L);

        assertEquals(
                10000000001L,
                customerId.value());
    }

    @Test
    void shouldRejectNullCustomerId() {

        assertThrows(
                NullPointerException.class,
                () -> CustomerId.of(null));
    }

    @Test
    void shouldRejectZeroCustomerId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> CustomerId.of(0L));
    }

    @Test
    void shouldRejectNegativeCustomerId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> CustomerId.of(-1L));
    }
}
