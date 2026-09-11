package com.bank.system.management.service.impl;

import com.bank.system.management.entity.User;
import com.bank.system.management.enums.Role;
import com.bank.system.management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("test@gmail.com");

        assertNotNull(result);
        assertEquals("test@gmail.com",
                result.getUsername());

        assertEquals("encodedPassword",
                result.getPassword());

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_CUSTOMER"))
        );
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {

        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> service.loadUserByUsername(
                                "test@gmail.com"));

        assertEquals(
                "User not found",
                exception.getMessage());
    }
}