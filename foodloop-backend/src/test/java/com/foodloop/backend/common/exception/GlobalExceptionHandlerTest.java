package com.foodloop.backend.common.exception;

import com.foodloop.backend.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException returning 404")
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "id", "123");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found with id: '123'", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException returning 400")
    void shouldHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON", (org.springframework.http.HttpInputMessage) null);
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Malformed JSON request", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException returning 409 Conflict")
    void shouldHandleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Email already exists");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleDuplicateResourceException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Email already exists", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle BadCredentialsException returning 401 Unauthorized")
    void shouldHandleBadCredentialsException() {
        org.springframework.security.authentication.BadCredentialsException ex =
                new org.springframework.security.authentication.BadCredentialsException("Bad credentials");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleBadCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid email or password", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle LockedException returning 403 Forbidden")
    void shouldHandleLockedException() {
        org.springframework.security.authentication.LockedException ex =
                new org.springframework.security.authentication.LockedException("User account is locked");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleLockedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User account is locked", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle DisabledException returning 403 Forbidden")
    void shouldHandleDisabledException() {
        org.springframework.security.authentication.DisabledException ex =
                new org.springframework.security.authentication.DisabledException("User account is inactive");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleDisabledException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User account is inactive", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle AccessDeniedException returning 403 Forbidden")
    void shouldHandleAccessDeniedException() {
        org.springframework.security.access.AccessDeniedException ex =
                new org.springframework.security.access.AccessDeniedException("Access denied");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException returning 400 Bad Request")
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument value");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid argument value", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle general Exception returning 500 without leaking stack traces")
    void shouldHandleGeneralException() {
        RuntimeException ex = new RuntimeException("Internal database connection error at line 42");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
    }
}
