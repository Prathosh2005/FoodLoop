package com.foodloop.backend.auth.dto;

import com.foodloop.backend.organization.dto.OrganisationResponse;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.user.dto.UserResponse;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthDtoSecurityTest {

    @Test
    @DisplayName("Verify that AuthResponse and UserRegistrationResponse do not have any password or hash fields")
    void verifyNoPasswordHashInResponses() {
        // Assert AuthResponse fields
        for (Field field : AuthResponse.class.getDeclaredFields()) {
            assertNotEquals("password", field.getName().toLowerCase());
            assertNotEquals("passwordhash", field.getName().toLowerCase());
        }

        // Assert UserRegistrationResponse fields
        for (Field field : UserRegistrationResponse.class.getDeclaredFields()) {
            assertNotEquals("password", field.getName().toLowerCase());
            assertNotEquals("passwordhash", field.getName().toLowerCase());
        }
    }

    @Test
    @DisplayName("Verify AuthResponse correctly extracts organisation verification status")
    void verifyAuthResponseVerificationStatus() {
        UUID orgId = UUID.randomUUID();
        OrganisationResponse org = new OrganisationResponse(
                orgId, "Helping Hands", OrganisationType.NGO, "Rescue food",
                "ngo@help.org", "1234567890", "REG-123", "Main St",
                OrganisationVerificationStatus.VERIFIED, null, null
        );

        UserResponse user = new UserResponse(
                UUID.randomUUID(), "Alice", "Smith", "Alice Smith", "alice@help.org",
                "1234567890", UserRole.ORGANIZATION_ADMIN, UserStatus.ACTIVE,
                orgId, "Helping Hands", null, null
        );

        AuthResponse authResponse = new AuthResponse("token123", "Bearer", 86400000L, user, org);

        assertEquals("token123", authResponse.getToken());
        assertEquals("Bearer", authResponse.getTokenType());
        assertEquals(86400000L, authResponse.getExpiresIn());
        assertEquals(OrganisationVerificationStatus.VERIFIED, authResponse.getOrganisationVerificationStatus());
    }
}
