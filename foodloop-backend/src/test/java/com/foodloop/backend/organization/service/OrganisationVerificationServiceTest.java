package com.foodloop.backend.organization.service;

import com.foodloop.backend.common.exception.ResourceNotFoundException;
import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.organization.repository.OrganisationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganisationVerificationServiceTest {

    @Mock
    private OrganisationRepository organisationRepository;

    private OrganisationVerificationService verificationService;

    @BeforeEach
    void setUp() {
        verificationService = new OrganisationVerificationService(organisationRepository);
    }

    private Organisation createOrganisationWithStatus(UUID id, OrganisationVerificationStatus status) {
        Organisation org = new Organisation(
                "Community Kitchen",
                OrganisationType.PROVIDER,
                "Providing surplus meals",
                "kitchen@community.org",
                "555-1234",
                "REG-CK-99",
                "123 Market St",
                status
        );
        ReflectionTestUtils.setField(org, "id", id);
        return org;
    }

    @Test
    @DisplayName("VERIFIED organisation should successfully pass participation validation")
    void verifiedOrganisationCanParticipate() {
        UUID orgId = UUID.randomUUID();
        Organisation org = createOrganisationWithStatus(orgId, OrganisationVerificationStatus.VERIFIED);
        when(organisationRepository.findById(orgId)).thenReturn(Optional.of(org));

        assertDoesNotThrow(() -> verificationService.validateOrganisationCanParticipate(orgId));
        assertTrue(verificationService.isVerified(orgId));
        assertEquals(OrganisationVerificationStatus.VERIFIED, verificationService.getAuthoritativeStatus(orgId));
    }

    @Test
    @DisplayName("PENDING organisation must be rejected from participating in operations")
    void pendingOrganisationCannotParticipate() {
        UUID orgId = UUID.randomUUID();
        Organisation org = createOrganisationWithStatus(orgId, OrganisationVerificationStatus.PENDING);
        when(organisationRepository.findById(orgId)).thenReturn(Optional.of(org));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                verificationService.validateOrganisationCanParticipate(orgId)
        );
        assertTrue(ex.getMessage().contains("PENDING"));
        assertFalse(verificationService.isVerified(orgId));
    }

    @Test
    @DisplayName("REJECTED organisation must be rejected from participating in operations")
    void rejectedOrganisationCannotParticipate() {
        UUID orgId = UUID.randomUUID();
        Organisation org = createOrganisationWithStatus(orgId, OrganisationVerificationStatus.REJECTED);
        when(organisationRepository.findById(orgId)).thenReturn(Optional.of(org));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                verificationService.validateOrganisationCanParticipate(orgId)
        );
        assertTrue(ex.getMessage().contains("REJECTED"));
        assertFalse(verificationService.isVerified(orgId));
    }

    @Test
    @DisplayName("SUSPENDED organisation must be rejected from participating in operations")
    void suspendedOrganisationCannotParticipate() {
        UUID orgId = UUID.randomUUID();
        Organisation org = createOrganisationWithStatus(orgId, OrganisationVerificationStatus.SUSPENDED);
        when(organisationRepository.findById(orgId)).thenReturn(Optional.of(org));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                verificationService.validateOrganisationCanParticipate(orgId)
        );
        assertTrue(ex.getMessage().contains("SUSPENDED"));
        assertFalse(verificationService.isVerified(orgId));
    }

    @Test
    @DisplayName("Non-existent organisation should throw ResourceNotFoundException")
    void nonExistentOrganisationThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(organisationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                verificationService.validateOrganisationCanParticipate(nonExistentId)
        );
    }
}
