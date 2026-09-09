package com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.adapter;

import com.rahulsmgv.cbs.account.application.port.AccountRepository;
import com.rahulsmgv.cbs.account.domain.model.Account;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.entity.AccountEntity;
import com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.mapper.AccountEntityMapper;
import com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.repository.AccountJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountRepositoryAdapter implements AccountRepository {

    private static final Logger log =
            LoggerFactory.getLogger(AccountRepositoryAdapter.class);

    private final AccountJpaRepository accountJpaRepository;

    public AccountRepositoryAdapter(
            AccountJpaRepository accountJpaRepository) {

        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Account save(Account account) {

        log.info(
                "Persisting account aggregate with accountId={}",
                account.accountId().value());

        AccountEntity entity =
                AccountEntityMapper.toEntity(account);

        AccountEntity savedEntity =
                accountJpaRepository.save(entity);

        log.info(
                "Account aggregate persisted successfully with accountId={}",
                savedEntity.getAccountId());

        return AccountEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(
            AccountId accountId) {

        log.info(
                "Querying account repository for accountId={}",
                accountId.value());

        Optional<Account> account =
                accountJpaRepository
                        .findById(accountId.value())
                        .map(AccountEntityMapper::toDomain);

        if (account.isPresent()) {
            log.info(
                    "Account lookup succeeded for accountId={}",
                    accountId.value());
        } else {
            log.warn(
                    "Account lookup returned no result for accountId={}",
                    accountId.value());
        }

        return account;
    }

    @Override
    public Optional<Account> findByAccountNumber(
            AccountNumber accountNumber) {

        log.info(
                "Querying account repository for accountNumber={}",
                accountNumber.value());

        return accountJpaRepository
                .findByAccountNumber(accountNumber.value())
                .map(AccountEntityMapper::toDomain);
    }

    @Override
    public boolean existsByAccountNumber(
            AccountNumber accountNumber) {

        return accountJpaRepository
                .existsByAccountNumber(accountNumber.value());
    }

    @Override
    public boolean existsByCustomerIdAndAccountType(
            CustomerId customerId,
            String accountType) {

        return accountJpaRepository
                .existsByCustomerIdAndAccountType(
                        customerId.value(),
                        com.rahulsmgv.cbs.account.domain.enums.AccountType
                                .valueOf(accountType));
    }

    @Override
    public void delete(Account account) {

        log.info(
                "Deleting account aggregate with accountId={}",
                account.accountId().value());

        AccountEntity entity =
                AccountEntityMapper.toEntity(account);

        accountJpaRepository.delete(entity);

        log.info(
                "Account aggregate deleted with accountId={}",
                account.accountId().value());
    }
}
