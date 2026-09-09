package com.rahulsmgv.cbs.account.application.dto;

import com.rahulsmgv.cbs.account.domain.enums.AccountStatus;
import com.rahulsmgv.cbs.account.domain.enums.AccountType;

import java.time.Instant;

public record AccountResponse(
        Long accountId,
        Long customerId,
        String accountNumber,
        AccountType accountType,
        AccountStatus status,
        String currency,
        Instant createdAt,
        Instant updatedAt) {
}