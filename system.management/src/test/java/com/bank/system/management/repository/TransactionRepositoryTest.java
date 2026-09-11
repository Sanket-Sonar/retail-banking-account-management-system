package com.bank.system.management.repository;

import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.Transaction;
import com.bank.system.management.entity.User;
import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import com.bank.system.management.enums.Role;
import com.bank.system.management.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByTransactionId_shouldReturnTransaction() {
        Account account = saveAccount();

        Transaction tx = transactionRepository.save(Transaction.builder()
                .transactionId("TXN-001")
                .transactionDate(LocalDateTime.of(2024, 7, 1, 12, 0))
                .transactionType(TransactionType.CREDIT)
                .transactionAmount(500.0)
                .availableBalance(1500.0)
                .account(account)
                .build());

        Optional<Transaction> found = transactionRepository.findByTransactionId("TXN-001");

        assertTrue(found.isPresent());
        assertEquals("TXN-001", found.get().getTransactionId());
        assertEquals(tx.getId(), found.get().getId());
    }

    @Test
    void findByAccountOrderByTransactionDateDesc_shouldReturnTransactionsInDescendingOrder() {
        Account account = saveAccount();

        transactionRepository.saveAll(List.of(
                Transaction.builder().transactionId("TXN-1").transactionDate(LocalDateTime.of(2024, 7, 1, 9, 0)).transactionType(TransactionType.DEBIT).transactionAmount(100.0).availableBalance(600.0).account(account).build(),
                Transaction.builder().transactionId("TXN-2").transactionDate(LocalDateTime.of(2024, 7, 2, 9, 0)).transactionType(TransactionType.CREDIT).transactionAmount(200.0).availableBalance(800.0).account(account).build()
        ));

        List<Transaction> transactions = transactionRepository.findByAccountOrderByTransactionDateDesc(account);

        assertEquals(2, transactions.size());
        assertEquals("TXN-2", transactions.get(0).getTransactionId());
        assertEquals("TXN-1", transactions.get(1).getTransactionId());
    }

    @Test
    void findTop10ByAccountOrderByTransactionDateDesc_shouldReturnLatestTen() {
        Account account = saveAccount();

        for (int i = 1; i <= 12; i++) {
            transactionRepository.save(Transaction.builder()
                    .transactionId("TXN-" + i)
                    .transactionDate(LocalDateTime.of(2024, 7, i, 9, 0))
                    .transactionType(TransactionType.CREDIT)
                    .transactionAmount(100.0 * i)
                    .availableBalance(1000.0 + i)
                    .account(account)
                    .build());
        }

        List<Transaction> latest = transactionRepository.findTop10ByAccountOrderByTransactionDateDesc(account);

        assertEquals(10, latest.size());
        assertEquals("TXN-12", latest.get(0).getTransactionId());
        assertEquals("TXN-3", latest.get(9).getTransactionId());
    }

    @Test
    void findByAccountAndTransactionDateBetween_shouldReturnTransactionsWithinRange() {
        Account account = saveAccount();

        transactionRepository.saveAll(List.of(
                Transaction.builder().transactionId("A").transactionDate(LocalDateTime.of(2024, 8, 1, 9, 0)).transactionType(TransactionType.CREDIT).transactionAmount(50.0).availableBalance(500.0).account(account).build(),
                Transaction.builder().transactionId("B").transactionDate(LocalDateTime.of(2024, 8, 10, 9, 0)).transactionType(TransactionType.DEBIT).transactionAmount(30.0).availableBalance(470.0).account(account).build(),
                Transaction.builder().transactionId("C").transactionDate(LocalDateTime.of(2024, 9, 1, 9, 0)).transactionType(TransactionType.CREDIT).transactionAmount(100.0).availableBalance(570.0).account(account).build()
        ));

        List<Transaction> inRange = transactionRepository.findByAccountAndTransactionDateBetween(
                account,
                LocalDateTime.of(2024, 8, 1, 0, 0),
                LocalDateTime.of(2024, 8, 31, 23, 59, 59));

        assertEquals(2, inRange.size());
        assertTrue(inRange.stream().map(Transaction::getTransactionId).toList().containsAll(List.of("A", "B")));
    }

    private Account saveAccount() {
        User user = userRepository.save(User.builder()
                .fullName("Tester")
                .email("tester@gmail.com")
                .password("secret")
                .role(Role.CUSTOMER)
                .build());

        return accountRepository.save(Account.builder()
                .accountNumber("ACC-100" + System.nanoTime())
                .customerName("Tester")
                .email("tester@gmail.com")
                .accountType(AccountType.SAVINGS)
                .branchName("Main")
                .openingDate(LocalDate.of(2024, 1, 1))
                .currentBalance(new BigDecimal("1000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("9999999999")
                .address("Bengaluru")
                .user(user)
                .build());
    }
}
