package com.rahulsmgv.cbs.customer.controller;

import com.rahulsmgv.cbs.customer.application.dto.CreateCustomerCommand;
import com.rahulsmgv.cbs.customer.application.dto.CustomerResponse;
import com.rahulsmgv.cbs.customer.application.dto.UpdateCustomerCommand;
import com.rahulsmgv.cbs.customer.application.service.CustomerApplicationService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for Customer APIs.
 *
 * This class is part of the presentation/API layer.
 *
 * Responsibility:
 * - Receive HTTP requests
 * - Convert request data into application commands
 * - Call the application service
 * - Return HTTP responses
 *
 * Business logic must NOT be implemented here.
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerApplicationService customerApplicationService;

    /**
     * Constructor injection.
     *
     * Spring automatically injects the
     * CustomerApplicationService bean.
     */
    public CustomerController(CustomerApplicationService customerApplicationService) {

        this.customerApplicationService = customerApplicationService;
    }

    /**
     * Creates a new customer.
     *
     * HTTP:
     * POST /api/v1/customers
     *
     * Request body contains customer information.
     *
     * The controller converts the request into
     * CreateCustomerCommand and passes it to
     * the application service.
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerCommand command) {

        log.info(
                "Execution step started: createCustomer for email={}, mobile={}",
                command.emailAddress(),
                command.mobileNumber());

        // Execute the Create Customer use case.
        CustomerResponse response = customerApplicationService
                .createCustomer(command);

        log.info(
                "Execution step completed: createCustomer for customerId={}",
                response.customerId());

        // Return HTTP 201 CREATED with the created customer.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves a customer by ID.
     *
     * HTTP:
     * GET /api/v1/customers/{customerId}
     */
    @GetMapping("/{customerId}")
        public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("customerId") Long customerId) {

        log.info("Execution step started: getCustomer for customerId={}", customerId);

        CustomerResponse response = customerApplicationService
                .getCustomer(customerId);

        log.info("Execution step completed: getCustomer for customerId={}", customerId);

        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing customer.
     *
     * HTTP:
     * PATCH /api/v1/customers/{customerId}
     *
     * The controller is responsible only for:
     * - Receiving the HTTP request
     * - Passing the request data to the application layer
     * - Returning the HTTP response
     *
     * Business logic remains inside the application
     * service and domain aggregate.
     */
    @PatchMapping("/{customerId}")
        public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable("customerId") Long customerId,
            @Valid @RequestBody UpdateCustomerCommand command) {

        log.info(
                "Execution step started: updateCustomer for customerId={}, email={}",
                customerId,
                command.emailAddress());

        // Convert the path variable into UUID.
        Long id = customerId;

        // Execute the Update Customer use case.
        CustomerResponse response = customerApplicationService.updateCustomer(id, command);

        log.info("Execution step completed: updateCustomer for customerId={}", customerId);

        // Return HTTP 200 OK with the updated customer.
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an existing customer.
     *
     * HTTP:
     * DELETE /api/v1/customers/{customerId}
     *
     * The controller only handles HTTP concerns.
     * The actual delete operation is delegated to
     * the application service.
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable("customerId") Long customerId) {

        log.info(
                "Execution step started: deleteCustomer for customerId={}",
                customerId);

        // Convert the path variable into UUID.
        Long id = customerId;

        // Execute the Delete Customer use case.
        customerApplicationService.deleteCustomer(id);

        log.info(
                "Execution step completed: deleteCustomer for customerId={}",
                customerId);

        // Customer was successfully deleted.
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a customer.
     *
     * HTTP:
     * POST /api/v1/customers/{customerId}/activate
     */
    @PostMapping("/{customerId}/activate")
    public ResponseEntity<CustomerResponse> activateCustomer(
            @PathVariable("customerId") Long customerId) {

        log.info(
                "Execution step started: activateCustomer for customerId={}",
                customerId);

        Long id = customerId;

        CustomerResponse response = customerApplicationService.activateCustomer(id);

        log.info(
                "Execution step completed: activateCustomer for customerId={}, status={}",
                customerId,
                response.status());

        return ResponseEntity.ok(response);
    }

    /**
     * Suspends a customer.
     *
     * HTTP:
     * POST /api/v1/customers/{customerId}/suspend
     */
    @PostMapping("/{customerId}/suspend")
    public ResponseEntity<CustomerResponse> suspendCustomer(
            @PathVariable("customerId") Long customerId) {

        log.info(
                "Execution step started: suspendCustomer for customerId={}",
                customerId);

        Long id = customerId;

        CustomerResponse response = customerApplicationService.suspendCustomer(id);

        log.info(
                "Execution step completed: suspendCustomer for customerId={}, status={}",
                customerId,
                response.status());

        return ResponseEntity.ok(response);
    }

    /**
     * Blocks a customer.
     *
     * HTTP:
     * POST /api/v1/customers/{customerId}/block
     */
    @PostMapping("/{customerId}/block")
    public ResponseEntity<CustomerResponse> blockCustomer(
            @PathVariable("customerId") Long customerId) {

        log.info(
                "Execution step started: blockCustomer for customerId={}",
                customerId);

        Long id = customerId;

        CustomerResponse response = customerApplicationService.blockCustomer(id);

        log.info(
                "Execution step completed: blockCustomer for customerId={}, status={}",
                customerId,
                response.status());

        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates a customer.
     *
     * HTTP:
     * POST /api/v1/customers/{customerId}/deactivate
     */
    @PostMapping("/{customerId}/deactivate")
    public ResponseEntity<CustomerResponse> deactivateCustomer(
            @PathVariable("customerId") Long customerId) {

        log.info(
                "Execution step started: deactivateCustomer for customerId={}",
                customerId);

        Long id = customerId;

        CustomerResponse response = customerApplicationService.deactivateCustomer(id);

        log.info(
                "Execution step completed: deactivateCustomer for customerId={}, status={}",
                customerId,
                response.status());

        return ResponseEntity.ok(response);
    }

    /**
     * Closes a customer.
     *
     * HTTP:
     * POST /api/v1/customers/{customerId}/close
     */
    @PostMapping("/{customerId}/close")
    public ResponseEntity<CustomerResponse> closeCustomer(
            @PathVariable("customerId") Long customerId) {

        log.info(
                "Execution step started: closeCustomer for customerId={}",
                customerId);

        Long id = customerId;

        CustomerResponse response = customerApplicationService.closeCustomer(id);

        log.info(
                "Execution step completed: closeCustomer for customerId={}, status={}",
                customerId,
                response.status());

        return ResponseEntity.ok(response);
    }

}