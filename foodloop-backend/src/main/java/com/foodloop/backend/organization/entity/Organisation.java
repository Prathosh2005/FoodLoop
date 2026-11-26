package com.foodloop.backend.organization.entity;

import com.foodloop.backend.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity representing an organization on FoodLoop (either a food PROVIDER or an NGO).
 * An organization can have multiple registered user accounts.
 */
@Entity
@Table(
        name = "organisations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_organisations_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_organisations_reg_num", columnNames = "registration_number")
        }
)
public class Organisation extends BaseEntity {

    @NotBlank(message = "Organisation name is required")
    @Size(min = 2, max = 255, message = "Organisation name must be between 2 and 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Organisation type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private OrganisationType type;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @NotBlank(message = "Official email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Size(max = 50, message = "Phone number cannot exceed 50 characters")
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @NotBlank(message = "Registration number is required")
    @Size(max = 100, message = "Registration number cannot exceed 100 characters")
    @Column(name = "registration_number", nullable = false, length = 100)
    private String registrationNumber;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    @NotNull(message = "Verification status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 50)
    private OrganisationVerificationStatus verificationStatus;

    protected Organisation() {
        super();
        this.verificationStatus = OrganisationVerificationStatus.PENDING;
    }

    public Organisation(
            String name,
            OrganisationType type,
            String description,
            String email,
            String phoneNumber,
            String registrationNumber,
            String address,
            OrganisationVerificationStatus verificationStatus
    ) {
        super();
        this.name = name;
        this.type = type;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registrationNumber = registrationNumber;
        this.address = address;
        this.verificationStatus = verificationStatus != null ? verificationStatus : OrganisationVerificationStatus.PENDING;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganisationType getType() {
        return type;
    }

    public void setType(OrganisationType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public OrganisationVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(OrganisationVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public boolean isVerified() {
        return this.verificationStatus == OrganisationVerificationStatus.VERIFIED;
    }

    public boolean canParticipateInOperations() {
        return this.verificationStatus != null && this.verificationStatus.canParticipateInOperations();
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", email='" + email + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", verificationStatus=" + verificationStatus +
                '}';
    }
}
