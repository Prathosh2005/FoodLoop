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
    SUSPENDED;

    /**
     * Determines whether an organisation with this status is eligible to participate
     * in operational activities such as donating or collecting food.
     * Only VERIFIED organisations can participate. PENDING, REJECTED, and SUSPENDED
     * organisations are strictly prohibited.
     *
     * @return true if VERIFIED, false otherwise
     */
    public boolean canParticipateInOperations() {
        return this == VERIFIED;
    }
}
