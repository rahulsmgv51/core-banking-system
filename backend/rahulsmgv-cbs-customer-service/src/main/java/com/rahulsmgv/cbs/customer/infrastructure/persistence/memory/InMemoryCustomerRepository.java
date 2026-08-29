package com.rahulsmgv.cbs.customer.infrastructure.persistence.memory;

import com.rahulsmgv.cbs.customer.application.port.CustomerRepository;
import com.rahulsmgv.cbs.customer.domain.model.Customer;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// The in-memory implementation is only for development/testing. Later we'll replace/use the real persistence adapter.
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<CustomerId, Customer> customers = new ConcurrentHashMap<>();

    @Override
    public Customer save(Customer customer) {

        customers.put(customer.customerId(), customer);

        return customer;
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {

        return Optional.ofNullable(
                customers.get(customerId));
    }

    @Override
    public boolean existsById(CustomerId customerId) {

        return customers.containsKey(customerId);
    }

    @Override
    public void delete(Customer customer) {

        customers.remove(customer.customerId());
    }

    @Override
    public boolean existsByEmailAddress(String emailAddress) {

        return customers.values()
                .stream()
                .anyMatch(customer -> customer.emailAddress()
                        .value()
                        .equalsIgnoreCase(emailAddress));
    }

    @Override
    public boolean existsByMobileNumber(String mobileNumber) {

        return customers.values()
                .stream()
                .anyMatch(customer -> customer.mobileNumber()
                        .value()
                        .equals(mobileNumber));
    }
}