package com.bank.system.management.exception;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionTest {

    @Test
    void resourceAlreadyExistsException_shouldKeepMessage() {
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Email already registered");

        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void resourceNotFoundException_shouldKeepMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Account not found");

        assertEquals("Account not found", ex.getMessage());
    }

    @Test
    void apiErrorResponse_shouldStorePayloadCorrectly() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 10, 15, 0);
        ApiErrorResponse response = new ApiErrorResponse("Invalid request", 400, now);

        assertAll(
                () -> assertEquals("Invalid request", response.getMessage()),
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals(now, response.getTimestamp())
        );
    }

    @Test
    void globalExceptionHandler_shouldReturnConflictResponse_forAlreadyExistsException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Email already registered");

        var response = handler.handleAlreadyExists(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Email already registered", response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void globalExceptionHandler_shouldReturnUnauthorizedResponse_forBadCredentials() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        var response = handler.handleBadCredentials(ex);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Invalid email or password", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void globalExceptionHandler_shouldReturnNotFoundResponse_forResourceNotFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found");

        var response = handler.handleResourceNotFoundException(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void globalExceptionHandler_shouldReturnForbiddenResponse_forAccessDenied() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        AccessDeniedException ex = new AccessDeniedException("Unauthorized access");

        var response = handler.handleAccessDeniedException(ex);

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Unauthorized access", response.getBody().getMessage());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void globalExceptionHandler_shouldReturnBadRequestForIllegalArgument() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        IllegalArgumentException ex = new IllegalArgumentException("Invalid amount");

        var response = handler.handleIllegalArgumentException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid amount", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }
}
