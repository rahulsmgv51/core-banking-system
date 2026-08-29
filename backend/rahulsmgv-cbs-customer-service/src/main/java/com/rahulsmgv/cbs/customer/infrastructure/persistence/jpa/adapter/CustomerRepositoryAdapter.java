package com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.adapter;

import com.rahulsmgv.cbs.customer.application.port.CustomerRepository;
import com.rahulsmgv.cbs.customer.domain.model.Customer;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.entity.CustomerEntity;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.mapper.CustomerEntityMapper;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.repository.CustomerJpaRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Persistence adapter for the Customer aggregate.
 *
 * This class implements the application-layer
 * CustomerRepository port.
 *
 * It acts as the bridge between:
 *
 * Application Layer
 * ↓
 * CustomerRepository
 * ↓
 * CustomerRepositoryAdapter
 * ↓
 * CustomerJpaRepository
 * ↓
 * PostgreSQL
 */
@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerRepositoryAdapter.class);

    private final CustomerJpaRepository customerJpaRepository;

    /**
     * Constructor injection.
     *
     * Spring automatically provides the
     * CustomerJpaRepository implementation.
     */
    public CustomerRepositoryAdapter(
            CustomerJpaRepository customerJpaRepository) {

        this.customerJpaRepository = customerJpaRepository;
    }

    /**
     * Saves a Customer domain aggregate.
     *
     * The domain object is first converted into
     * a JPA entity before being persisted.
     */
    @Override
    public Customer save(Customer customer) {

        log.info("Persisting customer aggregate with customerId={}", customer.customerId().value());

        // Convert domain object to persistence entity.
        CustomerEntity entity = CustomerEntityMapper.toEntity(customer);

        // Save entity using Spring Data JPA.
        CustomerEntity savedEntity = customerJpaRepository.save(entity);

        log.info("Customer aggregate persisted successfully with customerId={}", savedEntity.getCustomerId());

        // Convert persisted entity back to domain object.
        return CustomerEntityMapper.toDomain(savedEntity);
    }

    /**
     * Finds a customer using its domain CustomerId.
     */
    @Override
    public Optional<Customer> findById(
            CustomerId customerId) {

        log.info("Querying customer repository for customerId={}", customerId.value());

        Optional<Customer> customer = customerJpaRepository
                .findById(customerId.value())
                .map(CustomerEntityMapper::toDomain);

        if (customer.isPresent()) {
            log.info("Customer lookup succeeded for customerId={}", customerId.value());
        } else {
            log.warn("Customer lookup returned no result for customerId={}", customerId.value());
        }

        return customer;
    }

    /**
     * Checks whether a customer exists.
     */
    @Override
    public boolean existsById(
            CustomerId customerId) {

        boolean exists = customerJpaRepository
                .existsById(customerId.value());

        log.info("Customer existence check for customerId={} => {}", customerId.value(), exists);

        return exists;
    }

    /**
     * Deletes a customer.
     */
    @Override
    public void delete(Customer customer) {

        log.info("Deleting customer aggregate with customerId={}", customer.customerId().value());

        // Convert domain aggregate to JPA entity.
        CustomerEntity entity = CustomerEntityMapper.toEntity(customer);

        // Delete the entity from the database.
        customerJpaRepository.delete(entity);

        log.info("Customer aggregate deleted for customerId={}", customer.customerId().value());
    }

    @Override
    public boolean existsByEmailAddress(String emailAddress) {

        boolean exists = customerJpaRepository
                .existsByEmailAddress(emailAddress);

        log.info("Customer email existence check completed => {}", exists);

        return exists;
    }

    @Override
    public boolean existsByMobileNumber(String mobileNumber) {

        boolean exists = customerJpaRepository
                .existsByMobileNumber(mobileNumber);

        log.info("Customer mobile existence check completed => {}", exists);

        return exists;
    }
}