package com.bank.system.management.service.impl;

import com.bank.system.management.dto.CreateFDRequest;
import com.bank.system.management.dto.FDCalculatorResponse;
import com.bank.system.management.dto.FDResponse;
import com.bank.system.management.dto.mapper.FixedDepositMapper;
import com.bank.system.management.entity.FixedDeposit;
import com.bank.system.management.entity.User;
import com.bank.system.management.exception.ResourceNotFoundException;
import com.bank.system.management.repository.FixedDepositRepository;
import com.bank.system.management.repository.UserRepository;
import com.bank.system.management.service.FixedDepositService;
import com.bank.system.management.service.InterestCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class FixedDepositServiceImpl implements FixedDepositService {

    private static final double ONE_YEAR_RATE = 6.5;
    private static final double THREE_YEAR_RATE = 7.0;
    private static final double FIVE_YEAR_RATE = 7.5;

    private final FixedDepositRepository repository;
    private final UserRepository userRepository;
    private final FixedDepositMapper mapper;
    private final InterestCalculator calculator;

    @Override
    public FDResponse createFD(CreateFDRequest request, String email) {
        User customer =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        double interestRate =
                getInterestRate(
                        request.getTenureYears());

        double maturityAmount =
                calculator.calculate(
                        request.getDepositAmount(),
                        interestRate,
                        request.getTenureYears());

        maturityAmount = Math.round(maturityAmount * 100.0) / 100.0;


        FixedDeposit fd =
                FixedDeposit.builder()
                        .fdNumber(generateFdNumber())
                        .depositAmount(
                                request.getDepositAmount())
                        .interestRate(interestRate)
                        .tenureYears(
                                request.getTenureYears())
                        .depositDate(LocalDate.now())
                        .maturityDate(
                                LocalDate.now()
                                        .plusYears(
                                                request.getTenureYears()))
                        .maturityAmount(maturityAmount)
                        .customer(customer)
                        .build();

        repository.save(fd);

        log.info("FD created {}", fd.getFdNumber());

        return mapper.toResponse(fd);
    }

    @Override
    public FDResponse getFDDetails(String fdNumber) {

        log.info("Fetching FD details for FD number: {}", fdNumber);

        FixedDeposit fd = repository.findByFdNumber(fdNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "FD not found with number: " + fdNumber));

        return mapper.toResponse(fd);
    }

    @Override
    public List<FDResponse> getMyFds(String email) {

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));

        return repository.findByCustomer(customer)
                .stream()
                .map(mapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public FDCalculatorResponse calculateFd(Double amount, Integer tenure) {

        double rate = getInterestRate(tenure);

        double maturityAmount =
                calculator.calculate(
                        amount,
                        rate,
                        tenure);
        maturityAmount = Math.round(maturityAmount * 100.0) / 100.0;

        return FDCalculatorResponse.builder()
                .depositAmount(amount)
                .interestRate(rate)
                .tenureYears(tenure)
                .maturityAmount(maturityAmount)
                .build();
    }


    private String generateFdNumber() {

        return "FD"
                + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(
                        "yyyyMMddHHmmss"))
                + (100 + new Random().nextInt(900));
    }

    private double getInterestRate(Integer tenure) {

        return switch (tenure) {

            case 1 -> 6.5;
            case 3 -> 7.0;
            case 5 -> 7.5;

            default -> throw new IllegalArgumentException(
                    "Tenure should be 1, 3 or 5 years");
        };
    }
}
