package com.foodloop.backend.common.exception;

/**
 * Exception thrown when an attempt is made to create a resource that already exists
 * (e.g., duplicate user email, duplicate organisation registration number).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
