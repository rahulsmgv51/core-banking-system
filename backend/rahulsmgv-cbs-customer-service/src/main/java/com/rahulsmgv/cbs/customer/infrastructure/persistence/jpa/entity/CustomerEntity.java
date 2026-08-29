package com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.entity;

import com.rahulsmgv.cbs.customer.domain.enums.CustomerStatus;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * JPA persistence entity for the Customer.
 *
 * IMPORTANT:
 * This class belongs to the infrastructure layer.
 * It is intentionally separate from the domain Customer aggregate.
 */
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_email", columnNames = "email_address"),
        @UniqueConstraint(name = "uk_customer_mobile", columnNames = "mobile_number")
})
public class CustomerEntity {
    /**
     * Primary key of the customer table.
     */
    /**
     * Unique 11-digit customer number.
     *
     * PostgreSQL generates this value using a database sequence.
     */
    @Id
    @Column(name = "customer_id", nullable = false, updatable = false)
    private Long customerId;

    /**
     * Customer name.
     */
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /**
     * Customer classification.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 30)
    private CustomerType customerType;

    /**
     * Current lifecycle status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CustomerStatus status;

    /**
     * Customer email address.
     */
    @Column(name = "email_address", nullable = false, length = 254)
    private String emailAddress;

    /**
     * Customer mobile number.
     */
    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    /**
     * First address line.
     */
    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    /**
     * Second address line.
     */
    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    /**
     * City.
     */
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    /**
     * State.
     */
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    /**
     * Postal code.
     */
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    /**
     * Country.
     */
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    /**
     * Creation timestamp.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Last update timestamp.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Required by JPA.
     */
    public CustomerEntity() {
    }

    // Getters and setters will be added after
    // the persistence mapping is established.

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}