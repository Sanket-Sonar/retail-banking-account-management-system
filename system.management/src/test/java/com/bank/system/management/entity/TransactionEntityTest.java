package com.bank.system.management.entity;

import com.bank.system.management.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionEntityTest {

    @Test
    void builderAndAccessors_shouldSetTransactionFields() {
        Account account = Account.builder()
                .accountNumber("ACC-202")
                .build();

        Transaction transaction = Transaction.builder()
                .id(5L)
                .transactionId("TXN-123")
                .transactionDate(LocalDateTime.of(2024, 9, 10, 12, 30))
                .transactionType(TransactionType.CREDIT)
                .transactionAmount(1500.0)
                .availableBalance(22000.0)
                .account(account)
                .build();

        assertAll(
                () -> assertEquals(5L, transaction.getId()),
                () -> assertEquals("TXN-123", transaction.getTransactionId()),
                () -> assertEquals(LocalDateTime.of(2024, 9, 10, 12, 30), transaction.getTransactionDate()),
                () -> assertEquals(TransactionType.CREDIT, transaction.getTransactionType()),
                () -> assertEquals(1500.0, transaction.getTransactionAmount()),
                () -> assertEquals(22000.0, transaction.getAvailableBalance()),
                () -> assertSame(account, transaction.getAccount())
        );

        Account newAccount = Account.builder().accountNumber("ACC-303").build();
        transaction.setAccount(newAccount);
        assertSame(newAccount, transaction.getAccount());
    }
}
