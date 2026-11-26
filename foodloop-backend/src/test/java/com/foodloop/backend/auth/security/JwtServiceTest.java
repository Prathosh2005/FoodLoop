package com.foodloop.backend.auth.security;

import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.user.entity.User;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secretKey, expirationMs);
    }

    @Test
    @DisplayName("Should generate token with claims and extract all details successfully")
    void shouldGenerateTokenAndExtractClaims() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        Organisation org = new Organisation(
                "Fresh Bakery",
                OrganisationType.PROVIDER,
                "Bakery shop",
                "bakery@example.com",
                "1234567890",
                "REG-12345",
                "123 Main St",
                OrganisationVerificationStatus.PENDING
        );
        ReflectionTestUtils.setField(org, "id", orgId);

        User user = new User(
                "Alice",
                "Baker",
                "alice@bakery.com",
                "encodedPassword",
                "1234567890",
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                org
        );
        ReflectionTestUtils.setField(user, "id", userId);

        CustomUserDetails userDetails = CustomUserDetails.fromUser(user);

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals("alice@bakery.com", jwtService.extractUsername(token));
        assertEquals(userId, jwtService.extractUserId(token));
        assertEquals("ORGANIZATION_ADMIN", jwtService.extractRole(token));
        assertEquals(orgId, jwtService.extractOrganizationId(token));
        assertFalse(jwtService.isTokenExpired(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Should handle token generation for platform admin without organisation")
    void shouldHandleTokenForUserWithoutOrganisation() {
        UUID userId = UUID.randomUUID();
        User admin = new User(
                "Super",
                "Admin",
                "admin@foodloop.org",
                "encodedPassword",
                "9876543210",
                UserRole.PLATFORM_ADMIN,
                UserStatus.ACTIVE,
                null
        );
        ReflectionTestUtils.setField(admin, "id", userId);

        CustomUserDetails userDetails = CustomUserDetails.fromUser(admin);
        String token = jwtService.generateToken(userDetails);

        assertEquals("admin@foodloop.org", jwtService.extractUsername(token));
        assertEquals(userId, jwtService.extractUserId(token));
        assertEquals("PLATFORM_ADMIN", jwtService.extractRole(token));
        assertNull(jwtService.extractOrganizationId(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Should report token invalid when validated against different username")
    void shouldBeInvalidForDifferentUser() {
        CustomUserDetails user1 = new CustomUserDetails(
                UUID.randomUUID(),
                "user1@example.com",
                "pwd",
                UserRole.ORGANIZATION_MEMBER,
                UserStatus.ACTIVE,
                null,
                null,
                null,
                Collections.emptyList()
        );

        CustomUserDetails user2 = new CustomUserDetails(
                UUID.randomUUID(),
                "user2@example.com",
                "pwd",
                UserRole.ORGANIZATION_MEMBER,
                UserStatus.ACTIVE,
                null,
                null,
                null,
                Collections.emptyList()
        );

        String token = jwtService.generateToken(user1);
        assertFalse(jwtService.isTokenValid(token, user2));
    }

    @Test
    @DisplayName("Should detect expired token")
    void shouldDetectExpiredToken() {
        // Build expired token with negative expiration time
        String expiredToken = jwtService.buildToken(new HashMap<>(), "expired@test.com", -10000);
        assertTrue(jwtService.isTokenExpired(expiredToken));
    }
}
