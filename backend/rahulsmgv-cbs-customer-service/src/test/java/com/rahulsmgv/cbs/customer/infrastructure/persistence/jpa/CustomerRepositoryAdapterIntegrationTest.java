package com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa;

import com.rahulsmgv.cbs.customer.domain.enums.CustomerStatus;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;
import com.rahulsmgv.cbs.customer.domain.model.Customer;
import com.rahulsmgv.cbs.customer.domain.valueobject.Address;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerName;
import com.rahulsmgv.cbs.customer.domain.valueobject.EmailAddress;
import com.rahulsmgv.cbs.customer.domain.valueobject.MobileNumber;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.adapter.CustomerRepositoryAdapter;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.repository.CustomerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import org.springframework.test.annotation.Rollback;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the Customer persistence layer.
 *
 * This test verifies the complete persistence flow:
 *
 * Customer Domain
 * ↓
 * CustomerRepositoryAdapter
 * ↓
 * CustomerJpaRepository
 * ↓
 * CustomerEntity
 * ↓
 * PostgreSQL
 */
@SpringBootTest
@Transactional
class CustomerRepositoryAdapterIntegrationTest {
        @Autowired
        private EntityManager entityManager;

        /**
         * Real persistence adapter managed by Spring.
         */
        @Autowired
        private CustomerRepositoryAdapter customerRepositoryAdapter;

        /**
         * Spring Data JPA repository.
         *
         * We use this to clean the database before every test.
         */
        @Autowired
        private CustomerJpaRepository customerJpaRepository;

        /**
         * Clean the customer table before each test.
         *
         * This prevents one test from affecting another.
         */
        @BeforeEach
        void setUp() {

                customerJpaRepository.deleteAll();
                customerJpaRepository.flush();

                entityManager.clear();
        }

        /**
         * Tests that a Customer domain object can be
         * persisted and retrieved successfully.
         */
        @Test
        void shouldSaveAndFindCustomer() {

                // Create a Customer domain aggregate.
                Customer customer = Customer.create(
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

                // Save the domain object through the application port.
                Customer savedCustomer = customerRepositoryAdapter.save(customer);

                // Verify that the customer received a valid ID.
                assertNotNull(savedCustomer.customerId());

                // Verify initial lifecycle status.
                assertEquals(
                                CustomerStatus.PROSPECT,
                                savedCustomer.status());

                // Retrieve the customer using the domain repository.
                Optional<Customer> result = customerRepositoryAdapter.findById(
                                savedCustomer.customerId());

                // Verify that the customer exists.
                assertTrue(result.isPresent());

                Customer retrievedCustomer = result.orElseThrow();

                // Verify persisted values.
                assertEquals(
                                savedCustomer.customerId(),
                                retrievedCustomer.customerId());

                assertEquals(
                                "Rahul Kumar",
                                retrievedCustomer.name().value());

                assertEquals(
                                "rahul@example.com",
                                retrievedCustomer.emailAddress().value());

                assertEquals(
                                "+919876543210",
                                retrievedCustomer.mobileNumber().value());

                assertEquals(
                                "Gurgaon",
                                retrievedCustomer.address().city());

                assertEquals(
                                CustomerStatus.PROSPECT,
                                retrievedCustomer.status());
        }

        /**
         * Tests the existsById operation.
         */
        @Test
        void shouldCheckCustomerExists() {

                // Create a customer.
                Customer customer = Customer.create(
                                CustomerId.of(10000000002L),
                                CustomerName.of("Rahul Kumar"),
                                CustomerType.INDIVIDUAL,
                                EmailAddress.of("rahul@example.com"),
                                MobileNumber.of("+919876543210"),
                                Address.of(
                                                "123 Main Road",
                                                null,
                                                "Gurgaon",
                                                "Haryana",
                                                "122001",
                                                "India"));

                // Persist the customer.
                Customer savedCustomer = customerRepositoryAdapter.save(customer);

                // Verify the customer exists.
                assertTrue(
                                customerRepositoryAdapter.existsById(
                                                savedCustomer.customerId()));
        }

        /**
         * Tests that a non-existing customer
         * cannot be found.
         */
        @Test
        void shouldReturnEmptyWhenCustomerDoesNotExist() {

                // Create a customer but deliberately do not save it.
                Customer customer = Customer.create(
                                CustomerId.of(10000000003L),
                                CustomerName.of("Rahul Kumar"),
                                CustomerType.INDIVIDUAL,
                                EmailAddress.of("rahul@example.com"),
                                MobileNumber.of("+919876543210"),
                                Address.of(
                                                "123 Main Road",
                                                null,
                                                "Gurgaon",
                                                "Haryana",
                                                "122001",
                                                "India"));

                // Search for the unsaved customer's ID.
                Optional<Customer> result = customerRepositoryAdapter.findById(
                                customer.customerId());

                // No record should be found.
                assertTrue(result.isEmpty());
        }
}