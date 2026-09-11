package com.bank.system.management.entity;

import com.bank.system.management.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void builderAndAccessors_shouldSetUserFields() {
        List<Account> accounts = new ArrayList<>();

        User user = User.builder()
                .id(1L)
                .fullName("Jane Doe")
                .email("jane@gmail.com")
                .password("encoded-password")
                .role(Role.CUSTOMER)
                .accounts(accounts)
                .build();

        assertAll(
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("Jane Doe", user.getFullName()),
                () -> assertEquals("jane@gmail.com", user.getEmail()),
                () -> assertEquals("encoded-password", user.getPassword()),
                () -> assertEquals(Role.CUSTOMER, user.getRole()),
                () -> assertSame(accounts, user.getAccounts())
        );

        List<Account> updatedAccounts = new ArrayList<>();
        user.setAccounts(updatedAccounts);
        assertSame(updatedAccounts, user.getAccounts());
    }
}
