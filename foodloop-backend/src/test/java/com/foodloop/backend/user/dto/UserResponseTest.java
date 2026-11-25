package com.foodloop.backend.user.dto;

import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.user.entity.User;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    @DisplayName("Should map from User entity without exposing passwordHash")
    void shouldMapFromEntityWithoutPasswordHash() {
        Organisation org = new Organisation(
                "Fresh Bakery Co.",
                OrganisationType.PROVIDER,
                "Bakery donating leftover bread",
                "bakery@fresh.com",
                "+919876543210",
                "BAKE-2026-001",
                "7 Baker Street",
                OrganisationVerificationStatus.VERIFIED
        );
        UUID orgId = UUID.randomUUID();
        org.setId(orgId);

        User user = new User(
                "John",
                "Baker",
                "john@fresh.com",
                "$2a$12$SUPER_SECRET_HASHED_PASSWORD_NEVER_EXPOSED",
                "+919876543211",
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                org
        );
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        UserResponse response = UserResponse.fromEntity(user);

        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Baker", response.getLastName());
        assertEquals("John Baker", response.getFullName());
        assertEquals("john@fresh.com", response.getEmail());
        assertEquals("+919876543211", response.getPhoneNumber());
        assertEquals(UserRole.ORGANIZATION_ADMIN, response.getRole());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
        assertEquals(orgId, response.getOrganizationId());
        assertEquals("Fresh Bakery Co.", response.getOrganizationName());

        // Strictly verify that UserResponse has NO password or passwordHash field in its class definition
        boolean hasPasswordField = Arrays.stream(UserResponse.class.getDeclaredFields())
                .map(Field::getName)
                .anyMatch(name -> name.toLowerCase().contains("password"));
        assertFalse(hasPasswordField, "UserResponse must never contain any password field");
    }

    @Test
    @DisplayName("Should correctly handle PLATFORM_ADMIN with null organisation")
    void shouldHandlePlatformAdminWithNullOrganisation() {
        User admin = new User(
                "Root",
                "Admin",
                "admin@foodloop.org",
                "$2a$12$adminHash",
                null,
                UserRole.PLATFORM_ADMIN,
                UserStatus.ACTIVE,
                null
        );
        admin.setId(UUID.randomUUID());

        UserResponse response = UserResponse.fromEntity(admin);

        assertNotNull(response);
        assertEquals(UserRole.PLATFORM_ADMIN, response.getRole());
        assertNull(response.getOrganizationId());
        assertNull(response.getOrganizationName());
    }
}
