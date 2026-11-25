package com.foodloop.backend.user.repository;

import com.foodloop.backend.user.entity.User;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data access repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Retrieve a user by their unique email address.
     *
     * @param email User email
     * @return Optional containing the User if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email address already exists.
     *
     * @param email Email to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieve all users affiliated with a specific organization.
     *
     * @param organizationId ID of the organization
     * @return List of affiliated users
     */
    List<User> findByOrganizationId(UUID organizationId);

    /**
     * Retrieve all users with a specific role.
     *
     * @param role User role (PLATFORM_ADMIN, ORGANIZATION_ADMIN, ORGANIZATION_MEMBER)
     * @return List of matching users
     */
    List<User> findByRole(UserRole role);

    /**
     * Retrieve all users with a specific account status.
     *
     * @param status Account status (ACTIVE, INACTIVE, LOCKED)
     * @return List of matching users
     */
    List<User> findByStatus(UserStatus status);
}
