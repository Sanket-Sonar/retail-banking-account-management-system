package com.bank.system.management.service;

import com.bank.system.management.entity.User;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {

        User user = User.builder()
                .email("test@gmail.com")
                .password("encodedPassword")
                .role(com.bank.system.management.enums.Role.CUSTOMER)
                .build();

        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("test@gmail.com");

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(auth ->
                                auth.getAuthority()
                                        .equals("ROLE_CUSTOMER"))
        );

        verify(repository).findByEmail("test@gmail.com");
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
                "User Not Found",
                exception.getMessage());

        verify(repository).findByEmail("test@gmail.com");
    }
}