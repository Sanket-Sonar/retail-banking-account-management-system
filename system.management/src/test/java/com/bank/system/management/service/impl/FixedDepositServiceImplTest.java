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
import com.bank.system.management.service.InterestCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FixedDepositServiceImplTest {

    @Mock
    private FixedDepositRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FixedDepositMapper mapper;

    @Mock
    private InterestCalculator calculator;

    @InjectMocks
    private FixedDepositServiceImpl service;

    private User user;
    private FixedDeposit fd;
    private FDResponse response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        fd = FixedDeposit.builder()
                .fdNumber("FD123")
                .depositAmount(10000.0)
                .interestRate(6.5)
                .tenureYears(1)
                .customer(user)
                .build();

        response = FDResponse.builder()
                .fdNumber("FD123")
                .depositAmount(10000.0)
                .interestRate(6.5)
                .tenureYears(1)
                .build();
    }

    @Test
    void createFD_ShouldCreateSuccessfully() {

        CreateFDRequest request = CreateFDRequest.builder()
                .depositAmount(10000.0)
                .tenureYears(1)
                .build();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(calculator.calculate(10000.0, 6.5, 1))
                .thenReturn(10650.0);

        when(mapper.toResponse(any(FixedDeposit.class)))
                .thenReturn(response);

        FDResponse result =
                service.createFD(request, "test@gmail.com");

        assertNotNull(result);
        assertEquals("FD123", result.getFdNumber());

        verify(userRepository).findByEmail("test@gmail.com");
        verify(calculator).calculate(10000.0, 6.5, 1);
        verify(repository).save(any(FixedDeposit.class));
        verify(mapper).toResponse(any(FixedDeposit.class));
    }

    @Test
    void createFD_ShouldThrowException_WhenCustomerNotFound() {

        CreateFDRequest request = CreateFDRequest.builder()
                .depositAmount(10000.0)
                .tenureYears(1)
                .build();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> service.createFD(request,
                                "test@gmail.com"));

        assertEquals("Customer not found",
                exception.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void getFDDetails_ShouldReturnFDDetails() {

        when(repository.findByFdNumber("FD123"))
                .thenReturn(Optional.of(fd));

        when(mapper.toResponse(fd))
                .thenReturn(response);

        FDResponse result =
                service.getFDDetails("FD123");

        assertNotNull(result);
        assertEquals("FD123", result.getFdNumber());

        verify(repository).findByFdNumber("FD123");
    }

    @Test
    void getFDDetails_ShouldThrowException_WhenFDNotFound() {

        when(repository.findByFdNumber("FD123"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> service.getFDDetails("FD123"));

        assertTrue(exception.getMessage()
                .contains("FD not found"));
    }

    @Test
    void getMyFds_ShouldReturnFds() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(repository.findByCustomer(user))
                .thenReturn(List.of(fd));

        when(mapper.toResponse(fd))
                .thenReturn(response);

        List<FDResponse> result =
                service.getMyFds("test@gmail.com");

        assertEquals(1, result.size());

        verify(userRepository).findByEmail("test@gmail.com");
        verify(repository).findByCustomer(user);
    }

    @Test
    void getMyFds_ShouldThrowException_WhenCustomerNotFound() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> service.getMyFds("test@gmail.com"));

        assertEquals("Customer not found",
                exception.getMessage());
    }

    @Test
    void calculateFd_ShouldCalculateForOneYear() {

        when(calculator.calculate(10000.0, 6.5, 1))
                .thenReturn(10650.0);

        FDCalculatorResponse response =
                service.calculateFd(10000.0, 1);

        assertEquals(10000.0,
                response.getDepositAmount());
        assertEquals(6.5,
                response.getInterestRate());
        assertEquals(10650.0,
                response.getMaturityAmount());
    }

    @Test
    void calculateFd_ShouldCalculateForThreeYears() {

        when(calculator.calculate(10000.0, 7.0, 3))
                .thenReturn(12100.0);

        FDCalculatorResponse response =
                service.calculateFd(10000.0, 3);

        assertEquals(7.0,
                response.getInterestRate());
        assertEquals(12100.0,
                response.getMaturityAmount());
    }

    @Test
    void calculateFd_ShouldCalculateForFiveYears() {

        when(calculator.calculate(10000.0, 7.5, 5))
                .thenReturn(14500.0);

        FDCalculatorResponse response =
                service.calculateFd(10000.0, 5);

        assertEquals(7.5,
                response.getInterestRate());
        assertEquals(14500.0,
                response.getMaturityAmount());
    }

    @Test
    void calculateFd_ShouldThrowException_ForInvalidTenure() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.calculateFd(10000.0, 2));

        assertEquals(
                "Tenure should be 1, 3 or 5 years",
                exception.getMessage());
    }
}