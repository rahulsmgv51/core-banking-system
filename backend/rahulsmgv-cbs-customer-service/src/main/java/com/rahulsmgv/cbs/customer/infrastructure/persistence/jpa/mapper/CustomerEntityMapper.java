package com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.mapper;

import com.rahulsmgv.cbs.customer.domain.model.Customer;
import com.rahulsmgv.cbs.customer.domain.valueobject.Address;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerName;
import com.rahulsmgv.cbs.customer.domain.valueobject.EmailAddress;
import com.rahulsmgv.cbs.customer.domain.valueobject.MobileNumber;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.entity.CustomerEntity;

import java.time.Instant;

/**
 * Mapper between the Customer domain aggregate
 * and the Customer JPA persistence entity.
 *
 * This class keeps persistence concerns outside
 * the domain model.
 */
public final class CustomerEntityMapper {

    /**
     * Private constructor because this is a utility class.
     */
    private CustomerEntityMapper() {
    }

    /**
     * Converts the domain Customer into a JPA entity.
     *
     * @param customer domain Customer
     * @return CustomerEntity suitable for persistence
     */
    public static CustomerEntity toEntity(Customer customer) {

        CustomerEntity entity = new CustomerEntity();

        entity.setCustomerId(
                customer.customerId().value());

        entity.setName(
                customer.name().value());

        entity.setCustomerType(
                customer.customerType());

        entity.setStatus(
                customer.status());

        entity.setEmailAddress(
                customer.emailAddress().value());

        entity.setMobileNumber(
                customer.mobileNumber().value());

        entity.setAddressLine1(
                customer.address().addressLine1());

        entity.setAddressLine2(
                customer.address().addressLine2());

        entity.setCity(
                customer.address().city());

        entity.setState(
                customer.address().state());

        entity.setPostalCode(
                customer.address().postalCode());

        entity.setCountry(
                customer.address().country());

        entity.setCreatedAt(
                customer.createdAt());

        entity.setUpdatedAt(
                customer.updatedAt());

        return entity;
    }

    /**
     * Converts a JPA entity back into the domain Customer.
     *
     * Because Customer protects its state through
     * factory methods, reconstruction is handled through
     * the dedicated rehydrate method.
     *
     * @param entity persisted CustomerEntity
     * @return domain Customer
     */
    public static Customer toDomain(CustomerEntity entity) {

        return Customer.rehydrate(
                CustomerId.of(entity.getCustomerId()),
                CustomerName.of(entity.getName()),
                entity.getCustomerType(),
                entity.getStatus(),
                EmailAddress.of(entity.getEmailAddress()),
                MobileNumber.of(entity.getMobileNumber()),
                Address.of(
                        entity.getAddressLine1(),
                        entity.getAddressLine2(),
                        entity.getCity(),
                        entity.getState(),
                        entity.getPostalCode(),
                        entity.getCountry()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}