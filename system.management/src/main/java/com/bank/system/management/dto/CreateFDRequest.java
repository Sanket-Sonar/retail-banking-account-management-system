package com.bank.system.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFDRequest {
    @NotNull(message = "Deposit amount is required")
    @DecimalMin(
            value = "10000.01",
            message = "Deposit amount should be greater than Rs 10000")
    private Double depositAmount;

    @NotNull(message = "Tenure is required")
    private Integer tenureYears;

}
