package com.bank.system.management.dto.mapper;

import com.bank.system.management.dto.TransactionResponse;
import com.bank.system.management.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(
            source = "account.accountNumber",
            target = "accountNumber")
    @Mapping(
            source = "transactionType",
            target = "transactionType")
    TransactionResponse toResponse(
            Transaction transaction);

    List<TransactionResponse> toResponseList(
            List<Transaction> transactions);
}
