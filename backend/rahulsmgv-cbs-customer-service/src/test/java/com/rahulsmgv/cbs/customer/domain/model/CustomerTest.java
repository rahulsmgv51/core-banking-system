package com.rahulsmgv.cbs.customer.domain.model;

import com.rahulsmgv.cbs.customer.domain.enums.CustomerStatus;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;
import com.rahulsmgv.cbs.customer.domain.valueobject.Address;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerName;
import com.rahulsmgv.cbs.customer.domain.valueobject.EmailAddress;
import com.rahulsmgv.cbs.customer.domain.valueobject.MobileNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer createCustomer() {

        return Customer.create(
                CustomerId.of(10000000001L),
                CustomerName.of("Rahul Kumar"),
                CustomerType.INDIVIDUAL,
                EmailAddress.of("rahul@example.com"),
                MobileNumber.of("+919876543210"),
                Address.of(
                        "123 Main Road",
                        "Sector 10",
                        "Gurgaon",
                        "Haryana",
                        "122001",
                        "India"));
    }

    @Test
    void shouldCreateCustomerWithProspectStatus() {

        Customer customer = createCustomer();

        assertNotNull(customer.customerId());
        assertEquals(
                CustomerStatus.PROSPECT,
                customer.status());
    }

    @Test
    void shouldActivateCustomer() {

        Customer customer = createCustomer();

        customer.activate();

        assertEquals(
                CustomerStatus.ACTIVE,
                customer.status());
    }

    @Test
    void shouldSuspendCustomer() {

        Customer customer = createCustomer();

        customer.activate();
        customer.suspend();

        assertEquals(
                CustomerStatus.SUSPENDED,
                customer.status());
    }

    @Test
    void shouldBlockCustomer() {

        Customer customer = createCustomer();

        customer.activate();
        customer.block();

        assertEquals(
                CustomerStatus.BLOCKED,
                customer.status());
    }

    @Test
    void shouldCloseCustomer() {

        Customer customer = createCustomer();

        customer.activate();
        customer.close();

        assertEquals(
                CustomerStatus.CLOSED,
                customer.status());
    }

    @Test
    void shouldNotActivateClosedCustomer() {

        Customer customer = createCustomer();

        customer.close();

        assertThrows(
                IllegalStateException.class,
                customer::activate);
    }

    @Test
    void shouldNotSuspendClosedCustomer() {

        Customer customer = createCustomer();

        customer.close();

        assertThrows(
                IllegalStateException.class,
                customer::suspend);
    }

    @Test
    void shouldNotBlockClosedCustomer() {

        Customer customer = createCustomer();

        customer.close();

        assertThrows(
                IllegalStateException.class,
                customer::block);
    }

    @Test
    void shouldNotCloseCustomerTwice() {

        Customer customer = createCustomer();

        customer.close();

        assertThrows(
                IllegalStateException.class,
                customer::close);
    }

    @Test
    void shouldUpdateCustomerName() {

        Customer customer = createCustomer();

        customer.updateName(
                CustomerName.of("Rahul Sharma"));

        assertEquals(
                "Rahul Sharma",
                customer.name().value());
    }

    @Test
    void shouldUpdateContactInformation() {

        Customer customer = createCustomer();

        customer.updateContactInformation(
                EmailAddress.of("new@example.com"),
                MobileNumber.of("+919999999999"));

        assertEquals(
                "new@example.com",
                customer.emailAddress().value());

        assertEquals(
                "+919999999999",
                customer.mobileNumber().value());
    }

    @Test
    void shouldUpdateAddress() {

        Customer customer = createCustomer();

        Address newAddress = Address.of(
                "456 New Road",
                null,
                "Mumbai",
                "Maharashtra",
                "400001",
                "India");

        customer.updateAddress(newAddress);

        assertEquals(
                "Mumbai",
                customer.address().city());
    }
}