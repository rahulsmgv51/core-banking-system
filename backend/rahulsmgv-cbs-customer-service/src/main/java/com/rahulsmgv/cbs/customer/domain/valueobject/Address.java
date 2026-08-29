package com.rahulsmgv.cbs.customer.domain.valueobject;

import java.util.Objects;

public final class Address {

    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    private Address(
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country) {

        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public static Address of(
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country) {

        requireNonBlank(addressLine1, "Address line 1");
        requireNonBlank(city, "City");
        requireNonBlank(state, "State");
        requireNonBlank(postalCode, "Postal code");
        requireNonBlank(country, "Country");

        return new Address(
                normalize(addressLine1),
                normalizeNullable(addressLine2),
                normalize(city),
                normalize(state),
                normalize(postalCode),
                normalize(country));
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null or blank");
        }
    }

    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private static String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return normalize(value);
    }

    public String addressLine1() {
        return addressLine1;
    }

    public String addressLine2() {
        return addressLine2;
    }

    public String city() {
        return city;
    }

    public String state() {
        return state;
    }

    public String postalCode() {
        return postalCode;
    }

    public String country() {
        return country;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Address other)) {
            return false;
        }

        return Objects.equals(addressLine1, other.addressLine1)
                && Objects.equals(addressLine2, other.addressLine2)
                && Objects.equals(city, other.city)
                && Objects.equals(state, other.state)
                && Objects.equals(postalCode, other.postalCode)
                && Objects.equals(country, other.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                addressLine1,
                addressLine2,
                city,
                state,
                postalCode,
                country);
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}