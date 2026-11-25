package com.foodloop.backend.user.entity;

import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should create User with valid fields and default ACTIVE status")
    void shouldCreateUserWithDefaults() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@foodloop.org");
        user.setPasswordHash("$2a$12$e8Y7c6u.4g7z/testHashValue");
        user.setPhoneNumber("+919876543210");
        user.setRole(UserRole.PLATFORM_ADMIN);

        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Jane Doe", user.getFullName());
        assertEquals("jane.doe@foodloop.org", user.getEmail());
        assertEquals("$2a$12$e8Y7c6u.4g7z/testHashValue", user.getPasswordHash());
        assertEquals("+919876543210", user.getPhoneNumber());
        assertEquals(UserRole.PLATFORM_ADMIN, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertNull(user.getOrganization(), "PLATFORM_ADMIN should not belong to an organization");
    }

    @Test
    @DisplayName("Should associate User with Organisation for organization members")
    void shouldAssociateUserWithOrganisation() {
        Organisation org = new Organisation(
                "Helping Hands NGO",
                OrganisationType.NGO,
                "Food rescue NGO",
                "contact@helpinghands.org",
                "+911122334455",
                "NGO-REG-2026-001",
                "123 Charity Way, Metro City",
                OrganisationVerificationStatus.VERIFIED
        );
        org.setId(UUID.randomUUID());

        User orgAdmin = new User(
                "Alice",
                "Smith",
                "alice@helpinghands.org",
                "$2a$12$someSecureHash",
                "+919876543211",
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                org
        );

        User orgMember = new User(
                "Bob",
                "Jones",
                "bob@helpinghands.org",
                "$2a$12$anotherSecureHash",
                "+919876543212",
                UserRole.ORGANIZATION_MEMBER,
                UserStatus.ACTIVE,
                org
        );

        assertNotNull(orgAdmin.getOrganization());
        assertEquals(org.getId(), orgAdmin.getOrganization().getId());
        assertEquals("Helping Hands NGO", orgAdmin.getOrganization().getName());

        assertNotNull(orgMember.getOrganization());
        assertEquals(org.getId(), orgMember.getOrganization().getId());

        // Verify multiple users can point to the same organisation
        assertSame(orgAdmin.getOrganization(), orgMember.getOrganization());
    }

    @Test
    @DisplayName("Should verify equals and hashCode based on BaseEntity ID contract")
    void shouldVerifyEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        User u1 = new User();
        u1.setId(id);
        User u2 = new User();
        u2.setId(id);

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }
}
