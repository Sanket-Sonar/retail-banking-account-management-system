package com.bank.system.management.service;

import com.bank.system.management.dto.CreateFDRequest;
import com.bank.system.management.dto.FDCalculatorResponse;
import com.bank.system.management.dto.FDResponse;

import java.util.List;

public interface FixedDepositService {
    FDResponse createFD(
            CreateFDRequest request,
            String email);

    FDResponse getFDDetails(
            String fdNumber);

    List<FDResponse> getMyFds(
            String email);

    FDCalculatorResponse calculateFd(
            Double amount,
            Integer tenure);
}
