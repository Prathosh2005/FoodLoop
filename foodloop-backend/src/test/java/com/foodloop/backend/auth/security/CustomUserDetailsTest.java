package com.foodloop.backend.auth.security;

import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.user.entity.User;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    @DisplayName("Should adapt User entity with Organisation to CustomUserDetails correctly")
    void shouldAdaptUserWithOrganisation() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        Organisation org = new Organisation(
                "Helping Hands NGO",
                OrganisationType.NGO,
                "Food rescue NGO",
                "ngo@helpinghands.org",
                "9876543210",
                "NGO-55555",
                "456 Oak Avenue",
                OrganisationVerificationStatus.PENDING
        );
        ReflectionTestUtils.setField(org, "id", orgId);

        User user = new User(
                "Bob",
                "Helper",
                "bob@helpinghands.org",
                "$2a$10$encryptedPasswordHash",
                "9876543210",
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                org
        );
        ReflectionTestUtils.setField(user, "id", userId);

        CustomUserDetails details = CustomUserDetails.fromUser(user);

        assertEquals(userId, details.getId());
        assertEquals("bob@helpinghands.org", details.getUsername());
        assertEquals("bob@helpinghands.org", details.getEmail());
        assertEquals("$2a$10$encryptedPasswordHash", details.getPassword());
        assertEquals(UserRole.ORGANIZATION_ADMIN, details.getRole());
        assertEquals(UserStatus.ACTIVE, details.getStatus());
        assertEquals(orgId, details.getOrganizationId());
        assertEquals("Helping Hands NGO", details.getOrganizationName());
        assertEquals(OrganisationVerificationStatus.PENDING, details.getOrganizationVerificationStatus());

        assertTrue(details.isEnabled());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isCredentialsNonExpired());

        assertTrue(details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ORGANIZATION_ADMIN")));
    }

    @Test
    @DisplayName("Should adapt User with LOCKED status properly")
    void shouldReflectLockedStatus() {
        User user = new User(
                "Locked",
                "User",
                "locked@test.com",
                "pwdHash",
                null,
                UserRole.ORGANIZATION_MEMBER,
                UserStatus.LOCKED,
                null
        );

        CustomUserDetails details = CustomUserDetails.fromUser(user);
        assertFalse(details.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should adapt User with INACTIVE status properly")
    void shouldReflectInactiveStatus() {
        User user = new User(
                "Inactive",
                "User",
                "inactive@test.com",
                "pwdHash",
                null,
                UserRole.ORGANIZATION_MEMBER,
                UserStatus.INACTIVE,
                null
        );

        CustomUserDetails details = CustomUserDetails.fromUser(user);
        assertFalse(details.isEnabled());
    }
}
