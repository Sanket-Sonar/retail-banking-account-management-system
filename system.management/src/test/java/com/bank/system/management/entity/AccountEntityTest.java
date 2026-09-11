package com.bank.system.management.entity;

import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountEntityTest {

    @Test
    void builderAndAccessors_shouldSetAccountFields() {
        User user = User.builder()
                .id(2L)
                .fullName("John Doe")
                .email("john@gmail.com")
                .password("secret")
                .build();

        List<Transaction> transactions = new ArrayList<>();

        Account account = Account.builder()
                .id(10L)
                .accountNumber("ACC-1001")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.SAVINGS)
                .branchName("MG Road")
                .openingDate(LocalDate.of(2024, 1, 15))
                .currentBalance(new BigDecimal("25000.50"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("9876543210")
                .address("Bengaluru")
                .user(user)
                .transactions(transactions)
                .build();

        assertAll(
                () -> assertEquals(10L, account.getId()),
                () -> assertEquals("ACC-1001", account.getAccountNumber()),
                () -> assertEquals("John Doe", account.getCustomerName()),
                () -> assertEquals("john@gmail.com", account.getEmail()),
                () -> assertEquals(AccountType.SAVINGS, account.getAccountType()),
                () -> assertEquals("MG Road", account.getBranchName()),
                () -> assertEquals(LocalDate.of(2024, 1, 15), account.getOpeningDate()),
                () -> assertEquals(new BigDecimal("25000.50"), account.getCurrentBalance()),
                () -> assertEquals(AccountStatus.ACTIVE, account.getAccountStatus()),
                () -> assertEquals("9876543210", account.getMobileNumber()),
                () -> assertEquals("Bengaluru", account.getAddress()),
                () -> assertSame(user, account.getUser()),
                () -> assertSame(transactions, account.getTransactions())
        );

        List<Transaction> updatedTransactions = new ArrayList<>();
        account.setTransactions(updatedTransactions);
        assertSame(updatedTransactions, account.getTransactions());
    }
}
