package com.rahulsmgv.cbs.customer.domain.model;

import com.rahulsmgv.cbs.customer.domain.enums.CustomerStatus;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;
import com.rahulsmgv.cbs.customer.domain.valueobject.Address;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerName;
import com.rahulsmgv.cbs.customer.domain.valueobject.EmailAddress;
import com.rahulsmgv.cbs.customer.domain.valueobject.MobileNumber;

import java.time.Instant;
import java.util.Objects;

public class Customer {

    private final CustomerId customerId;

    private CustomerName name;

    private CustomerType customerType;

    private CustomerStatus status;

    private EmailAddress emailAddress;

    private MobileNumber mobileNumber;

    private Address address;

    private final Instant createdAt;

    private Instant updatedAt;

    private Customer(
            CustomerId customerId,
            CustomerName name,
            CustomerType customerType,
            CustomerStatus status,
            EmailAddress emailAddress,
            MobileNumber mobileNumber,
            Address address,
            Instant createdAt,
            Instant updatedAt) {

        this.customerId = Objects.requireNonNull(customerId);
        this.name = Objects.requireNonNull(name);
        this.customerType = Objects.requireNonNull(customerType);
        this.status = Objects.requireNonNull(status);
        this.emailAddress = Objects.requireNonNull(emailAddress);
        this.mobileNumber = Objects.requireNonNull(mobileNumber);
        this.address = Objects.requireNonNull(address);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Customer create(
            CustomerId customerId,
            CustomerName name,
            CustomerType customerType,
            EmailAddress emailAddress,
            MobileNumber mobileNumber,
            Address address) {

        Instant now = Instant.now();

        return new Customer(
            customerId,
                name,
                customerType,
                CustomerStatus.PROSPECT,
                emailAddress,
                mobileNumber,
                address,
                now,
                now);
    }

    public CustomerId customerId() {
        return customerId;
    }

    public CustomerName name() {
        return name;
    }

    public CustomerType customerType() {
        return customerType;
    }

    public CustomerStatus status() {
        return status;
    }

    public EmailAddress emailAddress() {
        return emailAddress;
    }

    public MobileNumber mobileNumber() {
        return mobileNumber;
    }

    public Address address() {
        return address;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void updateName(CustomerName name) {

        this.name = Objects.requireNonNull(name);

        touch();
    }

    public void updateContactInformation(
            EmailAddress emailAddress,
            MobileNumber mobileNumber) {

        this.emailAddress = Objects.requireNonNull(emailAddress);
        this.mobileNumber = Objects.requireNonNull(mobileNumber);

        touch();
    }

    public void updateAddress(Address address) {

        this.address = Objects.requireNonNull(address);

        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public void activate() {

        if (status == CustomerStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed customer cannot be activated");
        }

        if (status == CustomerStatus.BLOCKED) {
            throw new IllegalStateException(
                    "Blocked customer cannot be activated directly");
        }

        this.status = CustomerStatus.ACTIVE;

        touch();
    }

    public void suspend() {

        if (status == CustomerStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed customer cannot be suspended");
        }

        this.status = CustomerStatus.SUSPENDED;

        touch();
    }

    public void block() {

        if (status == CustomerStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed customer cannot be blocked");
        }

        this.status = CustomerStatus.BLOCKED;

        touch();
    }

    public void close() {

        if (status == CustomerStatus.CLOSED) {
            throw new IllegalStateException(
                    "Customer is already closed");
        }

        this.status = CustomerStatus.CLOSED;

        touch();
    }

    public void deactivate() {

        if (status == CustomerStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed customer cannot be deactivated");
        }

        this.status = CustomerStatus.INACTIVE;

        touch();
    }

    /**
     * Reconstructs a Customer aggregate from persisted data.
     *
     * This method is used by the persistence layer when
     * loading an existing customer from the database.
     */
    public static Customer rehydrate(
            CustomerId customerId,
            CustomerName name,
            CustomerType customerType,
            CustomerStatus status,
            EmailAddress emailAddress,
            MobileNumber mobileNumber,
            Address address,
            Instant createdAt,
            Instant updatedAt) {

        return new Customer(
                customerId,
                name,
                customerType,
                status,
                emailAddress,
                mobileNumber,
                address,
                createdAt,
                updatedAt);
    }
}