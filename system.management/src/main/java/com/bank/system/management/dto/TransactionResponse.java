package com.bank.system.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String transactionId;

    private String accountNumber;

    private LocalDateTime transactionDate;

    private String transactionType;

    private Double transactionAmount;

    private Double availableBalance;
}
