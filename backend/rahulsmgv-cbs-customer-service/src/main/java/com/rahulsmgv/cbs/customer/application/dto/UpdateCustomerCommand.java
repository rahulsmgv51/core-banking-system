package com.rahulsmgv.cbs.customer.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Command used to update customer information.
 *
 * This DTO belongs to the application layer.
 *
 * Bean Validation annotations protect the application
 * boundary by rejecting invalid input before the
 * application service is executed.
 */
public record UpdateCustomerCommand(

        /**
         * Updated customer name.
         */
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        /**
         * Updated customer email address.
         */
        @NotBlank(message = "Email address is required")
        @Email(message = "Email address must be valid")
        @Size(max = 254, message = "Email address must not exceed 254 characters")
        String emailAddress,

        /**
         * Updated customer mobile number.
         */
        @NotBlank(message = "Mobile number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Mobile number must contain 10 to 15 digits")
        String mobileNumber,

        /**
         * Updated primary address line.
         */
        @NotBlank(message = "Address line 1 is required")
        @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
        String addressLine1,

        /**
         * Updated secondary address line.
         */
        @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
        String addressLine2,

        /**
         * Updated city.
         */
        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        /**
         * Updated state.
         */
        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        String state,

        /**
         * Updated postal code.
         */
        @NotBlank(message = "Postal code is required")
        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode,

        /**
         * Updated country.
         */
        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country) {
}
