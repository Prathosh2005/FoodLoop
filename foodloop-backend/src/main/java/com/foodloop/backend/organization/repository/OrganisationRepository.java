package com.foodloop.backend.organization.repository;

import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data access repository for Organisation entities.
 */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {

    /**
     * Retrieve all organizations of a given type (PROVIDER or NGO).
     *
     * @param type OrganisationType
     * @return List of matching organizations
     */
    List<Organisation> findByType(OrganisationType type);

    /**
     * Retrieve all organizations with a given verification status.
     *
     * @param verificationStatus OrganisationVerificationStatus
     * @return List of matching organizations
     */
    List<Organisation> findByVerificationStatus(OrganisationVerificationStatus verificationStatus);

    /**
     * Retrieve organizations filtered by both type and verification status.
     *
     * @param type OrganisationType
     * @param verificationStatus OrganisationVerificationStatus
     * @return List of matching organizations
     */
    List<Organisation> findByTypeAndVerificationStatus(
            OrganisationType type,
            OrganisationVerificationStatus verificationStatus
    );

    /**
     * Check if an organization with the given official registration number exists.
     *
     * @param registrationNumber Official registration number
     * @return true if already registered, false otherwise
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Find an organization by its unique registration number.
     *
     * @param registrationNumber Official registration number
     * @return Optional containing the Organisation if found
     */
    Optional<Organisation> findByRegistrationNumber(String registrationNumber);

    /**
     * Check if an organization with the given email address exists.
     *
     * @param email Official contact email
     * @return true if already registered, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find an organization by its official contact email.
     *
     * @param email Official contact email
     * @return Optional containing the Organisation if found
     */
    Optional<Organisation> findByEmail(String email);
}
