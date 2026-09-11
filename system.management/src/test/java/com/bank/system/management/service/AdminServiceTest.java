package com.bank.system.management.service;

import com.bank.system.management.dto.CustomerResponse;
import com.bank.system.management.entity.User;
import com.bank.system.management.enums.Role;
import com.bank.system.management.repository.UserRepository;
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
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getAllCustomers_shouldMapUsersToCustomerResponses() {
        User user1 = User.builder()
                .id(1L)
                .fullName("Alice")
                .email("alice@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        User user2 = User.builder()
                .id(2L)
                .fullName("Bob")
                .email("bob@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByRole(Role.CUSTOMER)).thenReturn(List.of(user1, user2));

        List<CustomerResponse> response = adminService.getAllCustomers();

        assertEquals(2, response.size());
        assertEquals("Alice", response.get(0).getFullName());
        assertEquals("alice@gmail.com", response.get(0).getEmail());
        assertEquals("CUSTOMER", response.get(0).getRole());
        assertEquals("Bob", response.get(1).getFullName());

        verify(userRepository).findByRole(Role.CUSTOMER);
    }
}
