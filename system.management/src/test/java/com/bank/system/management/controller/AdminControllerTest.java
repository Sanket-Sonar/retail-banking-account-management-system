package com.bank.system.management.controller;

import com.bank.system.management.dto.CustomerResponse;
import com.bank.system.management.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void getAllCustomers_shouldReturnCustomerList() {
        List<CustomerResponse> customers = List.of(
                CustomerResponse.builder().id(1L).fullName("Alice").email("alice@gmail.com").role("CUSTOMER").build(),
                CustomerResponse.builder().id(2L).fullName("Bob").email("bob@gmail.com").role("CUSTOMER").build()
        );

        when(adminService.getAllCustomers()).thenReturn(customers);

        List<CustomerResponse> actual = adminController.getAllCustomers();

        assertEquals(2, actual.size());
        assertEquals("Alice", actual.get(0).getFullName());
        assertEquals("bob@gmail.com", actual.get(1).getEmail());
        verify(adminService).getAllCustomers();
    }
}
