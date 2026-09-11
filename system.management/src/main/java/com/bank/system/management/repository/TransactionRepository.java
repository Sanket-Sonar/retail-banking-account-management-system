package com.bank.system.management.repository;

import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(
            String transactionId);

    List<Transaction>
    findByAccountOrderByTransactionDateDesc(
            Account account);

    List<Transaction>
    findTop10ByAccountOrderByTransactionDateDesc(
            Account account);

    List<Transaction>
    findByAccountAndTransactionDateBetween(
            Account account,
            LocalDateTime startDate,
            LocalDateTime endDate);
}
