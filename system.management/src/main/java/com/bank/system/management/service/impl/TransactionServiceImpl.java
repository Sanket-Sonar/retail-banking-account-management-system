package com.bank.system.management.service.impl;

import com.bank.system.management.dto.DashboardResponse;
import com.bank.system.management.dto.TransactionResponse;
import com.bank.system.management.dto.mapper.TransactionMapper;
import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.Transaction;
import com.bank.system.management.enums.TransactionType;
import com.bank.system.management.exception.ResourceNotFoundException;
import com.bank.system.management.repository.AccountRepository;
import com.bank.system.management.repository.TransactionRepository;
import com.bank.system.management.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl  implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper mapper;



    @Override
    public DashboardResponse getDashboard(
            String accountNumber,
            String email) {

        Account account = getAccount(accountNumber);

        validateOwnership(account, email);

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountOrderByTransactionDateDesc(
                                account);

        double totalCredit =
                transactions.stream()
                        .filter(t ->
                                t.getTransactionType()
                                        == TransactionType.CREDIT)
                        .mapToDouble(
                                Transaction::getTransactionAmount)
                        .sum();

        double totalDebit =
                transactions.stream()
                        .filter(t ->
                                t.getTransactionType()
                                        == TransactionType.DEBIT)
                        .mapToDouble(
                                Transaction::getTransactionAmount)
                        .sum();

        return DashboardResponse.builder()
                .totalCredit(totalCredit)
                .totalDebit(totalDebit)
                .currentBalance(account.getCurrentBalance())
                .lastTenTransactions(
                        mapper.toResponseList(
                                transactionRepository
                                        .findTop10ByAccountOrderByTransactionDateDesc(
                                                account)))
                .build();
    }

    @Override
    public List<TransactionResponse> getTransactions(
            String accountNumber,
            String email) {

        Account account = getAccount(accountNumber);

        validateOwnership(account, email);

        return mapper.toResponseList(
                transactionRepository
                        .findByAccountOrderByTransactionDateDesc(
                                account));
    }

    @Override
    public List<TransactionResponse>
    getTransactionsByDateRange(
            String accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            String email) {

        Account account = getAccount(accountNumber);

        validateOwnership(account, email);

        return mapper.toResponseList(
                transactionRepository
                        .findByAccountAndTransactionDateBetween(
                                account,
                                startDate.atStartOfDay(),
                                endDate.atTime(23,59,59)));
    }

    @Override
    public TransactionResponse searchTransaction(
            String transactionId,
            String email) {

        Transaction transaction =
                transactionRepository
                        .findByTransactionId(
                                transactionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"));

        validateOwnership(
                transaction.getAccount(),
                email);

        return mapper.toResponse(transaction);
    }

    private Account getAccount(
            String accountNumber) {

        return accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found"));
    }

    private void validateOwnership(
            Account account,
            String email) {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean isAdmin =
                auth.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        if (!account.getUser()
                .getEmail()
                .equals(email)) {

            throw new AccessDeniedException(
                    "Unauthorized access");
        }
    }
}
