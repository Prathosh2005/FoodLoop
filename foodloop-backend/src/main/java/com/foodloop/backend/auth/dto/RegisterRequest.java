package com.foodloop.backend.auth.dto;

import com.foodloop.backend.organization.entity.OrganisationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for organization and user registration.
 */
public class RegisterRequest {

    // First administrator user details
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "User email cannot exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Size(max = 50, message = "Phone number cannot exceed 50 characters")
    private String phoneNumber;

    // Organisation details
    @NotBlank(message = "Organisation name is required")
    @Size(min = 2, max = 255, message = "Organisation name must be between 2 and 255 characters")
    private String organisationName;

    @NotNull(message = "Organisation type is required")
    private OrganisationType organisationType;

    @NotBlank(message = "Organisation email is required")
    @Email(message = "Invalid organisation email format")
    @Size(max = 255, message = "Organisation email cannot exceed 255 characters")
    private String organisationEmail;

    @Size(max = 50, message = "Organisation phone cannot exceed 50 characters")
    private String organisationPhone;

    @NotBlank(message = "Registration number is required")
    @Size(max = 100, message = "Registration number cannot exceed 100 characters")
    private String registrationNumber;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    public RegisterRequest() {
    }

    public RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            String phoneNumber,
            String organisationName,
            OrganisationType organisationType,
            String organisationEmail,
            String organisationPhone,
            String registrationNumber,
            String address,
            String description
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.organisationName = organisationName;
        this.organisationType = organisationType;
        this.organisationEmail = organisationEmail;
        this.organisationPhone = organisationPhone;
        this.registrationNumber = registrationNumber;
        this.address = address;
        this.description = description;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public String getOrganisationEmail() {
        return organisationEmail;
    }

    public void setOrganisationEmail(String organisationEmail) {
        this.organisationEmail = organisationEmail;
    }

    public String getOrganisationPhone() {
        return organisationPhone;
    }

    public void setOrganisationPhone(String organisationPhone) {
        this.organisationPhone = organisationPhone;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
