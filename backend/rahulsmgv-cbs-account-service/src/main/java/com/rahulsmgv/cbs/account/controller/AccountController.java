package com.rahulsmgv.cbs.account.controller;

import com.rahulsmgv.cbs.account.application.dto.AccountResponse;
import com.rahulsmgv.cbs.account.application.dto.CreateAccountCommand;
import com.rahulsmgv.cbs.account.application.service.AccountApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private static final Logger log =
            LoggerFactory.getLogger(AccountController.class);

    private final AccountApplicationService accountApplicationService;

    public AccountController(
            AccountApplicationService accountApplicationService) {

        this.accountApplicationService = accountApplicationService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody CreateAccountCommand command) {

        log.info(
                "Execution step started: createAccount for customerId={}, accountType={}, currency={}",
                command.customerId(),
                command.accountType(),
                command.currency());

        AccountResponse response =
                accountApplicationService.create(command);

        log.info(
                "Execution step completed: createAccount for customerId={}, accountId={}",
                command.customerId(),
                response.accountId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long accountId) {

        log.info("Execution step started: getAccountById for accountId={}", accountId);

        AccountResponse response =
                accountApplicationService.getById(accountId);

        log.info(
                "Execution step completed: getAccountById for accountId={}, customerId={}",
                accountId,
                response.customerId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable String accountNumber) {

        log.info(
                "Execution step started: getAccountByNumber for accountNumber={}",
                accountNumber);

        AccountResponse response =
                accountApplicationService.getByAccountNumber(accountNumber);

        log.info(
                "Execution step completed: getAccountByNumber for accountNumber={}, accountId={}",
                accountNumber,
                response.accountId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/activate")
    public ResponseEntity<AccountResponse> activateAccount(
            @PathVariable Long accountId) {

        log.info("Execution step started: activateAccount for accountId={}", accountId);

        AccountResponse response =
                accountApplicationService.activate(accountId);

        log.info(
                "Execution step completed: activateAccount for accountId={}, status={}",
                accountId,
                response.status());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/freeze")
    public ResponseEntity<AccountResponse> freezeAccount(
            @PathVariable Long accountId) {

        log.info("Execution step started: freezeAccount for accountId={}", accountId);

        AccountResponse response =
                accountApplicationService.freeze(accountId);

        log.info(
                "Execution step completed: freezeAccount for accountId={}, status={}",
                accountId,
                response.status());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/dormant")
    public ResponseEntity<AccountResponse> makeAccountDormant(
            @PathVariable Long accountId) {

        log.info("Execution step started: makeAccountDormant for accountId={}", accountId);

        AccountResponse response =
                accountApplicationService.makeDormant(accountId);

        log.info(
                "Execution step completed: makeAccountDormant for accountId={}, status={}",
                accountId,
                response.status());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/close")
    public ResponseEntity<AccountResponse> closeAccount(
            @PathVariable Long accountId) {

        log.info("Execution step started: closeAccount for accountId={}", accountId);

        AccountResponse response =
                accountApplicationService.close(accountId);

        log.info(
                "Execution step completed: closeAccount for accountId={}, status={}",
                accountId,
                response.status());

        return ResponseEntity.ok(response);
    }
}
