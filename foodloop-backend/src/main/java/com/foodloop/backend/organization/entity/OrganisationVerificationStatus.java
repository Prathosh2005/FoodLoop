package com.foodloop.backend.organization.entity;

/**
 * Verification lifecycle status for an organization on FoodLoop.
 */
public enum OrganisationVerificationStatus {
    /**
     * Initial status upon registration, pending review by PLATFORM_ADMIN.
     */
    PENDING,

    /**
     * Verified by PLATFORM_ADMIN; eligible to donate or request food.
     */
    VERIFIED,

    /**
     * Verification rejected due to invalid documentation or requirements.
     */
    REJECTED,

    /**
     * Temporarily or permanently suspended due to policy or compliance issues.
     */
    SUSPENDED
}
