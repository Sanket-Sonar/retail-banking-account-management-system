package com.bank.system.management.dto;

import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private String accountNumber;
    private String customerName;
    private String email;
    private AccountType accountType;
    private String branchName;
    private LocalDate openingDate;
    private BigDecimal currentBalance;
    private AccountStatus accountStatus;
    private String mobileNumber;
    private String address;
}
