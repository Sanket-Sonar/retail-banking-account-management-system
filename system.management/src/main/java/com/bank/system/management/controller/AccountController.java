package com.bank.system.management.controller;

import com.bank.system.management.dto.AccountRequest;
import com.bank.system.management.dto.AccountResponse;
import com.bank.system.management.dto.UpdateProfileRequest;
import com.bank.system.management.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Controller")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody AccountRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {

        return accountService.getAccount(
                accountNumber,
                authentication.getName());
    }

    @PutMapping("/{accountNumber}")
    public AccountResponse updateProfile(
            @PathVariable String accountNumber,
            @RequestBody UpdateProfileRequest request) {

        return accountService
                .updateProfile(accountNumber, request);
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable String accountNumber,
            Authentication authentication) {

        return ResponseEntity.ok(
                accountService
                        .getAccount(
                                accountNumber,
                                authentication.getName())
                        .getCurrentBalance());
    }

    @PostMapping("/{accountNumber}/credit")
    public AccountResponse creditAmount(
            @PathVariable String accountNumber,
            @RequestParam Double amount,
            Authentication authentication) {

        return accountService.creditAmount(
                accountNumber,
                amount,
                authentication.getName());
    }

    @PostMapping("/{accountNumber}/debit")
    public AccountResponse debitAmount(
            @PathVariable String accountNumber,
            @RequestParam Double amount,
            Authentication authentication) {

        return accountService.debitAmount(
                accountNumber,
                amount,
                authentication.getName());
    }
}
