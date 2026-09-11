package com.bank.system.management.repository;

import com.bank.system.management.entity.FixedDeposit;
import com.bank.system.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
    Optional<FixedDeposit> findByFdNumber(
            String fdNumber);

    List<FixedDeposit> findByCustomer(
            User customer);
}
