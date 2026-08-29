package com.rahulsmgv.cbs.customer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahulsmgv.cbs.customer.domain.enums.CustomerType;
import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.repository.CustomerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerJpaRepository customerJpaRepository;

    @BeforeEach
    void setUp() {
        customerJpaRepository.deleteAll();
    }

    @Test
    void shouldCreateCustomer() throws Exception {

        String request = """
                {
                    "name": "Rahul Kumar",
                    "customerType": "INDIVIDUAL",
                    "emailAddress": "rahul.create@example.com",
                    "mobileNumber": "+919876543210",
                    "addressLine1": "123 Main Road",
                    "addressLine2": "Sector 10",
                    "city": "Gurgaon",
                    "state": "Haryana",
                    "postalCode": "122001",
                    "country": "India"
                }
                """;

        mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.name").value("Rahul Kumar"))
                .andExpect(jsonPath("$.customerType")
                        .value(CustomerType.INDIVIDUAL.name()))
                .andExpect(jsonPath("$.status").value("PROSPECT"))
                .andExpect(jsonPath("$.emailAddress")
                        .value("rahul.create@example.com"))
                .andExpect(jsonPath("$.mobileNumber")
                        .value("+919876543210"));
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {

        String firstRequest = """
                {
                    "name": "Rahul Kumar",
                    "customerType": "INDIVIDUAL",
                    "emailAddress": "duplicate@example.com",
                    "mobileNumber": "+919876543210",
                    "addressLine1": "123 Main Road",
                    "addressLine2": "Sector 10",
                    "city": "Gurgaon",
                    "state": "Haryana",
                    "postalCode": "122001",
                    "country": "India"
                }
                """;

        String duplicateRequest = """
                {
                    "name": "Another Customer",
                    "customerType": "INDIVIDUAL",
                    "emailAddress": "duplicate@example.com",
                    "mobileNumber": "+919876543211",
                    "addressLine1": "456 Another Road",
                    "addressLine2": "Sector 20",
                    "city": "Gurgaon",
                    "state": "Haryana",
                    "postalCode": "122001",
                    "country": "India"
                }
                """;

        mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Customer already exists with email address: duplicate@example.com"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/customers"));
    }

    @Test
    void shouldRejectDuplicateMobileNumber() throws Exception {

        String firstRequest = """
                {
                    "name": "Rahul Kumar",
                    "customerType": "INDIVIDUAL",
                    "emailAddress": "rahul.mobile@example.com",
                    "mobileNumber": "+919876543212",
                    "addressLine1": "123 Main Road",
                    "addressLine2": "Sector 10",
                    "city": "Gurgaon",
                    "state": "Haryana",
                    "postalCode": "122001",
                    "country": "India"
                }
                """;

        String duplicateRequest = """
                {
                    "name": "Another Customer",
                    "customerType": "INDIVIDUAL",
                    "emailAddress": "another.mobile@example.com",
                    "mobileNumber": "+919876543212",
                    "addressLine1": "456 Another Road",
                    "addressLine2": "Sector 20",
                    "city": "Gurgaon",
                    "state": "Haryana",
                    "postalCode": "122001",
                    "country": "India"
                }
                """;

        mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Customer already exists with mobile number: +919876543212"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/customers"));
    }

    @Test
    void shouldGetCustomerById() throws Exception {

        String request = """
                {
                    "name": "Rahul Kumar",
                    "customerType": "INDIVIDUAL",
                    "emailAddress": "rahul.get@example.com",
                    "mobileNumber": "+919876543213",
                    "addressLine1": "123 Main Road",
                    "addressLine2": "Sector 10",
                    "city": "Gurgaon",
                    "state": "Haryana",
                    "postalCode": "122001",
                    "country": "India"
                }
                """;

        String response = mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        String customerId = json.get("customerId").asText();

        mockMvc.perform(
                get("/api/v1/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.name").value("Rahul Kumar"))
                .andExpect(jsonPath("$.customerType").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.status").value("PROSPECT"))
                .andExpect(jsonPath("$.emailAddress")
                        .value("rahul.get@example.com"))
                .andExpect(jsonPath("$.mobileNumber")
                        .value("+919876543213"));
    }

    @Test
    void shouldReturnBadRequestWhenCustomerDoesNotExist() throws Exception {

        String randomCustomerId = "99999999999";

        mockMvc.perform(
                get("/api/v1/customers/{customerId}", randomCustomerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Customer not found: " + randomCustomerId));
    }
}