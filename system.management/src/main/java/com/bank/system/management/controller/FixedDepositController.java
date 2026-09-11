package com.bank.system.management.controller;

import com.bank.system.management.dto.CreateFDRequest;
import com.bank.system.management.dto.FDCalculatorResponse;
import com.bank.system.management.dto.FDResponse;
import com.bank.system.management.service.FixedDepositService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fd")
@RequiredArgsConstructor
@Tag(name = "Fixed Deposit APIs")
@Slf4j
public class FixedDepositController {
    private final FixedDepositService service;

    @PostMapping
    public ResponseEntity<FDResponse> createFD(
            @Valid
            @RequestBody CreateFDRequest request,
            Authentication authentication) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createFD(
                                request,
                                authentication.getName()));
    }

    @GetMapping("/{fdNumber}")
    public ResponseEntity<FDResponse> getFDDetails(
            @PathVariable String fdNumber) {
            log.info("Fetching FD details for FD number: {}", fdNumber);
        return ResponseEntity.ok(
                service.getFDDetails(fdNumber));
    }

    @GetMapping("/my-fds")
    public ResponseEntity<List<FDResponse>>
    getMyFds(
            Authentication authentication) {
        log.info("Fetching My FDs for FD number: {}", authentication.getName());
        return ResponseEntity.ok(
                service.getMyFds(
                        authentication.getName()));
    }

    @GetMapping("/calculator")
    public ResponseEntity<FDCalculatorResponse>
    calculate(
            @RequestParam Double amount,
            @RequestParam Integer tenure) {
        log.info("Calculating funds for FD number: {}", amount);
        return ResponseEntity.ok(
                service.calculateFd(
                        amount,
                        tenure));
    }
}
