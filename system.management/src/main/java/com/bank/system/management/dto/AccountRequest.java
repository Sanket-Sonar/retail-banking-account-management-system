package com.bank.system.management.dto;

import com.bank.system.management.enums.AccountType;
import lombok.Data;

@Data
public class AccountRequest {
    private String customerName;

    private String email;

    private AccountType accountType;

    private String branchName;

    private String mobileNumber;

    private String address;
}
