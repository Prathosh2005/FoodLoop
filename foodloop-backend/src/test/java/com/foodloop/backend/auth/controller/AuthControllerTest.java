package com.foodloop.backend.auth.controller;

import com.foodloop.backend.auth.dto.AuthResponse;
import com.foodloop.backend.auth.dto.LoginRequest;
import com.foodloop.backend.auth.dto.RegisterRequest;
import com.foodloop.backend.auth.dto.UserRegistrationResponse;
import com.foodloop.backend.auth.security.CustomUserDetailsService;
import com.foodloop.backend.auth.security.JwtAuthenticationFilter;
import com.foodloop.backend.auth.security.JwtService;
import com.foodloop.backend.auth.service.AuthService;
import com.foodloop.backend.common.exception.DuplicateResourceException;
import com.foodloop.backend.config.SecurityConfig;
import com.foodloop.backend.organization.dto.OrganisationResponse;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.user.dto.UserResponse;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private static final String VALID_REGISTER_JSON = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "email": "john@provider.com",
                "password": "Password123!",
                "phoneNumber": "1234567890",
                "organisationName": "Gourmet Deli",
                "organisationType": "PROVIDER",
                "organisationEmail": "info@gourmetdeli.com",
                "organisationPhone": "1234567890",
                "registrationNumber": "DELI-1001",
                "address": "123 Main Street",
                "description": "Artisan deli and bakery"
            }
            """;

    @Test
    @DisplayName("POST /api/auth/register should return 201 Created on valid registration")
    void shouldRegisterSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        UserResponse user = new UserResponse(
                userId, "John", "Doe", "John Doe", "john@provider.com", "1234567890",
                UserRole.ORGANIZATION_ADMIN, UserStatus.ACTIVE, orgId, "Gourmet Deli", null, null
        );

        OrganisationResponse org = new OrganisationResponse(
                orgId, "Gourmet Deli", OrganisationType.PROVIDER, "Artisan deli and bakery",
                "info@gourmetdeli.com", "1234567890", "DELI-1001", "123 Main Street",
                OrganisationVerificationStatus.PENDING, null, null
        );

        UserRegistrationResponse registrationResponse = new UserRegistrationResponse(
                user, org, "Registration successful. Organisation verification is currently pending review."
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(registrationResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REGISTER_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.user.email").value("john@provider.com"))
                .andExpect(jsonPath("$.data.user.role").value("ORGANIZATION_ADMIN"))
                .andExpect(jsonPath("$.data.organisation.name").value("Gourmet Deli"))
                .andExpect(jsonPath("$.data.organisation.verificationStatus").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 400 Bad Request on validation failure")
    void shouldFailValidationOnMissingRequiredFields() throws Exception {
        String invalidJson = """
                {
                    "firstName": "",
                    "lastName": "Doe",
                    "email": "not-an-email",
                    "password": "short"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 409 Conflict when duplicate resource exists")
    void shouldReturnConflictOnDuplicate() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("A user with this email address already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REGISTER_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("A user with this email address already exists"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 200 OK with JWT token on valid credentials")
    void shouldLoginSuccessfully() throws Exception {
        String loginJson = """
                {
                    "email": "john@provider.com",
                    "password": "Password123!"
                }
                """;

        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        UserResponse user = new UserResponse(
                userId, "John", "Doe", "John Doe", "john@provider.com", "1234567890",
                UserRole.ORGANIZATION_ADMIN, UserStatus.ACTIVE, orgId, "Gourmet Deli", null, null
        );

        OrganisationResponse org = new OrganisationResponse(
                orgId, "Gourmet Deli", OrganisationType.PROVIDER, "Artisan deli and bakery",
                "info@gourmetdeli.com", "1234567890", "DELI-1001", "123 Main Street",
                OrganisationVerificationStatus.PENDING, null, null
        );

        AuthResponse authResponse = new AuthResponse("mock.jwt.token", "Bearer", 86400000L, user, org);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.email").value("john@provider.com"))
                .andExpect(jsonPath("$.data.organisationVerificationStatus").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 401 Unauthorized on invalid credentials")
    void shouldReturnUnauthorizedOnBadCredentials() throws Exception {
        String loginJson = """
                {
                    "email": "john@provider.com",
                    "password": "WrongPassword!"
                }
                """;

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 403 Forbidden when account is locked")
    void shouldReturnForbiddenWhenAccountLocked() throws Exception {
        String loginJson = """
                {
                    "email": "locked@provider.com",
                    "password": "Password123!"
                }
                """;

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new LockedException("User account is locked. Please contact support."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User account is locked. Please contact support."));
    }
}
