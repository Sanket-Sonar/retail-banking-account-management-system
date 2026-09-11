package com.bank.system.management.service;

import com.bank.system.management.dto.AccountRequest;
import com.bank.system.management.dto.AccountResponse;
import com.bank.system.management.dto.UpdateProfileRequest;
import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.Transaction;
import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import com.bank.system.management.enums.TransactionType;
import com.bank.system.management.exception.ResourceNotFoundException;
import com.bank.system.management.repository.AccountRepository;
import com.bank.system.management.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createAccount_shouldPersistAndReturnResponse() {
        AccountRequest request = new AccountRequest();
        request.setCustomerName("Jane Doe");
        request.setEmail("jane@gmail.com");
        request.setAccountType(AccountType.SAVINGS);
        request.setBranchName("Main Branch");
        request.setMobileNumber("9988776655");
        request.setAddress("Hyderabad");

        AccountResponse response = accountService.createAccount(request);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(repository).save(accountCaptor.capture());

        Account saved = accountCaptor.getValue();
        assertAll(
                () -> assertEquals("Jane Doe", saved.getCustomerName()),
                () -> assertEquals("jane@gmail.com", saved.getEmail()),
                () -> assertEquals(AccountType.SAVINGS, saved.getAccountType()),
                () -> assertEquals("Main Branch", saved.getBranchName()),
                () -> assertEquals(BigDecimal.ZERO, saved.getCurrentBalance()),
                () -> assertEquals(AccountStatus.ACTIVE, saved.getAccountStatus()),
                () -> assertEquals("ACC", saved.getAccountNumber().substring(0, 3)),
                () -> assertEquals("Jane Doe", response.getCustomerName()),
                () -> assertEquals("jane@gmail.com", response.getEmail())
        );
    }

    @Test
    void getAccount_shouldReturnAccountForOwner() {
        Account account = Account.builder()
                .accountNumber("ACC-123")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.CURRENT)
                .branchName("Downtown")
                .openingDate(java.time.LocalDate.now())
                .currentBalance(new BigDecimal("5000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("1234567890")
                .address("Pune")
                .build();

        when(repository.findByAccountNumber("ACC-123")).thenReturn(Optional.of(account));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@gmail.com", "pw",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        AccountResponse response = accountService.getAccount("ACC-123", "john@gmail.com");

        assertEquals("ACC-123", response.getAccountNumber());
        assertEquals(new BigDecimal("5000.00"), response.getCurrentBalance());
    }

    @Test
    void getAccount_shouldRejectUnauthorizedAccess() {
        Account account = Account.builder()
                .accountNumber("ACC-123")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.CURRENT)
                .currentBalance(new BigDecimal("5000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(repository.findByAccountNumber("ACC-123")).thenReturn(Optional.of(account));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("other@gmail.com", "pw",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        assertThrows(AccessDeniedException.class,
                () -> accountService.getAccount("ACC-123", "john@gmail.com"));
    }

    @Test
    void creditAmount_shouldIncreaseBalanceAndSaveTransaction() {
        Account account = Account.builder()
                .accountNumber("ACC-200")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.SAVINGS)
                .currentBalance(new BigDecimal("1000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(repository.findByAccountNumber("ACC-200")).thenReturn(Optional.of(account));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@gmail.com", "pw",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        AccountResponse response = accountService.creditAmount("ACC-200", 250.0, "john@gmail.com");

        verify(repository).save(account);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(new BigDecimal("1250.00"), response.getCurrentBalance());
        assertEquals(TransactionType.CREDIT, savedTransaction.getTransactionType());
        assertEquals(250.0, savedTransaction.getTransactionAmount());
        assertEquals("ACC-200", savedTransaction.getAccount().getAccountNumber());
    }

    @Test
    void debitAmount_shouldReduceBalanceAndSaveTransaction() {
        Account account = Account.builder()
                .accountNumber("ACC-300")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.SAVINGS)
                .currentBalance(new BigDecimal("1000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(repository.findByAccountNumber("ACC-300")).thenReturn(Optional.of(account));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@gmail.com", "pw",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        AccountResponse response = accountService.debitAmount("ACC-300", 200.0, "john@gmail.com");

        assertEquals(new BigDecimal("800.00"), response.getCurrentBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void debitAmount_shouldThrowWhenBalanceIsInsufficient() {
        Account account = Account.builder()
                .accountNumber("ACC-400")
                .customerName("John Doe")
                .email("john@gmail.com")
                .accountType(AccountType.SAVINGS)
                .currentBalance(new BigDecimal("100.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(repository.findByAccountNumber("ACC-400")).thenReturn(Optional.of(account));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@gmail.com", "pw",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.debitAmount("ACC-400", 200.0, "john@gmail.com"));

        assertEquals("Insufficient Balance", ex.getMessage());
    }

    @Test
    void updateProfile_shouldUpdateMobileAndAddress() {

        Account account = Account.builder()
                .accountNumber("ACC-500")
                .customerName("John Doe")
                .email("john@gmail.com")
                .mobileNumber("9999999999")
                .address("Old Address")
                .build();

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setMobileNumber("8888888888");
        request.setAddress("New Address");

        when(repository.findByAccountNumber("ACC-500"))
                .thenReturn(Optional.of(account));

        AccountResponse response =
                accountService.updateProfile(
                        "ACC-500",
                        request);

        verify(repository).save(account);

        assertEquals(
                "8888888888",
                account.getMobileNumber());

        assertEquals(
                "New Address",
                account.getAddress());

        assertEquals(
                "8888888888",
                response.getMobileNumber());

        assertEquals(
                "New Address",
                response.getAddress());
    }

    @Test
    void updateProfile_shouldThrowException_WhenAccountNotFound() {

        UpdateProfileRequest request =
                new UpdateProfileRequest();

        request.setMobileNumber("8888888888");
        request.setAddress("New Address");

        when(repository.findByAccountNumber("ACC-500"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> accountService.updateProfile(
                                "ACC-500",
                                request));

        assertEquals(
                "Account not found",
                exception.getMessage());
    }

    @Test
    void getAccount_shouldThrowException_WhenAccountNotFound() {

        when(repository.findByAccountNumber("ACC-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.getAccount(
                        "ACC-999",
                        "john@gmail.com"));
    }

    @Test
    void creditAmount_shouldThrowException_WhenAccountNotFound() {

        when(repository.findByAccountNumber("ACC-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.creditAmount(
                        "ACC-999",
                        100.0,
                        "john@gmail.com"));
    }

    @Test
    void debitAmount_shouldThrowException_WhenAccountNotFound() {

        when(repository.findByAccountNumber("ACC-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.debitAmount(
                        "ACC-999",
                        100.0,
                        "john@gmail.com"));
    }


}
