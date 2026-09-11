package com.bank.system.management.controller;

import com.bank.system.management.dto.DashboardResponse;
import com.bank.system.management.dto.TransactionResponse;
import com.bank.system.management.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Dashboard APIs")
@Slf4j
public class TransactionController {
    private final TransactionService service;

    @GetMapping("/dashboard/{accountNumber}")
    public ResponseEntity<DashboardResponse>
    getDashboard(
            @PathVariable String accountNumber,
            Authentication authentication) {
        log.info("Fetching dashboard for account number: {}", accountNumber);

        return ResponseEntity.ok(
                service.getDashboard(
                        accountNumber,
                        authentication.getName()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>>
    getTransactions(
            @PathVariable String accountNumber,
            Authentication authentication) {
        log.info("Fetching transactions for account number: {}", accountNumber);
        return ResponseEntity.ok(
                service.getTransactions(
                        accountNumber,
                        authentication.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<TransactionResponse>
    searchTransaction(
            @RequestParam String transactionId,
            Authentication authentication) {
        log.info("Fetching transactions for account number: {}", transactionId);
        return ResponseEntity.ok(
                service.searchTransaction(
                        transactionId,
                        authentication.getName()));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>>
    getTransactionsByDateRange(
            @RequestParam String accountNumber,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {
        log.info("Fetching transactions for account number: {}", accountNumber);
        return ResponseEntity.ok(
                service.getTransactionsByDateRange(
                        accountNumber,
                        startDate,
                        endDate,
                        authentication.getName()));
    }

}
