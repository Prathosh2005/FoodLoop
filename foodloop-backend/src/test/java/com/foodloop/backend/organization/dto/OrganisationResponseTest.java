package com.foodloop.backend.organization.dto;

import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationResponseTest {

    @Test
    @DisplayName("Should map from Organisation entity to OrganisationResponse DTO")
    void shouldMapFromEntity() {
        UUID orgId = UUID.randomUUID();
        Organisation org = new Organisation(
                "Community Kitchen",
                OrganisationType.NGO,
                "Community food kitchen and outreach",
                "contact@communitykitchen.org",
                "+911144556677",
                "NGO-CK-2026",
                "50 Service Road, North City",
                OrganisationVerificationStatus.VERIFIED
        );
        org.setId(orgId);

        OrganisationResponse response = OrganisationResponse.fromEntity(org);

        assertNotNull(response);
        assertEquals(orgId, response.getId());
        assertEquals("Community Kitchen", response.getName());
        assertEquals(OrganisationType.NGO, response.getType());
        assertEquals("Community food kitchen and outreach", response.getDescription());
        assertEquals("contact@communitykitchen.org", response.getEmail());
        assertEquals("+911144556677", response.getPhoneNumber());
        assertEquals("NGO-CK-2026", response.getRegistrationNumber());
        assertEquals("50 Service Road, North City", response.getAddress());
        assertEquals(OrganisationVerificationStatus.VERIFIED, response.getVerificationStatus());
    }
}
