package com.bank.system.management.repository;

import com.bank.system.management.entity.Account;
import com.bank.system.management.entity.User;
import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import com.bank.system.management.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAccountNumber_shouldReturnAccount_whenAccountExists() {
        User user = userRepository.save(User.builder()
                .fullName("Alice")
                .email("alice@gmail.com")
                .password("pass")
                .role(Role.CUSTOMER)
                .build());

        Account account = accountRepository.save(Account.builder()
                .accountNumber("ACC-1001")
                .customerName("Alice")
                .email("alice@gmail.com")
                .accountType(AccountType.SAVINGS)
                .branchName("Main")
                .openingDate(LocalDate.of(2024, 1, 1))
                .currentBalance(new BigDecimal("2500.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .mobileNumber("9000000000")
                .address("Pune")
                .user(user)
                .build());

        Optional<Account> found = accountRepository.findByAccountNumber(account.getAccountNumber());

        assertTrue(found.isPresent());
        assertEquals("ACC-1001", found.get().getAccountNumber());
        assertEquals("Alice", found.get().getCustomerName());
    }
}
