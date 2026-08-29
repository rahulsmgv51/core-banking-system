package com.rahulsmgv.cbs.customer.application.service;

import com.rahulsmgv.cbs.customer.application.dto.CreateCustomerCommand;
import com.rahulsmgv.cbs.customer.application.dto.CustomerResponse;
import com.rahulsmgv.cbs.customer.application.dto.UpdateCustomerCommand;
import com.rahulsmgv.cbs.customer.application.port.CustomerRepository;
import com.rahulsmgv.cbs.customer.application.port.CustomerIdGenerator;
import com.rahulsmgv.cbs.customer.domain.model.Customer;
import com.rahulsmgv.cbs.customer.domain.valueobject.Address;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerName;
import com.rahulsmgv.cbs.customer.domain.valueobject.EmailAddress;
import com.rahulsmgv.cbs.customer.domain.valueobject.MobileNumber;
import com.rahulsmgv.cbs.customer.exception.DuplicateCustomerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service responsible for executing
 * Customer-related use cases.
 *
 * @Service tells Spring to create and manage this
 *          class as a Spring bean.
 */
@Service
public class CustomerApplicationService {

        private static final Logger log = LoggerFactory.getLogger(CustomerApplicationService.class);

        private final CustomerRepository customerRepository;
        private final CustomerIdGenerator customerIdGenerator;

        /**
         * Constructor injection.
         *
         * Spring automatically injects the implementation
         * of CustomerRepository.
         */
        public CustomerApplicationService(
                        CustomerRepository customerRepository,
                        CustomerIdGenerator customerIdGenerator) {
                this.customerRepository = customerRepository;
                this.customerIdGenerator = customerIdGenerator;
        }

        public CustomerResponse createCustomer(
                        CreateCustomerCommand command) {

                log.info("Creating customer");

                if (customerRepository.existsByEmailAddress(
                                command.emailAddress())) {

                        throw new DuplicateCustomerException(
                                        "Customer already exists with email address: "
                                                        + command.emailAddress());
                }

                if (customerRepository.existsByMobileNumber(
                                command.mobileNumber())) {

                        throw new DuplicateCustomerException(
                                        "Customer already exists with mobile number: "
                                                        + command.mobileNumber());
                }

                Customer customer = Customer.create(
                                customerIdGenerator.nextCustomerId(),
                                CustomerName.of(command.name()),
                                command.customerType(),
                                EmailAddress.of(command.emailAddress()),
                                MobileNumber.of(command.mobileNumber()),
                                Address.of(
                                                command.addressLine1(),
                                                command.addressLine2(),
                                                command.city(),
                                                command.state(),
                                                command.postalCode(),
                                                command.country()));

                Customer savedCustomer = customerRepository.save(customer);

                log.info(
                                "Customer persisted successfully with customerId={}",
                                savedCustomer.customerId().value());

                return toResponse(savedCustomer);
        }

        public CustomerResponse getCustomer(Long customerId) {

                CustomerId id = CustomerId.of(customerId);

                Customer customer = customerRepository
                                .findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                log.info("Customer retrieved successfully for customerId={}", customerId);

                return toResponse(customer);
        }

        /**
         * Updates an existing customer.
                                                        Customer customer = Customer.create(
                                                                        customerIdGenerator.nextCustomerId(),
         * This method represents the Update Customer use case.
         *
         * Responsibilities:
         * - Load the existing customer
         * - Check whether the new email/mobile belongs
         * to another customer
         * - Convert raw input into domain value objects
         * - Ask the Customer aggregate to perform the update
         * - Persist the updated aggregate
         * - Convert the result into a response DTO
         *
         * Business rules remain inside the domain aggregate
         * wherever possible.
         */
        public CustomerResponse updateCustomer(Long customerId, UpdateCustomerCommand command) {

                log.info("Updating customer for customerId={}", customerId);

                // Convert the UUID into the domain-specific CustomerId.
                CustomerId id = CustomerId.of(customerId);

                // Load the existing customer from the repository.
                Customer customer = customerRepository
                                .findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                /*
                 * Check whether the requested email already belongs
                 * to another customer.
                 *
                 * We must allow the customer to keep their own email.
                 */
                if (!customer.emailAddress().value()
                                .equalsIgnoreCase(command.emailAddress())
                                && customerRepository.existsByEmailAddress(
                                                command.emailAddress())) {

                        throw new DuplicateCustomerException(
                                        "Customer already exists with email address: " + command.emailAddress());
                }

                /*
                 * Check whether the requested mobile number already
                 * belongs to another customer.
                 *
                 * We must allow the customer to keep their own
                 * existing mobile number.
                 */
                if (!customer.mobileNumber().value()
                                .equals(command.mobileNumber())
                                && customerRepository.existsByMobileNumber(
                                                command.mobileNumber())) {

                        throw new DuplicateCustomerException(
                                        "Customer already exists with mobile number: " + command.mobileNumber());
                }

                /*
                 * Update the customer aggregate through domain methods.
                 *
                 * We do not modify fields directly because the aggregate
                 * owns its state and business rules.
                 */
                customer.updateName(
                                CustomerName.of(command.name()));

                customer.updateContactInformation(
                                EmailAddress.of(command.emailAddress()),
                                MobileNumber.of(command.mobileNumber()));

                customer.updateAddress(
                                Address.of(
                                                command.addressLine1(),
                                                command.addressLine2(),
                                                command.city(),
                                                command.state(),
                                                command.postalCode(),
                                                command.country()));

                // Persist the updated aggregate.
                Customer updatedCustomer = customerRepository.save(customer);

                log.info("Customer updated successfully with customerId={}", updatedCustomer.customerId().value());

                // Convert the domain object into an API response.
                return toResponse(updatedCustomer);
        }

