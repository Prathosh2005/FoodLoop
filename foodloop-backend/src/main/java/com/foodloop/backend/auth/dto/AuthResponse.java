package com.foodloop.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foodloop.backend.organization.dto.OrganisationResponse;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.user.dto.UserResponse;

/**
 * Data Transfer Object returned upon successful authentication.
 * Exposes JWT access token and user/organization domain representations.
 * Never exposes passwords or internal hashes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserResponse user;
    private OrganisationResponse organisation;
    private OrganisationVerificationStatus organisationVerificationStatus;

    public AuthResponse() {
    }

    public AuthResponse(
            String token,
            String tokenType,
            long expiresIn,
            UserResponse user,
            OrganisationResponse organisation
    ) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
        this.organisation = organisation;
        if (organisation != null) {
            this.organisationVerificationStatus = organisation.getVerificationStatus();
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public OrganisationResponse getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationResponse organisation) {
        this.organisation = organisation;
        if (organisation != null) {
            this.organisationVerificationStatus = organisation.getVerificationStatus();
        }
    }

    public OrganisationVerificationStatus getOrganisationVerificationStatus() {
        return organisationVerificationStatus;
    }

    public void setOrganisationVerificationStatus(OrganisationVerificationStatus organisationVerificationStatus) {
        this.organisationVerificationStatus = organisationVerificationStatus;
    }
}
