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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getProfile_shouldReturnCustomerDetails() {
        User user = User.builder()
                .id(5L)
                .fullName("John Doe")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(user));

        CustomerResponse response = customerService.getProfile("john@gmail.com");

        assertEquals(5L, response.getId());
        assertEquals("John Doe", response.getFullName());
        assertEquals("john@gmail.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());

        verify(userRepository).findByEmail("john@gmail.com");
    }

    @Test
    void getProfile_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@gmail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customerService.getProfile("missing@gmail.com"));

        assertEquals("User not found", ex.getMessage());
    }
}
