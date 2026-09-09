package com.rahulsmgv.cbs.account.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahulsmgv.cbs.account.domain.enums.AccountType;
import com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.repository.AccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @BeforeEach
    void setUp() {
        accountJpaRepository.deleteAll();
        accountJpaRepository.flush();
    }

    @Test
    void shouldCreateAccount() throws Exception {

        String request = """
                {
                    "customerId": 10000000001,
                    "accountType": "SAVINGS",
                    "currency": "INR"
                }
                """;

        mockMvc.perform(
                post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").isNumber())
                .andExpect(jsonPath("$.customerId")
                        .value(10000000001L))
                .andExpect(jsonPath("$.accountNumber").isString())
                .andExpect(jsonPath("$.accountType")
                        .value("SAVINGS"))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"))
                .andExpect(jsonPath("$.currency")
                        .value("INR"));
    }

    @Test
    void shouldGetAccountById() throws Exception {

        String request = """
                {
                    "customerId": 10000000002,
                    "accountType": "SAVINGS",
                    "currency": "INR"
                }
                """;

        String response =
                mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode json =
                objectMapper.readTree(response);

        long accountId =
                json.get("accountId").asLong();

        mockMvc.perform(
                get("/api/v1/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId")
                        .value(accountId))
                .andExpect(jsonPath("$.customerId")
                        .value(10000000002L))
                .andExpect(jsonPath("$.accountType")
                        .value("SAVINGS"))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"))
                .andExpect(jsonPath("$.currency")
                        .value("INR"));
    }

    @Test
    void shouldGetAccountByAccountNumber() throws Exception {

        String request = """
                {
                    "customerId": 10000000003,
                    "accountType": "CURRENT",
                    "currency": "INR"
                }
                """;

        String response =
                mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode json =
                objectMapper.readTree(response);

        String accountNumber =
                json.get("accountNumber").asText();

        mockMvc.perform(
                get("/api/v1/accounts/number/" + accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber")
                        .value(accountNumber))
                .andExpect(jsonPath("$.customerId")
                        .value(10000000003L))
                .andExpect(jsonPath("$.accountType")
                        .value("CURRENT"));
    }

    @Test
    void shouldActivateAccount() throws Exception {

        String request = """
                {
                    "customerId": 10000000004,
                    "accountType": "SAVINGS",
                    "currency": "INR"
                }
                """;

        String response =
                mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        long accountId =
                objectMapper.readTree(response)
                        .get("accountId")
                        .asLong();

        mockMvc.perform(
                post("/api/v1/accounts/"
                        + accountId
                        + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId")
                        .value(accountId))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));
    }

    @Test
    void shouldFreezeAccount() throws Exception {

        String request = """
                {
                    "customerId": 10000000005,
                    "accountType": "SAVINGS",
                    "currency": "INR"
                }
                """;

        String response =
                mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        long accountId =
                objectMapper.readTree(response)
                        .get("accountId")
                        .asLong();

        mockMvc.perform(
                post("/api/v1/accounts/"
                        + accountId
                        + "/freeze"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("FROZEN"));
    }

    @Test
    void shouldMakeAccountDormant() throws Exception {

        String request = """
                {
                    "customerId": 10000000006,
                    "accountType": "SAVINGS",
                    "currency": "INR"
                }
                """;

        String response =
                mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        long accountId =
                objectMapper.readTree(response)
                        .get("accountId")
                        .asLong();

        mockMvc.perform(
                post("/api/v1/accounts/"
                        + accountId
                        + "/dormant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("DORMANT"));
    }

    @Test
    void shouldCloseAccount() throws Exception {

        String request = """
                {
                    "customerId": 10000000007,
                    "accountType": "SAVINGS",
                    "currency": "INR"
                }
                """;

        String response =
                mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        long accountId =
                objectMapper.readTree(response)
                        .get("accountId")
                        .asLong();

        mockMvc.perform(
                post("/api/v1/accounts/"
                        + accountId
                        + "/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("CLOSED"));
    }

    @Test
    void shouldRejectDuplicateAccountTypeForCustomer()
            throws Exception {

        String request = """
                {
                    "customerId": 10000000008,
                    "accountType": "SAVINGS",
                    "currency": "INR"
                }
                """;

        mockMvc.perform(
                post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(
        post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isConflict());  // 409 Conflict
    }
}
