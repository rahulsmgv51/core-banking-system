package com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.repository;

import com.rahulsmgv.cbs.account.domain.enums.AccountType;
import com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository
        extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(
            String accountNumber);

    boolean existsByAccountNumber(
            String accountNumber);

    boolean existsByCustomerIdAndAccountType(
            Long customerId,
            AccountType accountType);
}
