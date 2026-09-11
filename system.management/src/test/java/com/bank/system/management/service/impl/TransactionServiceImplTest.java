package com.bank.system.management.service.impl;

import com.bank.system.management.dto.DashboardResponse;
import com.bank.system.management.dto.TransactionResponse;
import com.bank.system.management.dto.mapper.TransactionMapper;
import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.Transaction;
import com.bank.system.management.entity.User;
import com.bank.system.management.enums.TransactionType;
import com.bank.system.management.exception.ResourceNotFoundException;
import com.bank.system.management.repository.AccountRepository;
import com.bank.system.management.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionServiceImpl service;

    private User user;
    private Account account;
    private Transaction creditTxn;
    private Transaction debitTxn;

    @BeforeEach
    void setup() {

        user = User.builder()
                .email("test@gmail.com")
                .build();

        account = Account.builder()
                .accountNumber("AC123")
                .currentBalance(BigDecimal.valueOf(10000.0))
                .user(user)
                .build();

        creditTxn = Transaction.builder()
                .transactionId("TXN1")
                .account(account)
                .transactionAmount(5000.0)
                .transactionType(TransactionType.CREDIT)
                .transactionDate(LocalDateTime.now())
                .build();

        debitTxn = Transaction.builder()
                .transactionId("TXN2")
                .account(account)
                .transactionAmount(2000.0)
                .transactionType(TransactionType.DEBIT)
                .transactionDate(LocalDateTime.now())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(() -> "ROLE_CUSTOMER"));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getDashboard_ShouldReturnDashboard() {

        List<Transaction> transactions =
                List.of(creditTxn, debitTxn);

        when(accountRepository.findByAccountNumber("AC123"))
                .thenReturn(Optional.of(account));

        when(transactionRepository
                .findByAccountOrderByTransactionDateDesc(account))
                .thenReturn(transactions);

        when(transactionRepository
                .findTop10ByAccountOrderByTransactionDateDesc(account))
                .thenReturn(transactions);

        when(mapper.toResponseList(transactions))
                .thenReturn(List.of(new TransactionResponse()));

        DashboardResponse result =
                service.getDashboard(
                        "AC123",
                        "test@gmail.com");

        assertNotNull(result);
        assertEquals(5000.0, result.getTotalCredit());
        assertEquals(2000.0, result.getTotalDebit());
        assertEquals(BigDecimal.valueOf(10000.0), result.getCurrentBalance());
    }

    @Test
    void getDashboard_ShouldThrowException_WhenAccountNotFound() {

        when(accountRepository.findByAccountNumber("AC123"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getDashboard(
                        "AC123",
                        "test@gmail.com"));
    }

    @Test
    void getTransactions_ShouldReturnTransactions() {

        List<Transaction> transactions =
                List.of(creditTxn);

        List<TransactionResponse> responses =
                List.of(new TransactionResponse());

        when(accountRepository.findByAccountNumber("AC123"))
                .thenReturn(Optional.of(account));

        when(transactionRepository
                .findByAccountOrderByTransactionDateDesc(account))
                .thenReturn(transactions);

        when(mapper.toResponseList(transactions))
                .thenReturn(responses);

        List<TransactionResponse> result =
                service.getTransactions(
                        "AC123",
                        "test@gmail.com");

        assertEquals(1, result.size());
    }

    @Test
    void getTransactionsByDateRange_ShouldReturnTransactions() {

        List<Transaction> transactions =
                List.of(creditTxn);

        List<TransactionResponse> responses =
                List.of(new TransactionResponse());

        when(accountRepository.findByAccountNumber("AC123"))
                .thenReturn(Optional.of(account));

        when(transactionRepository
                .findByAccountAndTransactionDateBetween(
                        eq(account),
                        any(),
                        any()))
                .thenReturn(transactions);

        when(mapper.toResponseList(transactions))
                .thenReturn(responses);

        List<TransactionResponse> result =
                service.getTransactionsByDateRange(
                        "AC123",
                        LocalDate.now().minusDays(10),
                        LocalDate.now(),
                        "test@gmail.com");

        assertEquals(1, result.size());
    }

    @Test
    void searchTransaction_ShouldReturnTransaction() {

        TransactionResponse response =
                new TransactionResponse();

        when(transactionRepository
                .findByTransactionId("TXN1"))
                .thenReturn(Optional.of(creditTxn));

        when(mapper.toResponse(creditTxn))
                .thenReturn(response);

        TransactionResponse result =
                service.searchTransaction(
                        "TXN1",
                        "test@gmail.com");

        assertNotNull(result);
    }

    @Test
    void searchTransaction_ShouldThrowException_WhenNotFound() {

        when(transactionRepository
                .findByTransactionId("TXN1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.searchTransaction(
                        "TXN1",
                        "test@gmail.com"));
    }

    @Test
    void getTransactions_ShouldThrowAccessDenied_WhenNotOwner() {

        when(accountRepository.findByAccountNumber("AC123"))
                .thenReturn(Optional.of(account));

        assertThrows(
                AccessDeniedException.class,
                () -> service.getTransactions(
                        "AC123",
                        "other@gmail.com"));
    }

    @Test
    void getTransactions_ShouldAllowAdminAccess() {

        UsernamePasswordAuthenticationToken adminAuth =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(() -> "ROLE_ADMIN"));

        SecurityContextHolder.getContext()
                .setAuthentication(adminAuth);

        List<Transaction> transactions =
                List.of(creditTxn);

        List<TransactionResponse> responses =
                List.of(new TransactionResponse());

        when(accountRepository.findByAccountNumber("AC123"))
                .thenReturn(Optional.of(account));

        when(transactionRepository
                .findByAccountOrderByTransactionDateDesc(account))
                .thenReturn(transactions);

        when(mapper.toResponseList(transactions))
                .thenReturn(responses);

        List<TransactionResponse> result =
                service.getTransactions(
                        "AC123",
                        "someone@gmail.com");

        assertEquals(1, result.size());
    }
}