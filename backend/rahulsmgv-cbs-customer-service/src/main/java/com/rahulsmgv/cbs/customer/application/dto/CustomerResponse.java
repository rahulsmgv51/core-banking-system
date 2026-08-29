package com.rahulsmgv.cbs.customer.application.dto;

import com.rahulsmgv.cbs.customer.domain.enums.CustomerStatus;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;

import java.time.Instant;

public record CustomerResponse(

        Long customerId,

        String name,

        CustomerType customerType,

        CustomerStatus status,

        String emailAddress,

        String mobileNumber,

        String addressLine1,

        String addressLine2,

        String city,

        String state,

        String postalCode,

        String country,

        Instant createdAt,

        Instant updatedAt) {
}