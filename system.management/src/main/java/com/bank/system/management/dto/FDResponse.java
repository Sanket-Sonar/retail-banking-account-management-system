package com.bank.system.management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FDResponse {
    private String fdNumber;

    private Double depositAmount;

    private Double interestRate;

    private Integer tenureYears;

    private LocalDate depositDate;

    private LocalDate maturityDate;

    private Double maturityAmount;
}
