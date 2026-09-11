package com.bank.system.management.dto.mapper;

import com.bank.system.management.dto.FDResponse;
import com.bank.system.management.entity.FixedDeposit;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FixedDepositMapper {
    FDResponse toResponse(
            FixedDeposit fixedDeposit);

    List<FDResponse> toResponseList(
            List<FixedDeposit> deposits);
}
