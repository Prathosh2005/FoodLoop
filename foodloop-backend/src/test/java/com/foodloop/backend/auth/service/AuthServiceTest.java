package com.foodloop.backend.auth.service;

import com.foodloop.backend.auth.dto.AuthResponse;
import com.foodloop.backend.auth.dto.LoginRequest;
import com.foodloop.backend.auth.dto.RegisterRequest;
import com.foodloop.backend.auth.dto.UserRegistrationResponse;
import com.foodloop.backend.auth.security.JwtService;
import com.foodloop.backend.common.exception.DuplicateResourceException;
import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.organization.repository.OrganisationRepository;
import com.foodloop.backend.user.entity.User;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import com.foodloop.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, organisationRepository, passwordEncoder, jwtService);
    }

    private RegisterRequest createSampleRegisterRequest(OrganisationType orgType) {
        return new RegisterRequest(
                "Jane",
                "Doe",
                "jane@restaurant.com",
                "SecurePassword123!",
                "123-456-7890",
                "Green Table Restaurant",
                orgType,
                "info@greentable.com",
                "123-456-7890",
                "REG-987654",
                "100 Broadway, NY",
                "Eco-friendly dining"
        );
    }

    @Test
    @DisplayName("Should successfully register a new organisation and first user")
    void shouldRegisterSuccessfully() {
        RegisterRequest request = createSampleRegisterRequest(OrganisationType.PROVIDER);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(organisationRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(false);
        when(organisationRepository.existsByEmail(request.getOrganisationEmail())).thenReturn(false);
        when(passwordEncoder.encode("SecurePassword123!")).thenReturn("$2a$10$hashedPassword");

        when(organisationRepository.save(any(Organisation.class))).thenAnswer(invocation -> {
            Organisation org = invocation.getArgument(0);
            ReflectionTestUtils.setField(org, "id", UUID.randomUUID());
            return org;
        });

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
            return user;
        });

        UserRegistrationResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNotNull(response.getOrganisation());
        assertEquals("Jane", response.getUser().getFirstName());
        assertEquals("Doe", response.getUser().getLastName());
        assertEquals("jane@restaurant.com", response.getUser().getEmail());
        assertEquals(UserRole.ORGANIZATION_ADMIN, response.getUser().getRole());
        assertEquals(UserStatus.ACTIVE, response.getUser().getStatus());

        assertEquals("Green Table Restaurant", response.getOrganisation().getName());
        assertEquals(OrganisationType.PROVIDER, response.getOrganisation().getType());
        assertEquals(OrganisationVerificationStatus.PENDING, response.getOrganisation().getVerificationStatus());

        verify(organisationRepository).save(argThat(org ->
                org.getVerificationStatus() == OrganisationVerificationStatus.PENDING &&
                        org.getName().equals("Green Table Restaurant")
        ));

        verify(userRepository).save(argThat(u ->
                u.getRole() == UserRole.ORGANIZATION_ADMIN &&
                        u.getStatus() == UserStatus.ACTIVE &&
                        u.getPasswordHash().equals("$2a$10$hashedPassword")
        ));
    }

    @Test
    @DisplayName("Should reject registration when user email is already registered")
    void shouldRejectDuplicateUserEmail() {
        RegisterRequest request = createSampleRegisterRequest(OrganisationType.NGO);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(organisationRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject registration when organisation registration number already exists")
    void shouldRejectDuplicateRegistrationNumber() {
        RegisterRequest request = createSampleRegisterRequest(OrganisationType.PROVIDER);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(organisationRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(organisationRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject registration when organisation email already exists")
    void shouldRejectDuplicateOrganisationEmail() {
        RegisterRequest request = createSampleRegisterRequest(OrganisationType.PROVIDER);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(organisationRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(false);
        when(organisationRepository.existsByEmail(request.getOrganisationEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(organisationRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully with valid credentials and return JWT AuthResponse")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("user@example.com", "PlainPassword123");

        Organisation org = new Organisation(
                "Food Help",
                OrganisationType.NGO,
                "Rescue food",
                "contact@foodhelp.org",
                "1112223333",
                "REG-NGO-1",
                "City Center",
                OrganisationVerificationStatus.VERIFIED
        );
        ReflectionTestUtils.setField(org, "id", UUID.randomUUID());

        User user = new User(
                "Sam",
                "Smith",
                "user@example.com",
                "$2a$10$hashedPassword",
                "1112223333",
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                org
        );
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("PlainPassword123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(86400000L, response.getExpiresIn());
        assertEquals("user@example.com", response.getUser().getEmail());
        assertEquals(OrganisationVerificationStatus.VERIFIED, response.getOrganisationVerificationStatus());
    }

    @Test
    @DisplayName("Should reject login when email is not found")
    void shouldRejectLoginWhenUserNotFound() {
        LoginRequest request = new LoginRequest("unknown@example.com", "somePassword");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should reject login when password does not match")
    void shouldRejectLoginWhenPasswordWrong() {
        LoginRequest request = new LoginRequest("user@example.com", "WrongPassword");
        User user = new User(
                "Sam",
                "Smith",
                "user@example.com",
                "$2a$10$hashedPassword",
                null,
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                null
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "$2a$10$hashedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should reject login when user account is LOCKED")
    void shouldRejectLoginWhenAccountLocked() {
        LoginRequest request = new LoginRequest("user@example.com", "CorrectPassword");
        User user = new User(
                "Sam",
                "Smith",
                "user@example.com",
                "$2a$10$hashedPassword",
                null,
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.LOCKED,
                null
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("CorrectPassword", "$2a$10$hashedPassword")).thenReturn(true);

        assertThrows(LockedException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should reject login when user account is INACTIVE")
    void shouldRejectLoginWhenAccountInactive() {
        LoginRequest request = new LoginRequest("user@example.com", "CorrectPassword");
        User user = new User(
                "Sam",
                "Smith",
                "user@example.com",
                "$2a$10$hashedPassword",
                null,
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.INACTIVE,
                null
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("CorrectPassword", "$2a$10$hashedPassword")).thenReturn(true);

        assertThrows(DisabledException.class, () -> authService.login(request));
    }
}
