package com.rahulsmgv.cbs.account.application.service;

import com.rahulsmgv.cbs.account.application.dto.AccountResponse;
import com.rahulsmgv.cbs.account.application.dto.CreateAccountCommand;
import com.rahulsmgv.cbs.account.application.port.AccountIdGenerator;
import com.rahulsmgv.cbs.account.application.port.AccountNumberGenerator;
import com.rahulsmgv.cbs.account.application.port.AccountRepository;
import com.rahulsmgv.cbs.account.domain.model.Account;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.Currency;
import com.rahulsmgv.cbs.account.domain.valueobject.CustomerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rahulsmgv.cbs.account.exception.AccountNotFoundException;
import com.rahulsmgv.cbs.account.exception.DuplicateAccountException;

@Service
@Transactional
public class AccountApplicationService {

    private static final Logger log =
            LoggerFactory.getLogger(AccountApplicationService.class);

    private final AccountRepository accountRepository;
    private final AccountIdGenerator accountIdGenerator;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountApplicationService(
            AccountRepository accountRepository,
            AccountIdGenerator accountIdGenerator,
            AccountNumberGenerator accountNumberGenerator) {

        this.accountRepository = accountRepository;
        this.accountIdGenerator = accountIdGenerator;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    public AccountResponse create(CreateAccountCommand command) {

        log.info(
                "Execution step started: createAccount for customerId={}, accountType={}, currency={}",
                command.customerId(),
                command.accountType(),
                command.currency());

        CustomerId customerId = CustomerId.of(command.customerId());

        if (accountRepository.existsByCustomerIdAndAccountType(
                customerId,
                command.accountType().name())) {

            throw new DuplicateAccountException(
                    "Account already exists for customer and account type");
        }

        Long accountIdValue = accountIdGenerator.nextAccountId();
        String accountNumberValue = accountNumberGenerator.nextAccountNumber();

        AccountId accountId = AccountId.of(accountIdValue);
        AccountNumber accountNumber = AccountNumber.of(accountNumberValue);
        Currency currency = Currency.of(command.currency());

        if (accountRepository.existsByAccountNumber(accountNumber)) {
                throw new DuplicateAccountException("Account number already exists: " + accountNumber.value());
        }

        Account account = Account.create(
                accountId,
                accountNumber,
                customerId,
                command.accountType(),
                currency);

        Account savedAccount = accountRepository.save(account);

        log.info(
                "Execution step completed: createAccount for customerId={}, accountId={}, accountNumber={}",
                customerId.value(),
                savedAccount.accountId().value(),
                savedAccount.accountNumber().value());

        return toResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponse getById(Long accountId) {

        log.info("Execution step started: getById for accountId={}", accountId);

        Account account = accountRepository
                .findById(AccountId.of(accountId))
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found: " + accountId));

        log.info(
                "Execution step completed: getById for accountId={}, customerId={}",
                accountId,
                account.customerId().value());

        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getByAccountNumber(String accountNumber) {

        log.info(
                "Execution step started: getByAccountNumber for accountNumber={}",
                accountNumber);

        Account account = accountRepository
                .findByAccountNumber(AccountNumber.of(accountNumber))
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found: " + accountNumber));

        log.info(
                "Execution step completed: getByAccountNumber for accountNumber={}, accountId={}",
                accountNumber,
                account.accountId().value());

        return toResponse(account);
    }

    public AccountResponse activate(Long accountId) {

        log.info("Execution step started: activate for accountId={}", accountId);

        Account account = getAccount(accountId);

        account.activate();

        Account updatedAccount = accountRepository.save(account);

        log.info(
                "Execution step completed: activate for accountId={}, status={}",
                accountId,
                updatedAccount.status());

        return toResponse(updatedAccount);
    }

    public AccountResponse freeze(Long accountId) {

        log.info("Execution step started: freeze for accountId={}", accountId);

        Account account = getAccount(accountId);

        account.freeze();

        Account updatedAccount = accountRepository.save(account);

        log.info(
                "Execution step completed: freeze for accountId={}, status={}",
                accountId,
                updatedAccount.status());

        return toResponse(updatedAccount);
    }

    public AccountResponse unfreeze(Long accountId) {

        log.info( "Execution step started: unfreeze for accountId={}", accountId);

        Account account = getAccount(accountId);

        account.unfreeze();

        Account updatedAccount = accountRepository.save(account);

        log.info( "Execution step completed: unfreeze for accountId={}, status={}", accountId, updatedAccount.status());
        return toResponse(updatedAccount);
        }

    public AccountResponse makeDormant(Long accountId) {

        log.info("Execution step started: makeDormant for accountId={}", accountId);

        Account account = getAccount(accountId);

        account.makeDormant();

        Account updatedAccount = accountRepository.save(account);

        log.info(
                "Execution step completed: makeDormant for accountId={}, status={}",
                accountId,
                updatedAccount.status());

        return toResponse(updatedAccount);
    }

    public AccountResponse close(Long accountId) {

        log.info("Execution step started: close for accountId={}", accountId);

        Account account = getAccount(accountId);

        account.close();

        Account updatedAccount = accountRepository.save(account);

        log.info(
                "Execution step completed: close for accountId={}, status={}",
                accountId,
                updatedAccount.status());

        return toResponse(updatedAccount);
    }

    private Account getAccount(Long accountId) {

        log.info("Execution step: getAccount for accountId={}", accountId);

        return accountRepository
                .findById(AccountId.of(accountId))
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found: " + accountId));
    }

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.accountId().value(),
                account.customerId().value(),
                account.accountNumber().value(),
                account.accountType(),
                account.status(),
                account.currency().code(),
                account.createdAt(),
                account.updatedAt());
    }
}
