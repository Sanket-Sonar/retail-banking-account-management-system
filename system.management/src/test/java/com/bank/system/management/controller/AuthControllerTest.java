package com.bank.system.management.controller;

import com.bank.system.management.dto.AuthResponse;
import com.bank.system.management.dto.LoginRequest;
import com.bank.system.management.dto.RegisterRequest;
import com.bank.system.management.enums.Role;
import com.bank.system.management.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_shouldReturnCreatedMessage() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john@gmail.com");
        request.setPassword("Abcdef1@");
        request.setRole(Role.CUSTOMER);

        doNothing().when(authService).register(any(RegisterRequest.class));

        ResponseEntity<String> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User Registered Successfully", response.getBody());
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@gmail.com");
        request.setPassword("Abcdef1@");

        AuthResponse expected = new AuthResponse("jwt-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(expected);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().getToken());
        verify(authService).login(any(LoginRequest.class));
    }
}
