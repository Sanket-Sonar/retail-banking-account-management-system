package com.bank.system.management.controller;

import com.bank.system.management.dto.CustomerResponse;
import com.bank.system.management.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("john@gmail.com", "pw");
    }

    @Test
    void profile_shouldReturnCustomerProfile() {
        CustomerResponse response = CustomerResponse.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@gmail.com")
                .role("CUSTOMER")
                .build();

        when(customerService.getProfile("john@gmail.com")).thenReturn(response);

        CustomerResponse actual = customerController.profile(authentication());

        assertEquals("John Doe", actual.getFullName());
        assertEquals("john@gmail.com", actual.getEmail());
        verify(customerService).getProfile("john@gmail.com");
    }
}
