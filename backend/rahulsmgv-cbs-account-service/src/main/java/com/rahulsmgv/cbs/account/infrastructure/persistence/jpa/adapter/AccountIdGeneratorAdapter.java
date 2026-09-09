package com.rahulsmgv.cbs.account.infrastructure.persistence.jpa.adapter;

import com.rahulsmgv.cbs.account.application.port.AccountIdGenerator;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class AccountIdGeneratorAdapter
        implements AccountIdGenerator {

    private final EntityManager entityManager;

    public AccountIdGeneratorAdapter(
            EntityManager entityManager) {

        this.entityManager = entityManager;
    }

    @Override
    public Long nextAccountId() {

        return ((Number) entityManager
                .createNativeQuery(
                        "SELECT nextval('account_id_sequence')")
                .getSingleResult())
                .longValue();
    }
}
