package com.rahulsmgv.cbs.account.application.port;

import com.rahulsmgv.cbs.account.domain.model.Account;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.CustomerId;

import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(AccountId accountId);

    Optional<Account> findByAccountNumber(AccountNumber accountNumber);

    boolean existsByAccountNumber(AccountNumber accountNumber);

    boolean existsByCustomerIdAndAccountType(
            CustomerId customerId,
            String accountType);

    void delete(Account account);
}
