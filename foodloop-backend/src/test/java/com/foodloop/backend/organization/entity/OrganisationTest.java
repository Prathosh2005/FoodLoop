package com.foodloop.backend.organization.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationTest {

    @Test
    @DisplayName("Should create Organisation with default PENDING verification status")
    void shouldCreateOrganisationWithDefaults() {
        Organisation org = new Organisation();
        org.setName("Grand Hotel & Kitchens");
        org.setType(OrganisationType.PROVIDER);
        org.setDescription("Luxury hotel donating surplus catering food");
        org.setEmail("kitchen@grandhotel.com");
        org.setPhoneNumber("+918023456789");
        org.setRegistrationNumber("HOTEL-REG-2026-99");
        org.setAddress("42 Ocean Boulevard, Central District");

        assertEquals("Grand Hotel & Kitchens", org.getName());
        assertEquals(OrganisationType.PROVIDER, org.getType());
        assertEquals("kitchen@grandhotel.com", org.getEmail());
        assertEquals("HOTEL-REG-2026-99", org.getRegistrationNumber());
        assertEquals(OrganisationVerificationStatus.PENDING, org.getVerificationStatus());
    }

    @Test
    @DisplayName("Should update verification status through workflow lifecycle")
    void shouldUpdateVerificationStatus() {
        Organisation org = new Organisation(
                "City Food Pantry",
                OrganisationType.NGO,
                "Neighborhood pantry",
                "info@cityfoodpantry.org",
                "+919988776655",
                "PANTRY-REG-101",
                "10 Main Street",
                OrganisationVerificationStatus.PENDING
        );

        assertEquals(OrganisationVerificationStatus.PENDING, org.getVerificationStatus());

        org.setVerificationStatus(OrganisationVerificationStatus.VERIFIED);
        assertEquals(OrganisationVerificationStatus.VERIFIED, org.getVerificationStatus());

        org.setVerificationStatus(OrganisationVerificationStatus.SUSPENDED);
        assertEquals(OrganisationVerificationStatus.SUSPENDED, org.getVerificationStatus());
    }

    @Test
    @DisplayName("Should verify entity equality based on BaseEntity ID contract")
    void shouldVerifyEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Organisation o1 = new Organisation();
        o1.setId(id);
        Organisation o2 = new Organisation();
        o2.setId(id);

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
    }
}
