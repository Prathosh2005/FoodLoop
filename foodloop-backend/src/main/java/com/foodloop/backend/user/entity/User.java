package com.foodloop.backend.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodloop.backend.common.base.BaseEntity;
import com.foodloop.backend.organization.entity.Organisation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity representing an individual user in FoodLoop.
 * A user can be a PLATFORM_ADMIN (no organisation association) or an
 * ORGANIZATION_ADMIN / ORGANIZATION_MEMBER associated with a specific Organisation.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
public class User extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank(message = "Password hash is required")
    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Size(max = 50, message = "Phone number cannot exceed 50 characters")
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @NotNull(message = "User role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;

    @NotNull(message = "User status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private UserStatus status;

    /**
     * Unidirectional ManyToOne association to Organisation.
     * Optional (nullable = true) for PLATFORM_ADMIN users.
     * No cascade delete is applied to protect user accounts when organizations are modified.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "fk_users_organization")
    )
    private Organisation organization;

    protected User() {
        super();
        this.status = UserStatus.ACTIVE;
    }

    public User(
            String firstName,
            String lastName,
            String email,
            String passwordHash,
            String phoneNumber,
            UserRole role,
            UserStatus status,
            Organisation organization
    ) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.organization = organization;
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
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public Organisation getOrganization() {
        return organization;
    }

    public void setOrganization(Organisation organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", organizationId=" + (organization != null ? organization.getId() : "none") +
                '}';
    }
}
