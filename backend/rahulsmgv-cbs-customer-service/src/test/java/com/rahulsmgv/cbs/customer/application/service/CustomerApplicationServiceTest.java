package com.rahulsmgv.cbs.customer.application.service;

import com.rahulsmgv.cbs.customer.application.dto.CreateCustomerCommand;
import com.rahulsmgv.cbs.customer.application.dto.CustomerResponse;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.memory.InMemoryCustomerRepository;
import com.rahulsmgv.cbs.customer.application.port.CustomerIdGenerator;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerStatus;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.rahulsmgv.cbs.customer.exception.DuplicateCustomerException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CustomerApplicationService.
 *
 * These tests verify the application/use-case layer
 * without using PostgreSQL, JPA, or Spring Boot.
 */
class CustomerApplicationServiceTest {

        private CustomerApplicationService customerApplicationService;

        /**
         * Creates a fresh application service before every test.
         *
         * We use the in-memory repository so that the test
         * does not depend on an external database.
         */
        @BeforeEach
        void setUp() {

                InMemoryCustomerRepository repository = new InMemoryCustomerRepository();

                CustomerIdGenerator customerIdGenerator = () -> com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId.of(10000000001L);
                customerApplicationService = new CustomerApplicationService(repository, customerIdGenerator);
        }

        /**
         * Tests successful customer creation.
         */
        @Test
        void shouldCreateCustomer() {

                // Prepare the input command.
                CreateCustomerCommand command = new CreateCustomerCommand(
                                "Rahul Kumar",
                                CustomerType.INDIVIDUAL,
                                "rahul@example.com",
                                "+919876543210",
                                "123 Main Road",
                                "Sector 10",
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                // Execute the create-customer use case.
                CustomerResponse response = customerApplicationService.createCustomer(command);

                // Verify that a customer ID was generated.
                assertNotNull(response.customerId());

                // Verify customer details.
                assertEquals(
                                "Rahul Kumar",
                                response.name());

                assertEquals(
                                CustomerType.INDIVIDUAL,
                                response.customerType());

                // A newly created customer starts as PROSPECT.
                assertEquals(
                                CustomerStatus.PROSPECT,
                                response.status());

                // Verify contact information.
                assertEquals(
                                "rahul@example.com",
                                response.emailAddress());

                assertEquals(
                                "+919876543210",
                                response.mobileNumber());

                // Verify address.
                assertEquals(
                                "123 Main Road",
                                response.addressLine1());

                assertEquals(
                                "Gurgaon",
                                response.city());

                assertEquals(
                                "Haryana",
                                response.state());

                assertEquals(
                                "122001",
                                response.postalCode());

                assertEquals(
                                "India",
                                response.country());

                // Verify timestamps.
                assertNotNull(response.createdAt());
                assertNotNull(response.updatedAt());
        }

        /**
         * Tests that the customer can be retrieved
         * after it has been created.
         */
        @Test
        void shouldGetCustomerById() {

                // Create a customer first.
                CreateCustomerCommand command = new CreateCustomerCommand(
                                "Rahul Kumar",
                                CustomerType.INDIVIDUAL,
                                "rahul@example.com",
                                "+919876543210",
                                "123 Main Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                CustomerResponse createdCustomer = customerApplicationService.createCustomer(command);

                // Retrieve the customer using the generated ID.
                CustomerResponse response = customerApplicationService.getCustomer(
                                createdCustomer.customerId());

                // Verify that the correct customer was returned.
                assertEquals(
                                createdCustomer.customerId(),
                                response.customerId());

                assertEquals(
                                "Rahul Kumar",
                                response.name());

                assertEquals(
                                CustomerStatus.PROSPECT,
                                response.status());
        }

        /**
         * Tests that requesting a non-existing customer
         * results in an exception.
         */
        @Test
        void shouldThrowExceptionWhenCustomerDoesNotExist() {

                // Generate an ID that does not exist in the repository.
                Long customerId = 10000000099L;

                // Verify that the application service throws
                // an exception when the customer is not found.
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> customerApplicationService
                                                .getCustomer(customerId));

                // Verify the exception message.
                assertTrue(
                                exception.getMessage()
                                                .contains("Customer not found"));
        }

        /**
         * Tests that invalid customer input is rejected.
         */
        @Test
        void shouldRejectInvalidEmail() {

                CreateCustomerCommand command = new CreateCustomerCommand(
                                "Rahul Kumar",
                                CustomerType.INDIVIDUAL,
                                "invalid-email",
                                "+919876543210",
                                "123 Main Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                // EmailAddress validation should reject the request.
                assertThrows(
                                IllegalArgumentException.class,
                                () -> customerApplicationService
                                                .createCustomer(command));
        }

        /**
         * Tests that invalid mobile numbers are rejected.
         */
        @Test
        void shouldRejectInvalidMobileNumber() {

                CreateCustomerCommand command = new CreateCustomerCommand(
                                "Rahul Kumar",
                                CustomerType.INDIVIDUAL,
                                "rahul@example.com",
                                "123",
                                "123 Main Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                // MobileNumber validation should reject the request.
                assertThrows(
                                IllegalArgumentException.class,
                                () -> customerApplicationService
                                                .createCustomer(command));
        }

        /**
         * Tests that blank customer names are rejected.
         */
        @Test
        void shouldRejectBlankCustomerName() {

                CreateCustomerCommand command = new CreateCustomerCommand(
                                " ",
                                CustomerType.INDIVIDUAL,
                                "rahul@example.com",
                                "+919876543210",
                                "123 Main Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                // CustomerName validation should reject the request.
                assertThrows(
                                IllegalArgumentException.class,
                                () -> customerApplicationService
                                                .createCustomer(command));
        }

        @Test
        void shouldRejectDuplicateEmailAddress() {

                CreateCustomerCommand firstCommand = new CreateCustomerCommand(
                                "Rahul Kumar",
                                CustomerType.INDIVIDUAL,
                                "rahul@example.com",
                                "+919876543210",
                                "123 Main Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                customerApplicationService.createCustomer(firstCommand);

                CreateCustomerCommand duplicateCommand = new CreateCustomerCommand(
                                "Another Customer",
                                CustomerType.INDIVIDUAL,
                                "rahul@example.com",
                                "+919876543211",
                                "456 Another Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                DuplicateCustomerException exception = assertThrows(
                                DuplicateCustomerException.class,
                                () -> customerApplicationService.createCustomer(duplicateCommand));

                assertTrue(
                                exception.getMessage()
                                                .contains("email address"));
        }

        @Test
        void shouldRejectDuplicateMobileNumber() {

                CreateCustomerCommand firstCommand = new CreateCustomerCommand(
                                "Rahul Kumar",
                                CustomerType.INDIVIDUAL,
                                "rahul@example.com",
                                "+919876543210",
                                "123 Main Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                customerApplicationService.createCustomer(firstCommand);

                CreateCustomerCommand duplicateCommand = new CreateCustomerCommand(
                                "Another Customer",
                                CustomerType.INDIVIDUAL,
                                "another@example.com",
                                "+919876543210",
                                "456 Another Road",
                                null,
                                "Gurgaon",
                                "Haryana",
                                "122001",
                                "India");

                DuplicateCustomerException exception = assertThrows(
                                DuplicateCustomerException.class,
                                () -> customerApplicationService.createCustomer(duplicateCommand));

                assertTrue(
                                exception.getMessage()
                                                .contains("mobile number"));
        }

}