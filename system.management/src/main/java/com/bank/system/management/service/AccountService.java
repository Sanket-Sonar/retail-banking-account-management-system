package com.bank.system.management.service;

import com.bank.system.management.dto.AccountRequest;
import com.bank.system.management.dto.AccountResponse;
import com.bank.system.management.dto.UpdateProfileRequest;
import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.Transaction;
import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.TransactionType;
import com.bank.system.management.exception.ResourceNotFoundException;
import com.bank.system.management.repository.AccountRepository;
import com.bank.system.management.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final TransactionRepository transactionRepository;

    public AccountResponse createAccount(
            AccountRequest request) {

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .accountType(request.getAccountType())
                .branchName(request.getBranchName())
                .openingDate(LocalDate.now())
                .currentBalance(BigDecimal.ZERO)
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber(request.getMobileNumber())
                .address(request.getAddress())
                .build();

        repository.save(account);

        return mapToResponse(account);
    }

    public AccountResponse getAccount(
            String accountNumber,
            String email) {

        Account account = repository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found"));

        validateOwnership(account, email);

        return mapToResponse(account);
    }

    public AccountResponse updateProfile(
            String accountNumber,
            UpdateProfileRequest request) {

        Account account = repository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        account.setMobileNumber(request.getMobileNumber());
        account.setAddress(request.getAddress());

        repository.save(account);

        return mapToResponse(account);
    }

    private String generateAccountNumber() {

        return "ACC" +
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMddHHmmssSSS"));
    }

    private AccountResponse mapToResponse(
            Account account) {

        return AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .customerName(account.getCustomerName())
                .email(account.getEmail())
                .accountType(account.getAccountType())
                .branchName(account.getBranchName())
                .openingDate(account.getOpeningDate())
                .currentBalance(account.getCurrentBalance())
                .accountStatus(account.getAccountStatus())
                .mobileNumber(account.getMobileNumber())
                .address(account.getAddress())
                .build();
    }

    public AccountResponse creditAmount(
            String accountNumber,
            Double amount,
            String email) {

        Account account = repository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found"));

        validateOwnership(account, email);

        account.setCurrentBalance(
                account.getCurrentBalance()
                        .add(BigDecimal.valueOf(amount)));

        repository.save(account);

        saveTransaction(
                account,
                TransactionType.CREDIT,
                amount);

        return mapToResponse(account);
    }

    public AccountResponse debitAmount(
            String accountNumber,
            Double amount,
            String email) {

        Account account = repository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found"));

        validateOwnership(account, email);

        if (account.getCurrentBalance()
                .compareTo(BigDecimal.valueOf(amount)) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient Balance");
        }

        account.setCurrentBalance(
                account.getCurrentBalance()
                        .subtract(BigDecimal.valueOf(amount)));

        repository.save(account);

        saveTransaction(
                account,
                TransactionType.DEBIT,
                amount);

        return mapToResponse(account);
    }

    private void saveTransaction(
            Account account,
            TransactionType transactionType,
            Double amount) {

        Transaction transaction =
                Transaction.builder()
                        .transactionId(
                                "TXN" +
                                        System.currentTimeMillis())
                        .transactionDate(
                                LocalDateTime.now())
                        .transactionType(
                                transactionType)
                        .transactionAmount(
                                amount)
                        .availableBalance(
                                account.getCurrentBalance()
                                        .doubleValue())
                        .account(account)
                        .build();

        transactionRepository.save(transaction);
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

        String principalName = auth.getName();
        if (!account.getEmail().equalsIgnoreCase(principalName)) {
            throw new AccessDeniedException(
                    "Unauthorized access");
        }
    }

}
