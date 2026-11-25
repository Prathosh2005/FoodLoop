package com.foodloop.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foodloop.backend.user.entity.User;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing user information in API responses.
 * Under no circumstances is passwordHash exposed to clients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
    private UUID organizationId;
    private String organizationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse() {
    }

    public UserResponse(
            UUID id,
            String firstName,
            String lastName,
            String fullName,
            String email,
            String phoneNumber,
            UserRole role,
            UserStatus status,
            UUID organizationId,
            String organizationName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = status;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Map User entity to UserResponse DTO.
     *
     * @param user Source User entity
     * @return UserResponse without sensitive security credentials
     */
    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UUID orgId = null;
        String orgName = null;
        if (user.getOrganization() != null) {
            orgId = user.getOrganization().getId();
            orgName = user.getOrganization().getName();
        }

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                orgId,
                orgName,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
