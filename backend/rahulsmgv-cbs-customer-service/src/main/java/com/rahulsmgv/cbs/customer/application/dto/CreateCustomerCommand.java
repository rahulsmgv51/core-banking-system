package com.rahulsmgv.cbs.customer.application.dto;

import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Command used to create a new customer.
 *
 * This DTO belongs to the application layer.
 *
 * Bean Validation annotations protect the application
 * boundary by rejecting invalid input before the
 * application service is executed.
 */
public record CreateCustomerCommand(

        /**
         * Customer full name.
         */
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        /**
         * Customer classification.
         */
        @NotNull(message = "Customer type is required")
        CustomerType customerType,

        /**
         * Customer email address.
         */
        @NotBlank(message = "Email address is required")
        @Email(message = "Email address must be valid")
        @Size(max = 254, message = "Email address must not exceed 254 characters")
        String emailAddress,

        /**
         * Customer mobile number.
         *
         * Allows an optional '+' followed by 10 to 15 digits.
         */
        @NotBlank(message = "Mobile number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Mobile number must contain 10 to 15 digits")
        String mobileNumber,

        /**
         * Primary address line.
         */
        @NotBlank(message = "Address line 1 is required")
        @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
        String addressLine1,

        /**
         * Secondary address line.
         */
        @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
        String addressLine2,

        /**
         * City.
         */
        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        /**
         * State.
         */
        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        String state,

        /**
         * Postal code.
         */
        @NotBlank(message = "Postal code is required")
        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode,

        /**
         * Country.
         */
        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country) {
}