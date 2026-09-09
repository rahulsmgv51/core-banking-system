package com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.adapter;

import com.rahulsmgv.cbs.account.domain.enums.AccountStatus;
import com.rahulsmgv.cbs.account.domain.enums.AccountType;
import com.rahulsmgv.cbs.account.domain.model.Account;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.Balance;
import com.rahulsmgv.cbs.account.domain.valueobject.Currency;
import com.rahulsmgv.cbs.account.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.repository.AccountJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AccountRepositoryAdapterIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @BeforeEach
    void setUp() {
        accountJpaRepository.deleteAll();
        accountJpaRepository.flush();
        entityManager.clear();
    }

    @Test
    void shouldSaveAndFindAccount() {

        Account account = Account.create(
                AccountId.of(10000000002L),
                AccountNumber.of("100000000002"),
                CustomerId.of(10000000001L),
                AccountType.SAVINGS,
                Currency.inr());

        Account savedAccount =
                accountRepositoryAdapter.save(account);

        assertNotNull(savedAccount);
        assertEquals(
                10000000002L,
                savedAccount.accountId().value());

        assertEquals(
                "100000000002",
                savedAccount.accountNumber().value());

        assertEquals(
                10000000001L,
                savedAccount.customerId().value());

        assertEquals(
                AccountType.SAVINGS,
                savedAccount.accountType());

        assertEquals(
                AccountStatus.PENDING,
                savedAccount.status());

        assertEquals(
                "INR",
                savedAccount.currency().code());

        assertEquals(
                BigDecimal.ZERO,
                savedAccount.balance().amount());

        Optional<Account> result =
                accountRepositoryAdapter.findById(
                        savedAccount.accountId());

        assertTrue(result.isPresent());

        Account retrievedAccount =
                result.orElseThrow();

        assertEquals(
                savedAccount.accountId(),
                retrievedAccount.accountId());

        assertEquals(
                savedAccount.accountNumber(),
                retrievedAccount.accountNumber());

        assertEquals(
                savedAccount.customerId(),
                retrievedAccount.customerId());

        assertEquals(
                savedAccount.accountType(),
                retrievedAccount.accountType());

        assertEquals(
                savedAccount.status(),
                retrievedAccount.status());

        assertEquals(
                savedAccount.currency(),
                retrievedAccount.currency());

        assertEquals(
                savedAccount.balance().amount(),
                retrievedAccount.balance().amount());
    }

    @Test
    void shouldFindAccountByAccountNumber() {

        Account account = Account.create(
                AccountId.of(10000000003L),
                AccountNumber.of("100000000003"),
                CustomerId.of(10000000001L),
                AccountType.CURRENT,
                Currency.inr());

        accountRepositoryAdapter.save(account);

        Optional<Account> result =
                accountRepositoryAdapter.findByAccountNumber(
                        AccountNumber.of("100000000003"));

        assertTrue(result.isPresent());

        Account retrievedAccount =
                result.orElseThrow();

        assertEquals(
                10000000003L,
                retrievedAccount.accountId().value());

        assertEquals(
                "100000000003",
                retrievedAccount.accountNumber().value());

        assertEquals(
                AccountType.CURRENT,
                retrievedAccount.accountType());
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {

        Optional<Account> result =
                accountRepositoryAdapter.findById(
                        AccountId.of(99999999999L));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCheckAccountNumberExists() {

        Account account = Account.create(
                AccountId.of(10000000004L),
                AccountNumber.of("100000000004"),
                CustomerId.of(10000000001L),
                AccountType.SAVINGS,
                Currency.inr());

        accountRepositoryAdapter.save(account);

        assertTrue(
                accountRepositoryAdapter.existsByAccountNumber(
                        AccountNumber.of("100000000004")));

        assertFalse(
                accountRepositoryAdapter.existsByAccountNumber(
                        AccountNumber.of("100000000099")));
    }

    @Test
    void shouldCheckCustomerAndAccountTypeExists() {

        Account account = Account.create(
                AccountId.of(10000000005L),
                AccountNumber.of("100000000005"),
                CustomerId.of(10000000002L),
                AccountType.SAVINGS,
                Currency.inr());

        accountRepositoryAdapter.save(account);

        assertTrue(
                accountRepositoryAdapter
                        .existsByCustomerIdAndAccountType(
                                CustomerId.of(10000000002L),
                                AccountType.SAVINGS.name()));

        assertFalse(
                accountRepositoryAdapter
                        .existsByCustomerIdAndAccountType(
                                CustomerId.of(10000000002L),
                                AccountType.CURRENT.name()));

        assertFalse(
                accountRepositoryAdapter
                        .existsByCustomerIdAndAccountType(
                                CustomerId.of(10000000003L),
                                AccountType.SAVINGS.name()));
    }

    @Test
    void shouldPersistActivatedAccount() {

        Account account = Account.create(
                AccountId.of(10000000006L),
                AccountNumber.of("100000000006"),
                CustomerId.of(10000000003L),
                AccountType.SAVINGS,
                Currency.inr());

        account.activate();

        Account savedAccount =
                accountRepositoryAdapter.save(account);

        Optional<Account> result =
                accountRepositoryAdapter.findById(
                        savedAccount.accountId());

        assertTrue(result.isPresent());

        Account retrievedAccount =
                result.orElseThrow();

        assertEquals(
                AccountStatus.ACTIVE,
                retrievedAccount.status());
    }

    @Test
    void shouldPersistAccountBalance() {

        Account account = Account.create(
                AccountId.of(10000000007L),
                AccountNumber.of("100000000007"),
                CustomerId.of(10000000004L),
                AccountType.SAVINGS,
                Currency.inr());

        account.activate();
        account.credit(new BigDecimal("5000.00"));

        Account savedAccount =
                accountRepositoryAdapter.save(account);

        Optional<Account> result =
                accountRepositoryAdapter.findById(
                        savedAccount.accountId());

        assertTrue(result.isPresent());

        Account retrievedAccount =
                result.orElseThrow();

        assertEquals(
                new BigDecimal("5000.00"),
                retrievedAccount.balance().amount());

        assertEquals(
                AccountStatus.ACTIVE,
                retrievedAccount.status());
    }
}
