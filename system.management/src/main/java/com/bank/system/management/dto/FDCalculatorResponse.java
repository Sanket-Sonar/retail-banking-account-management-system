package com.bank.system.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FDCalculatorResponse {
    private Double depositAmount;

    private Double interestRate;

    private Integer tenureYears;

    private Double maturityAmount;


}
