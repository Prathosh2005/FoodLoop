package com.foodloop.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foodloop.backend.organization.dto.OrganisationResponse;
import com.foodloop.backend.user.dto.UserResponse;

/**
 * Data Transfer Object returned after registering a new organisation and initial administrator.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegistrationResponse {

    private UserResponse user;
    private OrganisationResponse organisation;
    private String message;

    public UserRegistrationResponse() {
    }

    public UserRegistrationResponse(UserResponse user, OrganisationResponse organisation, String message) {
        this.user = user;
        this.organisation = organisation;
        this.message = message;
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
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
