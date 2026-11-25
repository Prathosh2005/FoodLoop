package com.foodloop.backend.user.entity;

/**
 * Account lifecycle status for users in FoodLoop.
 */
public enum UserStatus {
    /**
     * Active user account with normal system access.
     */
    ACTIVE,

    /**
     * Deactivated or pending user account.
     */
    INACTIVE,

    /**
     * Account locked due to security policy violations or failed authentication attempts.
     */
    LOCKED
}
