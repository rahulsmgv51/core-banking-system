package com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.adapter;

import com.rahulsmgv.cbs.account.application.port.AccountNumberGenerator;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberGeneratorAdapter
        implements AccountNumberGenerator {

    private final EntityManager entityManager;

    public AccountNumberGeneratorAdapter(
            EntityManager entityManager) {

        this.entityManager = entityManager;
    }

    @Override
    public String nextAccountNumber() {

        return (String) entityManager
                .createNativeQuery(
                        "SELECT LPAD(nextval('account_number_sequence')::text, 12, '0')")
                .getSingleResult();
    }
}
