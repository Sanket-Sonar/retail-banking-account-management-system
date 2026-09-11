package com.bank.system.management.controller;

import com.bank.system.management.dto.AccountRequest;
import com.bank.system.management.dto.AccountResponse;
import com.bank.system.management.dto.UpdateProfileRequest;
import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import com.bank.system.management.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("john@gmail.com", "pw");
    }

    @Test
    void createAccount_shouldReturnCreatedAccount() {
        AccountRequest request = new AccountRequest();
        request.setCustomerName("John Doe");
        request.setEmail("john@gmail.com");
        request.setAccountType(AccountType.SAVINGS);
        request.setBranchName("Main Branch");
        request.setMobileNumber("9988776655");
        request.setAddress("Pune");

        AccountResponse response = AccountResponse.builder()
                .accountNumber("ACC-1001")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.SAVINGS)
                .branchName("Main Branch")
                .openingDate(LocalDate.of(2024, 1, 10))
                .currentBalance(new BigDecimal("1000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("9988776655")
                .address("Pune")
                .build();

        when(accountService.createAccount(any(AccountRequest.class))).thenReturn(response);

        ResponseEntity<AccountResponse> actual = accountController.createAccount(request);

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals("ACC-1001", actual.getBody().getAccountNumber());
        assertEquals("John Doe", actual.getBody().getCustomerName());
        verify(accountService).createAccount(any(AccountRequest.class));
    }

    @Test
    void getAccount_shouldReturnAccountForAuthenticatedUser() {
        AccountResponse response = AccountResponse.builder()
                .accountNumber("ACC-1001")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.SAVINGS)
                .branchName("Main Branch")
                .openingDate(LocalDate.of(2024, 1, 10))
                .currentBalance(new BigDecimal("1500.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("9988776655")
                .address("Pune")
                .build();

        when(accountService.getAccount("ACC-1001", "john@gmail.com")).thenReturn(response);

        AccountResponse actual = accountController.getAccount("ACC-1001", authentication());

        assertEquals("ACC-1001", actual.getAccountNumber());
        assertEquals(new BigDecimal("1500.00"), actual.getCurrentBalance());
        verify(accountService).getAccount("ACC-1001", "john@gmail.com");
    }

    @Test
    void updateProfile_shouldReturnUpdatedAccount() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setMobileNumber("9090909090");
        request.setAddress("Hyderabad");

        AccountResponse response = AccountResponse.builder()
                .accountNumber("ACC-1001")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.SAVINGS)
                .branchName("Main Branch")
                .openingDate(LocalDate.of(2024, 1, 10))
                .currentBalance(new BigDecimal("1500.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("9090909090")
                .address("Hyderabad")
                .build();

        when(accountService.updateProfile("ACC-1001", request)).thenReturn(response);

        AccountResponse actual = accountController.updateProfile("ACC-1001", request);

        assertEquals("9090909090", actual.getMobileNumber());
        assertEquals("Hyderabad", actual.getAddress());
        verify(accountService).updateProfile("ACC-1001", request);
    }

    @Test
    void getBalance_shouldReturnCurrentBalance() {
        AccountResponse response = AccountResponse.builder()
                .accountNumber("ACC-1001")
                .currentBalance(new BigDecimal("2500.50"))
                .build();

        when(accountService.getAccount("ACC-1001", "john@gmail.com")).thenReturn(response);

        ResponseEntity<BigDecimal> actual = accountController.getBalance("ACC-1001", authentication());

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(new BigDecimal("2500.50"), actual.getBody());
        verify(accountService).getAccount("ACC-1001", "john@gmail.com");
    }

    @Test
    void creditAmount_shouldReturnUpdatedAccount() {
        AccountResponse response = AccountResponse.builder()
                .accountNumber("ACC-1001")
                .customerName("John Doe")
                .currentBalance(new BigDecimal("2600.00"))
                .build();

        when(accountService.creditAmount("ACC-1001", 100.0, "john@gmail.com")).thenReturn(response);

        AccountResponse actual = accountController.creditAmount("ACC-1001", 100.0, authentication());

        assertEquals(new BigDecimal("2600.00"), actual.getCurrentBalance());
        verify(accountService).creditAmount("ACC-1001", 100.0, "john@gmail.com");
    }

    @Test
    void debitAmount_shouldReturnUpdatedAccount() {
        AccountResponse response = AccountResponse.builder()
                .accountNumber("ACC-1001")
                .customerName("John Doe")
                .currentBalance(new BigDecimal("2300.00"))
                .build();

        when(accountService.debitAmount("ACC-1001", 200.0, "john@gmail.com")).thenReturn(response);

        AccountResponse actual = accountController.debitAmount("ACC-1001", 200.0, authentication());

        assertEquals(new BigDecimal("2300.00"), actual.getCurrentBalance());
        verify(accountService).debitAmount("ACC-1001", 200.0, "john@gmail.com");
    }
}
