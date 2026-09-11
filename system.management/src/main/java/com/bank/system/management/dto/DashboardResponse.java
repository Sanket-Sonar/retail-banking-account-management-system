package com.bank.system.management.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private Double totalCredit;

    private Double totalDebit;

    private BigDecimal currentBalance;

    private List<TransactionResponse>
            lastTenTransactions;
}
