package com.bank.system.management.service;

import com.bank.system.management.dto.AuthResponse;
import com.bank.system.management.dto.LoginRequest;
import com.bank.system.management.dto.RegisterRequest;
import com.bank.system.management.entity.User;
import com.bank.system.management.enums.Role;
import com.bank.system.management.exception.ResourceAlreadyExistsException;
import com.bank.system.management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldSaveUserWhenEmailIsNew() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Jane Doe");
        request.setEmail("jane@gmail.com");
        request.setPassword("Abcdef1@");
        request.setRole(Role.CUSTOMER);

        when(repository.existsByEmail("jane@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Abcdef1@")).thenReturn("encoded-pass");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("Jane Doe", savedUser.getFullName());
        assertEquals("jane@gmail.com", savedUser.getEmail());
        assertEquals("encoded-pass", savedUser.getPassword());
        assertEquals(Role.CUSTOMER, savedUser.getRole());
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("jane@gmail.com");
        request.setPassword("Abcdef1@");
        request.setRole(Role.CUSTOMER);

        when(repository.existsByEmail("jane@gmail.com")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> authService.register(request));

        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void login_shouldAuthenticateAndReturnJwtToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@gmail.com");
        request.setPassword("Abcdef1@");

        when(jwtService.generateToken("john@gmail.com")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        verify(authManager).authenticate(any());
        verify(jwtService).generateToken("john@gmail.com");
        assertEquals("jwt-token", response.getToken());
    }
}
