package com.bank.system.management.repository;

import com.bank.system.management.entity.FixedDeposit;
import com.bank.system.management.entity.User;
import com.bank.system.management.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FixedDepositRepositoryTest {

    @Autowired
    private FixedDepositRepository fixedDepositRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByFdNumber_shouldReturnFixedDeposit() {
        User customer = userRepository.save(User.builder()
                .fullName("Customer")
                .email("customer@gmail.com")
                .password("secret")
                .role(Role.CUSTOMER)
                .build());

        FixedDeposit fd = fixedDepositRepository.save(FixedDeposit.builder()
                .fdNumber("FD-100")
                .depositAmount(50000.0)
                .interestRate(6.5)
                .tenureYears(1)
                .depositDate(LocalDate.of(2024, 1, 1))
                .maturityDate(LocalDate.of(2025, 1, 1))
                .maturityAmount(53000.0)
                .customer(customer)
                .build());

        Optional<FixedDeposit> found = fixedDepositRepository.findByFdNumber("FD-100");

        assertTrue(found.isPresent());
        assertEquals(fd.getId(), found.get().getId());
        assertEquals("FD-100", found.get().getFdNumber());
    }

    @Test
    void findByCustomer_shouldReturnAllDepositsForCustomer() {
        User customer = userRepository.save(User.builder()
                .fullName("Customer")
                .email("customer2@gmail.com")
                .password("secret")
                .role(Role.CUSTOMER)
                .build());

        fixedDepositRepository.saveAll(List.of(
                FixedDeposit.builder().fdNumber("FD-A").depositAmount(20000.0).interestRate(6.5).tenureYears(1).depositDate(LocalDate.of(2024, 1, 1)).maturityDate(LocalDate.of(2025, 1, 1)).maturityAmount(21200.0).customer(customer).build(),
                FixedDeposit.builder().fdNumber("FD-B").depositAmount(30000.0).interestRate(7.0).tenureYears(3).depositDate(LocalDate.of(2024, 2, 1)).maturityDate(LocalDate.of(2027, 2, 1)).maturityAmount(37000.0).customer(customer).build()
        ));

        List<FixedDeposit> deposits = fixedDepositRepository.findByCustomer(customer);

        assertEquals(2, deposits.size());
        assertTrue(deposits.stream().allMatch(fd -> fd.getCustomer().getId().equals(customer.getId())));
    }
}
