package com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.mapper;

import com.rahulsmgv.cbs.account.domain.model.Account;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountId;
import com.rahulsmgv.cbs.account.domain.valueobject.AccountNumber;
import com.rahulsmgv.cbs.account.domain.valueobject.Balance;
import com.rahulsmgv.cbs.account.domain.valueobject.Currency;
import com.rahulsmgv.cbs.account.domain.valueobject.CustomerId;
import com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.entity.AccountEntity;

public final class AccountEntityMapper {

    private AccountEntityMapper() {
    }

    public static AccountEntity toEntity(Account account) {

        AccountEntity entity = new AccountEntity();

        entity.setAccountId(
                account.accountId().value());

        entity.setAccountNumber(
                account.accountNumber().value());

        entity.setCustomerId(
                account.customerId().value());

        entity.setAccountType(
                account.accountType());

        entity.setStatus(
                account.status());

        entity.setCurrency(
                account.currency().code());

        entity.setBalance(
                account.balance().amount());

        entity.setCreatedAt(
                account.createdAt());

        entity.setUpdatedAt(
                account.updatedAt());

        return entity;
    }

    public static Account toDomain(AccountEntity entity) {

        return Account.rehydrate(
                AccountId.of(entity.getAccountId()),
                AccountNumber.of(entity.getAccountNumber()),
                CustomerId.of(entity.getCustomerId()),
                entity.getAccountType(),
                Currency.of(entity.getCurrency()),
                Balance.of(entity.getBalance()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
