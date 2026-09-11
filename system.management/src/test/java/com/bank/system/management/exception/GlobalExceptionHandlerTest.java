package com.bank.system.management.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleAlreadyExists_ShouldReturnConflict() {

        ResourceAlreadyExistsException ex =
                new ResourceAlreadyExistsException(
                        "User already exists");

        ResponseEntity<ApiErrorResponse> response =
                exceptionHandler.handleAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT,
                response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals("User already exists",
                response.getBody().getMessage());

        assertEquals(409,
                response.getBody().getStatus());
    }

    @Test
    void handleBadCredentials_ShouldReturnUnauthorized() {

        BadCredentialsException ex =
                new BadCredentialsException(
                        "Bad credentials");

        ResponseEntity<ApiErrorResponse> response =
                exceptionHandler.handleBadCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED,
                response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(
                "Invalid email or password",
                response.getBody().getMessage());

        assertEquals(
                401,
                response.getBody().getStatus());
    }
    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {

        IllegalArgumentException ex =
                new IllegalArgumentException(
                        "Invalid tenure");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler
                        .handleIllegalArgumentException(ex);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(
                400,
                response.getBody().get("status"));

        assertEquals(
                "Invalid tenure",
                response.getBody().get("message"));
    }
}