        /**
         * Deletes an existing customer.
         *
         * This method implements the Delete Customer use case.
         *
         * The application service first retrieves the customer
         * from the repository and then asks the repository to
         * delete the aggregate.
         *
         * Business rules related to deletion can be added here
         * later if the CBS requires soft-delete or restrictions.
         *
         * @param customerId unique customer identifier
         */
        public void deleteCustomer(Long customerId) {

                log.info("Deleting customer with customerId={}", customerId);

                CustomerId id = CustomerId.of(customerId);

                Customer customer = customerRepository
                                .findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                customerRepository.delete(customer);

                log.info(
                                "Customer deleted successfully with customerId={}",
                                customerId);
        }

        private CustomerResponse toResponse(
                        Customer customer) {

                return new CustomerResponse(
                                customer.customerId().value(),
                                customer.name().value(),
                                customer.customerType(),
                                customer.status(),
                                customer.emailAddress().value(),
                                customer.mobileNumber().value(),
                                customer.address().addressLine1(),
                                customer.address().addressLine2(),
                                customer.address().city(),
                                customer.address().state(),
                                customer.address().postalCode(),
                                customer.address().country(),
                                customer.createdAt(),
                                customer.updatedAt());
        }

        /**
         * Activates a customer.
         *
         * The application service coordinates the use case,
         * while the Customer domain aggregate owns the
         * actual lifecycle transition rules.
         */
        public CustomerResponse activateCustomer(Long customerId) {

                log.info("Activating customer with customerId={}", customerId);

                Customer customer = customerRepository
                                .findById(CustomerId.of(customerId))
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                // Domain aggregate validates whether activation
                // is allowed from the current customer status.
                customer.activate();

                Customer savedCustomer = customerRepository.save(customer);

                log.info(
                                "Customer activated successfully with customerId={}",
                                customerId);

                return toResponse(savedCustomer);
        }

        /**
         * Suspends a customer.
         */
        public CustomerResponse suspendCustomer(Long customerId) {

                log.info("Suspending customer with customerId={}", customerId);

                Customer customer = customerRepository
                                .findById(CustomerId.of(customerId))
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                customer.suspend();

                Customer savedCustomer = customerRepository.save(customer);

                log.info(
                                "Customer suspended successfully with customerId={}",
                                customerId);

                return toResponse(savedCustomer);
        }

        /**
         * Blocks a customer.
         */
        public CustomerResponse blockCustomer(Long customerId) {

                log.info("Blocking customer with customerId={}", customerId);

                Customer customer = customerRepository
                                .findById(CustomerId.of(customerId))
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                customer.block();

                Customer savedCustomer = customerRepository.save(customer);

                log.info(
                                "Customer blocked successfully with customerId={}",
                                customerId);

                return toResponse(savedCustomer);
        }

        /**
         * Deactivates a customer.
         */
        public CustomerResponse deactivateCustomer(Long customerId) {

                log.info("Deactivating customer with customerId={}", customerId);

                Customer customer = customerRepository
                                .findById(CustomerId.of(customerId))
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                customer.deactivate();

                Customer savedCustomer = customerRepository.save(customer);

                log.info(
                                "Customer deactivated successfully with customerId={}",
                                customerId);

                return toResponse(savedCustomer);
        }

        /**
         * Closes a customer.
         *
         * Closing is a terminal lifecycle operation.
         */
        public CustomerResponse closeCustomer(Long customerId) {

                log.info("Closing customer with customerId={}", customerId);

                Customer customer = customerRepository
                                .findById(CustomerId.of(customerId))
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found: " + customerId));

                customer.close();

                Customer savedCustomer = customerRepository.save(customer);

                log.info(
                                "Customer closed successfully with customerId={}",
                                customerId);

                return toResponse(savedCustomer);
        }

}