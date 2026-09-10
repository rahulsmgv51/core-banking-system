package com.rahulsmgv.cbs.account.domain.model;

import com.rahulsmgv.cbs.account.domain.enums.AccountStatus;
import com.rahulsmgv.cbs.account.domain.enums.AccountType;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.Currency;
import com.rahulsmgv.cbs.account.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

        private Account createAccount() {

                return Account.create(
                                AccountId.of(10000000001L),
                                AccountNumber.of("123456789012"),
                                CustomerId.of(10000000017L),
                                AccountType.SAVINGS,
                                Currency.inr());
        }

        @Test
        void shouldCreateAccountWithPendingStatus() {

                Account account = createAccount();

                assertNotNull(account.accountId());
                assertNotNull(account.accountNumber());

                assertEquals(
                                AccountStatus.PENDING,
                                account.status());

                assertEquals(
                                BigDecimal.ZERO,
                                account.balance().amount());
        }

        @Test
        void shouldActivateAccount() {

                Account account = createAccount();

                account.activate();

                assertEquals(
                                AccountStatus.ACTIVE,
                                account.status());
        }

        @Test
        void shouldCreditActiveAccount() {

                Account account = createAccount();

                account.activate();

                account.credit(
                                new BigDecimal("1000.00"));

                assertEquals(
                                new BigDecimal("1000.00"),
                                account.balance().amount());
        }

        @Test
        void shouldDebitActiveAccount() {

                Account account = createAccount();

                account.activate();

                account.credit(
                                new BigDecimal("1000.00"));

                account.debit(
                                new BigDecimal("250.00"));

                assertEquals(
                                new BigDecimal("750.00"),
                                account.balance().amount());
        }

        @Test
        void shouldNotDebitMoreThanBalance() {

                Account account = createAccount();

                account.activate();

                account.credit(
                                new BigDecimal("1000.00"));

                assertThrows(
                                IllegalStateException.class,
                                () -> account.debit(
                                                new BigDecimal("1500.00")));
        }

        @Test
        void shouldFreezeAccount() {

                Account account = createAccount();

                account.activate();
                account.freeze();

                assertEquals(
                                AccountStatus.FROZEN,
                                account.status());
        }

        @Test
        void shouldMakeAccountDormant() {

                Account account = createAccount();

                account.activate();
                account.makeDormant();

                assertEquals(
                                AccountStatus.DORMANT,
                                account.status());
        }

        @Test
        void shouldCloseAccount() {

                Account account = createAccount();

                account.activate();
                account.close();

                assertEquals(
                                AccountStatus.CLOSED,
                                account.status());
        }

        @Test
        void shouldNotActivateClosedAccount() {
                Account account = createAccount();
                account.activate();
                account.close();

                assertThrows(
                                IllegalStateException.class,
                                account::activate);
        }

        @Test
        void shouldNotFreezeClosedAccount() {
                Account account = createAccount();
                account.activate();
                account.close();

                assertThrows(
                                IllegalStateException.class,
                                account::freeze);
        }

        @Test
        void shouldUnfreezeFrozenAccount() {

                Account account = createAccount();

                account.activate();
                account.freeze();

                account.unfreeze();

                assertEquals(
                                AccountStatus.ACTIVE,
                                account.status());
        }

        @Test
        void shouldReactivateDormantAccount() {

                Account account = createAccount();

                account.activate();
                account.makeDormant();

                account.activate();

                assertEquals(
                                AccountStatus.ACTIVE,
                                account.status());
        }

        @Test
        void shouldNotFreezePendingAccount() {

                Account account = createAccount();

                assertThrows(
                                IllegalStateException.class,
                                account::freeze);
        }

        @Test
        void shouldNotMakePendingAccountDormant() {

                Account account = createAccount();

                assertThrows(
                                IllegalStateException.class,
                                account::makeDormant);
        }

        @Test
        void shouldNotClosePendingAccount() {

                Account account = createAccount();

                assertThrows(
                                IllegalStateException.class,
                                account::close);
        }

        @Test
        void shouldNotMakeFrozenAccountDormant() {

                Account account = createAccount();

                account.activate();
                account.freeze();

                assertThrows(
                                IllegalStateException.class,
                                account::makeDormant);
        }

        @Test
        void shouldNotFreezeDormantAccount() {

                Account account = createAccount();

                account.activate();
                account.makeDormant();

                assertThrows(
                                IllegalStateException.class,
                                account::freeze);
        }

        @Test
        void shouldNotUnfreezeActiveAccount() {

                Account account = createAccount();

                account.activate();

                assertThrows(
                                IllegalStateException.class,
                                account::unfreeze);
        }

        @Test
        void shouldNotUnfreezeClosedAccount() {

                Account account = createAccount();

                account.activate();
                account.close();

                assertThrows(
                                IllegalStateException.class,
                                account::unfreeze);
        }

        @Test
        void shouldNotCreditInactiveAccount() {

                Account account = createAccount();

                assertThrows(
                                IllegalStateException.class,
                                () -> account.credit(
                                                new BigDecimal("1000.00")));
        }
}