package com.foodloop.backend.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    @DisplayName("Should construct success response with default message and payload")
    void shouldCreateSuccessResponseWithData() {
        ApiResponse<String> response = ApiResponse.success("test data");

        assertTrue(response.isSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertEquals("test data", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should construct success response with custom message and payload")
    void shouldCreateSuccessResponseWithMessageAndData() {
        ApiResponse<Integer> response = ApiResponse.success("Custom success", 42);

        assertTrue(response.isSuccess());
        assertEquals("Custom success", response.getMessage());
        assertEquals(42, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should construct error response with message and null payload")
    void shouldCreateErrorResponseWithMessage() {
        ApiResponse<Void> response = ApiResponse.error("Something went wrong");

        assertFalse(response.isSuccess());
        assertEquals("Something went wrong", response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should construct error response with message and payload")
    void shouldCreateErrorResponseWithMessageAndData() {
        ApiResponse<String> response = ApiResponse.error("Validation failed", "Invalid input");

        assertFalse(response.isSuccess());
        assertEquals("Validation failed", response.getMessage());
        assertEquals("Invalid input", response.getData());
        assertNotNull(response.getTimestamp());
    }
}
