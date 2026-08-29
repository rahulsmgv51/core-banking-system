package com.rahulsmgv.cbs.customer.exception;

/**
 * Thrown when an attempt is made to create a customer
 * that already exists according to the configured
 * customer deduplication rules.
 */
public class DuplicateCustomerException extends RuntimeException {

    public DuplicateCustomerException(String message) {
        super(message);
    }
}