package com.foodloop.backend.user.entity;

/**
 * Roles defining user authorization and system access levels in FoodLoop.
 */
public enum UserRole {
    /**
     * System administrators managing platform operations and organization verifications.
     * PLATFORM_ADMIN accounts do not belong to an organization.
     */
    PLATFORM_ADMIN,

    /**
     * Organization administrators managing organization profile, members, and operations.
     */
    ORGANIZATION_ADMIN,

    /**
     * Standard organization staff or volunteer members creating/processing donations or pickups.
     */
    ORGANIZATION_MEMBER
}
