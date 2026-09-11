package com.bank.system.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "fixed_deposit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fdNumber;

    private Double depositAmount;

    private Double interestRate;

    private Integer tenureYears;

    private LocalDate depositDate;

    private LocalDate maturityDate;

    private Double maturityAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

}
