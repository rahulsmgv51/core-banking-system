package com.rahulsmgv.cbs.customer.application.port;

import com.rahulsmgv.cbs.customer.domain.model.Customer;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;

import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId customerId);

    boolean existsById(CustomerId customerId);

    boolean existsByEmailAddress(String emailAddress);

    boolean existsByMobileNumber(String mobileNumber);

    void delete(Customer customer);
}