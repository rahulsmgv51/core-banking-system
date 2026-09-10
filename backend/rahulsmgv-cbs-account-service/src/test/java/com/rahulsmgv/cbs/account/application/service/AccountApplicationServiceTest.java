package com.rahulsmgv.cbs.account.application.service;

import com.rahulsmgv.cbs.account.application.dto.AccountResponse;
import com.rahulsmgv.cbs.account.application.dto.CreateAccountCommand;
import com.rahulsmgv.cbs.account.application.port.AccountIdGenerator;
import com.rahulsmgv.cbs.account.application.port.AccountNumberGenerator;
import com.rahulsmgv.cbs.account.application.port.AccountRepository;
import com.rahulsmgv.cbs.account.domain.enums.AccountStatus;
import com.rahulsmgv.cbs.account.domain.enums.AccountType;
import com.rahulsmgv.cbs.account.domain.model.Account;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.rahulsmgv.cbs.account.exception.AccountNotFoundException;
import com.rahulsmgv.cbs.account.exception.DuplicateAccountException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountApplicationServiceTest {

        @Mock
        private AccountRepository accountRepository;

        @Mock
        private AccountIdGenerator accountIdGenerator;

        @Mock
        private AccountNumberGenerator accountNumberGenerator;

        private AccountApplicationService service;

        @BeforeEach
        void setUp() {
                service = new AccountApplicationService(
                                accountRepository,
                                accountIdGenerator,
                                accountNumberGenerator);
        }

        @Test
        void shouldCreateAccount() {

                CreateAccountCommand command = new CreateAccountCommand(
                                10000000017L,
                                AccountType.SAVINGS,
                                "INR");

                when(accountRepository.existsByCustomerIdAndAccountType(
                                any(CustomerId.class),
                                eq(AccountType.SAVINGS.name())))
                                .thenReturn(false);

                when(accountIdGenerator.nextAccountId())
                                .thenReturn(1L);

                when(accountNumberGenerator.nextAccountNumber())
                                .thenReturn("123456789012");

                when(accountRepository.existsByAccountNumber(
                                any(AccountNumber.class)))
                                .thenReturn(false);

                when(accountRepository.save(any(Account.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                AccountResponse response = service.create(command);

                assertNotNull(response);
                assertEquals(1L, response.accountId());
                assertEquals(10000000017L, response.customerId());
                assertEquals("123456789012", response.accountNumber());
                assertEquals(AccountType.SAVINGS, response.accountType());
                assertEquals(AccountStatus.PENDING, response.status());
                assertEquals("INR", response.currency());

                verify(accountRepository).save(any(Account.class));
        }

        @Test
        void shouldRejectDuplicateCustomerAccountType() {
                CreateAccountCommand command = new CreateAccountCommand(10000000017L, AccountType.SAVINGS, "INR");
                when(accountRepository.existsByCustomerIdAndAccountType(any(CustomerId.class),
                                eq(AccountType.SAVINGS.name()))).thenReturn(true);
                DuplicateAccountException exception = assertThrows(DuplicateAccountException.class,
                                () -> service.create(command));
                assertEquals("Account already exists for customer and account type", exception.getMessage());
                verify(accountRepository, never()).save(any());
                verify(accountIdGenerator, never()).nextAccountId();
        }

        @Test
        void shouldRejectDuplicateAccountNumber() {
                CreateAccountCommand command = new CreateAccountCommand(10000000017L, AccountType.SAVINGS, "INR");
                when(accountRepository.existsByCustomerIdAndAccountType(any(CustomerId.class),
                                eq(AccountType.SAVINGS.name()))).thenReturn(false);
                when(accountIdGenerator.nextAccountId()).thenReturn(1L);
                when(accountNumberGenerator.nextAccountNumber()).thenReturn("123456789012");
                when(accountRepository.existsByAccountNumber(any(AccountNumber.class))).thenReturn(true);
                DuplicateAccountException exception = assertThrows(DuplicateAccountException.class,
                                () -> service.create(command));
                assertEquals("Account number already exists: 123456789012", exception.getMessage());
                verify(accountRepository, never()).save(any());
        }

        @Test
        void shouldGetAccountById() {

                Account account = createAccount();

                when(accountRepository.findById(AccountId.of(1L)))
                                .thenReturn(Optional.of(account));

                AccountResponse response = service.getById(1L);

                assertEquals(1L, response.accountId());
                assertEquals(10000000017L, response.customerId());
                assertEquals("123456789012", response.accountNumber());
                assertEquals(AccountType.SAVINGS, response.accountType());
                assertEquals(AccountStatus.PENDING, response.status());
                assertEquals("INR", response.currency());
        }

        @Test
        void shouldGetAccountByAccountNumber() {

                Account account = createAccount();

                when(accountRepository.findByAccountNumber(
                                AccountNumber.of("123456789012")))
                                .thenReturn(Optional.of(account));

                AccountResponse response = service.getByAccountNumber("123456789012");

                assertEquals(1L, response.accountId());
                assertEquals("123456789012", response.accountNumber());
        }

        @Test
        void shouldThrowWhenAccountDoesNotExist() {
                when(accountRepository.findById(AccountId.of(999L))).thenReturn(Optional.empty());
                AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                                () -> service.getById(999L));
                assertEquals("Account not found: 999", exception.getMessage());
        }

        @Test
        void shouldActivateAccount() {

                Account account = createAccount();

                when(accountRepository.findById(AccountId.of(1L)))
                                .thenReturn(Optional.of(account));

                when(accountRepository.save(any(Account.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                AccountResponse response = service.activate(1L);

                assertEquals(AccountStatus.ACTIVE, response.status());

                verify(accountRepository).save(account);
        }

        @Test
        void shouldFreezeAccount() {

                Account account = createAccount();
                account.activate();

                when(accountRepository.findById(AccountId.of(1L)))
                                .thenReturn(Optional.of(account));

                when(accountRepository.save(any(Account.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                AccountResponse response = service.freeze(1L);

                assertEquals(AccountStatus.FROZEN, response.status());

                verify(accountRepository).save(account);
        }

        @Test
        void shouldMakeAccountDormant() {

                Account account = createAccount();
                account.activate();

                when(accountRepository.findById(AccountId.of(1L)))
                                .thenReturn(Optional.of(account));

                when(accountRepository.save(any(Account.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                AccountResponse response = service.makeDormant(1L);

                assertEquals(AccountStatus.DORMANT, response.status());

                verify(accountRepository).save(account);
        }

        @Test
        void shouldCloseAccount() {

                Account account = createAccount();
                account.activate();

                when(accountRepository.findById(AccountId.of(1L)))
                                .thenReturn(Optional.of(account));

                when(accountRepository.save(any(Account.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                AccountResponse response = service.close(1L);

                assertEquals(AccountStatus.CLOSED, response.status());

                verify(accountRepository).save(account);
        }

        private Account createAccount() {

                return Account.create(
                                AccountId.of(1L),
                                AccountNumber.of("123456789012"),
                                CustomerId.of(10000000017L),
                                AccountType.SAVINGS,
                                com.rahulsmgv.cbs.account.domain.valueobject.Currency.inr());
        }
}
