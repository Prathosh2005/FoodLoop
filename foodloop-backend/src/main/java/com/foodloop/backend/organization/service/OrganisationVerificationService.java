package com.foodloop.backend.organization.service;

import com.foodloop.backend.common.exception.ResourceNotFoundException;
import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.organization.repository.OrganisationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service providing authoritative database verification checks for organisations.
 * 
 * Security Notice:
 * JWT tokens contain an 'organizationVerificationStatus' claim for informational/UI purposes,
 * but that claim MUST NOT be treated as authoritative for operational security. Any sensitive
 * business flow (such as food donation publishing, pickup requests, or claiming surplus food)
 * MUST perform live verification checks against the database via this service.
 * 
 * PENDING, REJECTED, and SUSPENDED organisations are strictly prohibited from participating.
 */
@Service
public class OrganisationVerificationService {

    private final OrganisationRepository organisationRepository;

    public OrganisationVerificationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    /**
     * Retrieves the authoritative verification status directly from the database.
     *
     * @param organisationId Unique ID of the organisation
     * @return current live OrganisationVerificationStatus from PostgreSQL
     */
    @Transactional(readOnly = true)
    public OrganisationVerificationStatus getAuthoritativeStatus(UUID organisationId) {
        return getOrganisation(organisationId).getVerificationStatus();
    }

    /**
     * Checks if the organisation is currently verified in the database.
     *
     * @param organisationId Unique ID of the organisation
     * @return true if VERIFIED, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isVerified(UUID organisationId) {
        return getAuthoritativeStatus(organisationId) == OrganisationVerificationStatus.VERIFIED;
    }

    /**
     * Validates that an organisation has VERIFIED status and is permitted to participate
     * in operational activities (e.g. food donations, pickup collections, claim requests).
     * 
     * PENDING, REJECTED, and SUSPENDED organisations cannot participate in operations
     * and will be rejected with an AccessDeniedException.
     *
     * @param organisationId Unique ID of the organisation
     * @throws AccessDeniedException if the organisation is not VERIFIED
     * @throws ResourceNotFoundException if the organisation does not exist
     */
    @Transactional(readOnly = true)
    public void validateOrganisationCanParticipate(UUID organisationId) {
        Organisation organisation = getOrganisation(organisationId);
        OrganisationVerificationStatus status = organisation.getVerificationStatus();

        if (status == null || !status.canParticipateInOperations()) {
            throw new AccessDeniedException(
                    "Organisation is not verified to participate in operations. Current status: " + status +
                    ". Only VERIFIED organisations can create donations or perform pickups."
            );
        }
    }

    private Organisation getOrganisation(UUID organisationId) {
        if (organisationId == null) {
            throw new IllegalArgumentException("Organisation ID cannot be null.");
        }
        return organisationRepository.findById(organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", "id", organisationId));
    }
}
