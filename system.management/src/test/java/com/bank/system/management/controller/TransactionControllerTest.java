package com.bank.system.management.controller;

import com.bank.system.management.dto.DashboardResponse;
import com.bank.system.management.dto.TransactionResponse;
import com.bank.system.management.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService service;

    @InjectMocks
    private TransactionController controller;

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("john@gmail.com", "pw");
    }

    @Test
    void getDashboard_shouldReturnDashboard() {
        DashboardResponse dashboard = DashboardResponse.builder()
                .totalCredit(15000.0)
                .totalDebit(5000.0)
                .currentBalance(new BigDecimal("12000.00"))
                .lastTenTransactions(List.of(
                        TransactionResponse.builder()
                                .transactionId("TXN-1")
                                .accountNumber("ACC-1001")
                                .transactionDate(LocalDateTime.of(2024, 9, 1, 9, 30))
                                .transactionType("CREDIT")
                                .transactionAmount(1500.0)
                                .availableBalance(12000.0)
                                .build()))
                .build();

        when(service.getDashboard("ACC-1001", "john@gmail.com")).thenReturn(dashboard);

        ResponseEntity<DashboardResponse> actual = controller.getDashboard("ACC-1001", authentication());

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(15000.0, actual.getBody().getTotalCredit());
        assertEquals(new BigDecimal("12000.00"), actual.getBody().getCurrentBalance());
        verify(service).getDashboard("ACC-1001", "john@gmail.com");
    }

    @Test
    void getTransactions_shouldReturnList() {
        List<TransactionResponse> transactions = List.of(
                TransactionResponse.builder()
                        .transactionId("TXN-1")
                        .accountNumber("ACC-1001")
                        .transactionDate(LocalDateTime.of(2024, 9, 1, 9, 30))
                        .transactionType("CREDIT")
                        .transactionAmount(1500.0)
                        .availableBalance(12000.0)
                        .build(),
                TransactionResponse.builder()
                        .transactionId("TXN-2")
                        .accountNumber("ACC-1001")
                        .transactionDate(LocalDateTime.of(2024, 9, 2, 10, 0))
                        .transactionType("DEBIT")
                        .transactionAmount(500.0)
                        .availableBalance(11500.0)
                        .build()
        );

        when(service.getTransactions("ACC-1001", "john@gmail.com")).thenReturn(transactions);

        ResponseEntity<List<TransactionResponse>> actual = controller.getTransactions("ACC-1001", authentication());

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("TXN-1", actual.getBody().get(0).getTransactionId());
        assertEquals("DEBIT", actual.getBody().get(1).getTransactionType());
        verify(service).getTransactions("ACC-1001", "john@gmail.com");
    }

    @Test
    void searchTransaction_shouldReturnTransaction() {
        TransactionResponse response = TransactionResponse.builder()
                .transactionId("TXN-7")
                .accountNumber("ACC-1001")
                .transactionDate(LocalDateTime.of(2024, 9, 10, 12, 45))
                .transactionType("CREDIT")
                .transactionAmount(2000.0)
                .availableBalance(14000.0)
                .build();

        when(service.searchTransaction("TXN-7", "john@gmail.com")).thenReturn(response);

        ResponseEntity<TransactionResponse> actual = controller.searchTransaction("TXN-7", authentication());

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("TXN-7", actual.getBody().getTransactionId());
        assertEquals("ACC-1001", actual.getBody().getAccountNumber());
        verify(service).searchTransaction("TXN-7", "john@gmail.com");
    }

    @Test
    void getTransactionsByDateRange_shouldReturnFilteredTransactions() {
        List<TransactionResponse> transactions = List.of(
                TransactionResponse.builder()
                        .transactionId("TXN-11")
                        .accountNumber("ACC-1001")
                        .transactionDate(LocalDateTime.of(2024, 8, 1, 11, 0))
                        .transactionType("CREDIT")
                        .transactionAmount(1000.0)
                        .availableBalance(5000.0)
                        .build()
        );

        when(service.getTransactionsByDateRange("ACC-1001", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 31), "john@gmail.com"))
                .thenReturn(transactions);

        ResponseEntity<List<TransactionResponse>> actual = controller.getTransactionsByDateRange("ACC-1001", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 31), authentication());

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("TXN-11", actual.getBody().get(0).getTransactionId());
        verify(service).getTransactionsByDateRange("ACC-1001", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 31), "john@gmail.com");
    }
}
