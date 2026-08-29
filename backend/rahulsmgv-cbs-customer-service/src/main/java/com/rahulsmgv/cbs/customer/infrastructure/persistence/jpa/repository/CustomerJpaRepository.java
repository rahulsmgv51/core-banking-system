package com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.repository;

import com.rahulsmgv.cbs.customer.infrastructure.persistence.jpa.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Spring Data JPA repository for CustomerEntity.
 *
 * This interface is responsible only for database-level
 * persistence operations.
 */
public interface CustomerJpaRepository
        extends JpaRepository<CustomerEntity, Long> {

    boolean existsByEmailAddress(String emailAddress);

    boolean existsByMobileNumber(String mobileNumber);
}