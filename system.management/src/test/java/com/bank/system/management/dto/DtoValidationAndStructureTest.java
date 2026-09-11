package com.bank.system.management.dto;

import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import com.bank.system.management.enums.Role;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationAndStructureTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void accountRequest_shouldCaptureBasicAccountDetails() {
        AccountRequest request = new AccountRequest();
        request.setCustomerName("Jane Doe");
        request.setEmail("jane@example.com");
        request.setAccountType(AccountType.SAVINGS);
        request.setBranchName("Main Branch");
        request.setMobileNumber("9876543210");
        request.setAddress("Hyderabad");

        assertAll(
                () -> assertEquals("Jane Doe", request.getCustomerName()),
                () -> assertEquals("jane@example.com", request.getEmail()),
                () -> assertEquals(AccountType.SAVINGS, request.getAccountType()),
                () -> assertEquals("Main Branch", request.getBranchName()),
                () -> assertEquals("9876543210", request.getMobileNumber()),
                () -> assertEquals("Hyderabad", request.getAddress())
        );
    }

    @Test
    void accountResponse_shouldCreateAndExposeAllFields() {
        AccountResponse response = AccountResponse.builder()
                .accountNumber("ACC-1001")
                .customerName("Jane Doe")
                .email("jane@gmail.com")
                .accountType(AccountType.CURRENT)
                .branchName("Downtown")
                .openingDate(LocalDate.of(2024, 1, 10))
                .currentBalance(new BigDecimal("45000.50"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("9988776655")
                .address("Pune")
                .build();

        assertAll(
                () -> assertEquals("ACC-1001", response.getAccountNumber()),
                () -> assertEquals("Jane Doe", response.getCustomerName()),
                () -> assertEquals("jane@gmail.com", response.getEmail()),
                () -> assertEquals(AccountType.CURRENT, response.getAccountType()),
                () -> assertEquals("Downtown", response.getBranchName()),
                () -> assertEquals(LocalDate.of(2024, 1, 10), response.getOpeningDate()),
                () -> assertEquals(new BigDecimal("45000.50"), response.getCurrentBalance()),
                () -> assertEquals(AccountStatus.ACTIVE, response.getAccountStatus()),
                () -> assertEquals("9988776655", response.getMobileNumber()),
                () -> assertEquals("Pune", response.getAddress())
        );
    }

    @Test
    void authResponse_shouldStoreToken() {
        AuthResponse response = new AuthResponse("jwt-token");

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void createFdRequest_shouldValidateMinimumDepositAndRequiredTenure() {
        CreateFDRequest validRequest = new CreateFDRequest();
        validRequest.setDepositAmount(15000.0);
        validRequest.setTenureYears(3);

        assertTrue(validator.validate(validRequest).isEmpty());

        CreateFDRequest invalidAmount = new CreateFDRequest();
        invalidAmount.setDepositAmount(10000.0);
        invalidAmount.setTenureYears(1);

        var violations = validator.validate(invalidAmount);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("depositAmount")));

        CreateFDRequest missingTenure = new CreateFDRequest();
        missingTenure.setDepositAmount(20000.0);
        var missingViolations = validator.validate(missingTenure);
        assertTrue(missingViolations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("tenureYears")));
    }

    @Test
    void customerResponse_shouldExposeFields() {
        CustomerResponse response = CustomerResponse.builder()
                .id(7L)
                .fullName("John Smith")
                .email("john@gmail.com")
                .role("CUSTOMER")
                .build();

        assertAll(
                () -> assertEquals(7L, response.getId()),
                () -> assertEquals("John Smith", response.getFullName()),
                () -> assertEquals("john@gmail.com", response.getEmail()),
                () -> assertEquals("CUSTOMER", response.getRole())
        );
    }

    @Test
    void dashboardResponse_shouldStoreTotalsAndLastTransactions() {
        TransactionResponse transaction = TransactionResponse.builder()
                .transactionId("TXN-1")
                .accountNumber("ACC-1001")
                .transactionDate(LocalDateTime.of(2024, 10, 12, 8, 30))
                .transactionType("CREDIT")
                .transactionAmount(5000.0)
                .availableBalance(25000.0)
                .build();

        DashboardResponse dashboard = DashboardResponse.builder()
                .totalCredit(15000.0)
                .totalDebit(3000.0)
                .currentBalance(new BigDecimal("22000.00"))
                .lastTenTransactions(List.of(transaction))
                .build();

        assertAll(
                () -> assertEquals(15000.0, dashboard.getTotalCredit()),
                () -> assertEquals(3000.0, dashboard.getTotalDebit()),
                () -> assertEquals(new BigDecimal("22000.00"), dashboard.getCurrentBalance()),
                () -> assertEquals(1, dashboard.getLastTenTransactions().size()),
                () -> assertEquals("TXN-1", dashboard.getLastTenTransactions().get(0).getTransactionId())
        );
    }

    @Test
    void errorResponse_shouldStoreMessageStatusAndTimestamp() {
        LocalDateTime now = LocalDateTime.of(2024, 3, 15, 14, 0);
        ErrorResponse response = ErrorResponse.builder()
                .message("Account not found")
                .status(404)
                .timestamp(now)
                .build();

        assertAll(
                () -> assertEquals("Account not found", response.getMessage()),
                () -> assertEquals(404, response.getStatus()),
                () -> assertEquals(now, response.getTimestamp())
        );
    }

    @Test
    void fdCalculatorResponse_shouldExposeComputedValues() {
        FDCalculatorResponse response = FDCalculatorResponse.builder()
                .depositAmount(100000.0)
                .interestRate(7.5)
                .tenureYears(5)
                .maturityAmount(145000.0)
                .build();

        assertAll(
                () -> assertEquals(100000.0, response.getDepositAmount()),
                () -> assertEquals(7.5, response.getInterestRate()),
                () -> assertEquals(5, response.getTenureYears()),
                () -> assertEquals(145000.0, response.getMaturityAmount())
        );
    }

    @Test
    void fdResponse_shouldExposeDepositDetails() {
        FDResponse response = FDResponse.builder()
                .fdNumber("FD-123")
                .depositAmount(50000.0)
                .interestRate(6.2)
                .tenureYears(2)
                .depositDate(LocalDate.of(2024, 1, 1))
                .maturityDate(LocalDate.of(2026, 1, 1))
                .maturityAmount(58000.0)
                .build();

        assertAll(
                () -> assertEquals("FD-123", response.getFdNumber()),
                () -> assertEquals(50000.0, response.getDepositAmount()),
                () -> assertEquals(6.2, response.getInterestRate()),
                () -> assertEquals(2, response.getTenureYears()),
                () -> assertEquals(LocalDate.of(2024, 1, 1), response.getDepositDate()),
                () -> assertEquals(LocalDate.of(2026, 1, 1), response.getMaturityDate()),
                () -> assertEquals(58000.0, response.getMaturityAmount())
        );
    }

    @Test
    void loginRequest_shouldValidateEmailAndPasswordPresence() {
        LoginRequest validRequest = new LoginRequest();
        validRequest.setEmail("user@gmail.com");
        validRequest.setPassword("StrongPass1!");
        assertTrue(validator.validate(validRequest).isEmpty());

        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("");

        var violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void registerRequest_shouldAcceptValidDataAndRejectInvalidData() {
        RegisterRequest validRequest = new RegisterRequest();
        validRequest.setFullName("John Doe");
        validRequest.setEmail("john@gmail.com");
        validRequest.setPassword("Abcdef1@");
        validRequest.setRole(Role.CUSTOMER);

        assertTrue(validator.validate(validRequest).isEmpty());

        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setFullName("Jo");
        invalidRequest.setEmail("john@yahoo.com");
        invalidRequest.setPassword("weak");
        invalidRequest.setRole(null);

        var violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fullName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    void transactionResponse_shouldExposeTransactionPayload() {
        TransactionResponse response = TransactionResponse.builder()
                .transactionId("TXN-77")
                .accountNumber("ACC-1002")
                .transactionDate(LocalDateTime.of(2024, 9, 5, 12, 40))
                .transactionType("DEBIT")
                .transactionAmount(2500.0)
                .availableBalance(10000.0)
                .build();

        assertAll(
                () -> assertEquals("TXN-77", response.getTransactionId()),
                () -> assertEquals("ACC-1002", response.getAccountNumber()),
                () -> assertEquals(LocalDateTime.of(2024, 9, 5, 12, 40), response.getTransactionDate()),
                () -> assertEquals("DEBIT", response.getTransactionType()),
                () -> assertEquals(2500.0, response.getTransactionAmount()),
                () -> assertEquals(10000.0, response.getAvailableBalance())
        );
    }

    @Test
    void updateProfileRequest_shouldCaptureMobileAndAddress() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setMobileNumber("9000000000");
        request.setAddress("Bengaluru");

        assertAll(
                () -> assertEquals("9000000000", request.getMobileNumber()),
                () -> assertEquals("Bengaluru", request.getAddress())
        );
    }
}
