package com.bank.system.management.dto.mapper;

import com.bank.system.management.dto.FDResponse;
import com.bank.system.management.dto.TransactionResponse;
import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.FixedDeposit;
import com.bank.system.management.entity.Transaction;
import com.bank.system.management.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);
    private final FixedDepositMapper fixedDepositMapper = Mappers.getMapper(FixedDepositMapper.class);

    @Test
    void transactionMapper_shouldMapEntityToResponse() {
        Account account = Account.builder()
                .accountNumber("ACC-555")
                .build();

        Transaction transaction = Transaction.builder()
                .transactionId("TXN-100")
                .transactionDate(LocalDateTime.of(2024, 5, 15, 10, 45))
                .transactionType(TransactionType.CREDIT)
                .transactionAmount(1200.0)
                .availableBalance(30800.0)
                .account(account)
                .build();

        TransactionResponse response = transactionMapper.toResponse(transaction);

        assertAll(
                () -> assertEquals("TXN-100", response.getTransactionId()),
                () -> assertEquals("ACC-555", response.getAccountNumber()),
                () -> assertEquals(LocalDateTime.of(2024, 5, 15, 10, 45), response.getTransactionDate()),
                () -> assertEquals("CREDIT", response.getTransactionType()),
                () -> assertEquals(1200.0, response.getTransactionAmount()),
                () -> assertEquals(30800.0, response.getAvailableBalance())
        );
    }

    @Test
    void transactionMapper_shouldMapListOfEntitiesToResponses() {
        Account account = Account.builder().accountNumber("ACC-900").build();

        List<Transaction> transactions = List.of(
                Transaction.builder()
                        .transactionId("TXN-1")
                        .transactionDate(LocalDateTime.of(2024, 2, 1, 9, 0))
                        .transactionType(TransactionType.DEBIT)
                        .transactionAmount(500.0)
                        .availableBalance(9500.0)
                        .account(account)
                        .build(),
                Transaction.builder()
                        .transactionId("TXN-2")
                        .transactionDate(LocalDateTime.of(2024, 2, 2, 9, 0))
                        .transactionType(TransactionType.CREDIT)
                        .transactionAmount(1000.0)
                        .availableBalance(10500.0)
                        .account(account)
                        .build()
        );

        List<TransactionResponse> responses = transactionMapper.toResponseList(transactions);

        assertEquals(2, responses.size());
        assertEquals("ACC-900", responses.get(0).getAccountNumber());
        assertEquals("DEBIT", responses.get(0).getTransactionType());
        assertEquals("ACC-900", responses.get(1).getAccountNumber());
        assertEquals("CREDIT", responses.get(1).getTransactionType());
    }

    @Test
    void fixedDepositMapper_shouldMapEntityToResponse() {
        FixedDeposit deposit = FixedDeposit.builder()
                .fdNumber("FD-77")
                .depositAmount(25000.0)
                .interestRate(7.1)
                .tenureYears(3)
                .depositDate(LocalDate.of(2024, 1, 15))
                .maturityDate(LocalDate.of(2027, 1, 15))
                .maturityAmount(31000.0)
                .build();

        FDResponse response = fixedDepositMapper.toResponse(deposit);

        assertAll(
                () -> assertEquals("FD-77", response.getFdNumber()),
                () -> assertEquals(25000.0, response.getDepositAmount()),
                () -> assertEquals(7.1, response.getInterestRate()),
                () -> assertEquals(3, response.getTenureYears()),
                () -> assertEquals(LocalDate.of(2024, 1, 15), response.getDepositDate()),
                () -> assertEquals(LocalDate.of(2027, 1, 15), response.getMaturityDate()),
                () -> assertEquals(31000.0, response.getMaturityAmount())
        );
    }

    @Test
    void fixedDepositMapper_shouldMapListOfEntitiesToResponses() {
        List<FixedDeposit> deposits = List.of(
                FixedDeposit.builder()
                        .fdNumber("FD-A")
                        .depositAmount(60000.0)
                        .interestRate(6.5)
                        .tenureYears(2)
                        .depositDate(LocalDate.of(2023, 6, 1))
                        .maturityDate(LocalDate.of(2025, 6, 1))
                        .maturityAmount(70000.0)
                        .build(),
                FixedDeposit.builder()
                        .fdNumber("FD-B")
                        .depositAmount(80000.0)
                        .interestRate(7.0)
                        .tenureYears(4)
                        .depositDate(LocalDate.of(2024, 3, 1))
                        .maturityDate(LocalDate.of(2028, 3, 1))
                        .maturityAmount(102000.0)
                        .build()
        );

        List<FDResponse> responses = fixedDepositMapper.toResponseList(deposits);

        assertEquals(2, responses.size());
        assertEquals("FD-A", responses.get(0).getFdNumber());
        assertEquals(60000.0, responses.get(0).getDepositAmount());
        assertEquals("FD-B", responses.get(1).getFdNumber());
        assertEquals(102000.0, responses.get(1).getMaturityAmount());
    }
}
