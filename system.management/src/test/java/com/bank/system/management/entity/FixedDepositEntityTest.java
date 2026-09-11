package com.bank.system.management.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FixedDepositEntityTest {

    @Test
    void builderAndAccessors_shouldSetFixedDepositFields() {
        User customer = User.builder()
                .id(7L)
                .fullName("Alice Smith")
                .email("alice@gmail.com")
                .password("pass")
                .build();

        FixedDeposit fixedDeposit = FixedDeposit.builder()
                .id(11L)
                .fdNumber("FD-900")
                .depositAmount(50000.0)
                .interestRate(6.8)
                .tenureYears(3)
                .depositDate(LocalDate.of(2024, 1, 10))
                .maturityDate(LocalDate.of(2027, 1, 10))
                .maturityAmount(62000.0)
                .customer(customer)
                .build();

        assertAll(
                () -> assertEquals(11L, fixedDeposit.getId()),
                () -> assertEquals("FD-900", fixedDeposit.getFdNumber()),
                () -> assertEquals(50000.0, fixedDeposit.getDepositAmount()),
                () -> assertEquals(6.8, fixedDeposit.getInterestRate()),
                () -> assertEquals(3, fixedDeposit.getTenureYears()),
                () -> assertEquals(LocalDate.of(2024, 1, 10), fixedDeposit.getDepositDate()),
                () -> assertEquals(LocalDate.of(2027, 1, 10), fixedDeposit.getMaturityDate()),
                () -> assertEquals(62000.0, fixedDeposit.getMaturityAmount()),
                () -> assertSame(customer, fixedDeposit.getCustomer())
        );

        User newCustomer = User.builder().id(8L).fullName("Bob Jones").build();
        fixedDeposit.setCustomer(newCustomer);
        assertSame(newCustomer, fixedDeposit.getCustomer());
    }
}
