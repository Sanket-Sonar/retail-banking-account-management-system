package com.bank.system.management.controller;

import com.bank.system.management.dto.CreateFDRequest;
import com.bank.system.management.dto.FDCalculatorResponse;
import com.bank.system.management.dto.FDResponse;
import com.bank.system.management.service.FixedDepositService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixedDepositControllerTest {

    @Mock
    private FixedDepositService service;

    @InjectMocks
    private FixedDepositController controller;

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("john@gmail.com", "pw");
    }

    @Test
    void createFD_shouldReturnCreatedFd() {
        CreateFDRequest request = new CreateFDRequest();
        request.setDepositAmount(25000.0);
        request.setTenureYears(3);

        FDResponse response = FDResponse.builder()
                .fdNumber("FD-100")
                .depositAmount(25000.0)
                .interestRate(7.5)
                .tenureYears(3)
                .depositDate(LocalDate.of(2024, 1, 1))
                .maturityDate(LocalDate.of(2027, 1, 1))
                .maturityAmount(31000.0)
                .build();

        when(service.createFD(any(CreateFDRequest.class), any(String.class))).thenReturn(response);

        ResponseEntity<FDResponse> actual = controller.createFD(request, authentication());

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals("FD-100", actual.getBody().getFdNumber());
        assertEquals(31000.0, actual.getBody().getMaturityAmount());
        verify(service).createFD(any(CreateFDRequest.class), any(String.class));
    }

    @Test
    void getFDDetails_shouldReturnFdDetails() {
        FDResponse response = FDResponse.builder()
                .fdNumber("FD-200")
                .depositAmount(20000.0)
                .interestRate(6.5)
                .tenureYears(2)
                .depositDate(LocalDate.of(2024, 6, 1))
                .maturityDate(LocalDate.of(2026, 6, 1))
                .maturityAmount(23000.0)
                .build();

        when(service.getFDDetails("FD-200")).thenReturn(response);

        ResponseEntity<FDResponse> actual = controller.getFDDetails("FD-200");

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("FD-200", actual.getBody().getFdNumber());
        assertEquals(23000.0, actual.getBody().getMaturityAmount());
        verify(service).getFDDetails("FD-200");
    }

    @Test
    void getMyFds_shouldReturnUserFds() {
        List<FDResponse> responses = List.of(
                FDResponse.builder().fdNumber("FD-1").depositAmount(10000.0).interestRate(6.0).tenureYears(1).depositDate(LocalDate.of(2023, 1, 1)).maturityDate(LocalDate.of(2024, 1, 1)).maturityAmount(10600.0).build(),
                FDResponse.builder().fdNumber("FD-2").depositAmount(20000.0).interestRate(7.0).tenureYears(2).depositDate(LocalDate.of(2024, 2, 1)).maturityDate(LocalDate.of(2026, 2, 1)).maturityAmount(23000.0).build()
        );

        when(service.getMyFds("john@gmail.com")).thenReturn(responses);

        ResponseEntity<List<FDResponse>> actual = controller.getMyFds(authentication());

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("FD-1", actual.getBody().get(0).getFdNumber());
        assertEquals("FD-2", actual.getBody().get(1).getFdNumber());
        verify(service).getMyFds("john@gmail.com");
    }

    @Test
    void calculate_shouldReturnFdCalculation() {
        FDCalculatorResponse response = FDCalculatorResponse.builder()
                .depositAmount(100000.0)
                .interestRate(7.5)
                .tenureYears(5)
                .maturityAmount(145000.0)
                .build();

        when(service.calculateFd(100000.0, 5)).thenReturn(response);

        ResponseEntity<FDCalculatorResponse> actual = controller.calculate(100000.0, 5);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(100000.0, actual.getBody().getDepositAmount());
        assertEquals(145000.0, actual.getBody().getMaturityAmount());
        verify(service).calculateFd(100000.0, 5);
    }
}
