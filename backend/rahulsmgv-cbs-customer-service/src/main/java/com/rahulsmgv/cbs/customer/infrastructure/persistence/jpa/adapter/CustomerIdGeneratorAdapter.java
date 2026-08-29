package com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.adapter;

import com.rahulsmgv.cbs.customer.application.port.CustomerIdGenerator;
import com.rahulsmgv.cbs.customer.domain.valueobject.CustomerId;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class CustomerIdGeneratorAdapter implements CustomerIdGenerator {

    private final EntityManager entityManager;

    public CustomerIdGeneratorAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CustomerId nextCustomerId() {
        Long customerId = ((Number) entityManager
                .createNativeQuery("SELECT nextval('customer_id_sequence')")
                .getSingleResult())
                .longValue();

        return CustomerId.of(customerId);
    }
}
