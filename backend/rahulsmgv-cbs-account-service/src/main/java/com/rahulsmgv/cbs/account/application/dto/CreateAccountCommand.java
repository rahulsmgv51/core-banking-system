package com.rahulsmgv.cbs.account.application.dto;

import com.rahulsmgv.cbs.account.domain.enums.AccountType;

public record CreateAccountCommand(
        Long customerId,
        AccountType accountType,
        String currency) {
}