package com.foodloop.backend.organization.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing organization details in API responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganisationResponse {

    private UUID id;
    private String name;
    private OrganisationType type;
    private String description;
    private String email;
    private String phoneNumber;
    private String registrationNumber;
    private String address;
    private OrganisationVerificationStatus verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrganisationResponse() {
    }

    public OrganisationResponse(
            UUID id,
            String name,
            OrganisationType type,
            String description,
            String email,
            String phoneNumber,
            String registrationNumber,
            String address,
            OrganisationVerificationStatus verificationStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registrationNumber = registrationNumber;
        this.address = address;
        this.verificationStatus = verificationStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Map Organisation entity to OrganisationResponse DTO.
     *
     * @param organisation Source Organisation entity
     * @return OrganisationResponse DTO
     */
    public static OrganisationResponse fromEntity(Organisation organisation) {
        if (organisation == null) {
            return null;
        }

        return new OrganisationResponse(
                organisation.getId(),
                organisation.getName(),
                organisation.getType(),
                organisation.getDescription(),
                organisation.getEmail(),
                organisation.getPhoneNumber(),
                organisation.getRegistrationNumber(),
                organisation.getAddress(),
                organisation.getVerificationStatus(),
                organisation.getCreatedAt(),
                organisation.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
