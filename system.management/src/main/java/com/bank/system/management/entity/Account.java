package com.bank.system.management.entity;

import com.bank.system.management.enums.AccountStatus;
import com.bank.system.management.enums.AccountType;
import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountNumber;

    private String customerName;

    private String email;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private String branchName;

    private LocalDate openingDate;

    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private String mobileNumber;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Transaction> transactions;
}
