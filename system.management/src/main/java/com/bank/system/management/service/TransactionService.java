package com.bank.system.management.service;

import com.bank.system.management.dto.DashboardResponse;
import com.bank.system.management.dto.TransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    DashboardResponse getDashboard(
            String accountNumber,
            String email);

    List<TransactionResponse> getTransactions(
            String accountNumber,
            String email);

    List<TransactionResponse>
    getTransactionsByDateRange(
            String accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            String email);

    TransactionResponse searchTransaction(
            String transactionId,
            String email);
}
