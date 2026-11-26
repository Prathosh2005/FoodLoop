package com.foodloop.backend.auth.security;

import com.foodloop.backend.HealthController;
import com.foodloop.backend.auth.controller.AuthController;
import com.foodloop.backend.auth.service.AuthService;
import com.foodloop.backend.config.SecurityConfig;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {HealthController.class, AuthController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // --- Public endpoint tests (Strictly POST register, POST login, GET health) ---

    @Test
    @DisplayName("GET /api/health should be public (200 OK)")
    void getHealthShouldBePublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/auth/register should be public (reaches controller, 400 on empty body, not 401)")
    void postRegisterShouldBePublic() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/login should be public (reaches controller, 400 on empty body, not 401)")
    void postLoginShouldBePublic() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // --- Non-public endpoints and methods must return 401 Unauthorized ---

    @Test
    @DisplayName("POST /api/health must require authentication (401 Unauthorized)")
    void postHealthRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/health"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Authentication required"));
    }

    @Test
    @DisplayName("GET /api/health/sub-path must require authentication (401 Unauthorized)")
    void subHealthPathRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/health/sub-path"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/auth/register must require authentication (401 Unauthorized)")
    void getRegisterRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/auth/login must require authentication (401 Unauthorized)")
    void getLoginRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/auth/future-endpoint must require authentication by default (401 Unauthorized)")
    void futureAuthEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Unauthenticated request to protected endpoint should return 401 Unauthorized")
    void protectedEndpointShouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/protected-resource"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Authentication required"));
    }

    // --- JWT validation and rejection tests (invalid and expired tokens return HTTP 401) ---

    @Test
    @DisplayName("Protected endpoint must return 401 when token has invalid signature")
    void protectedEndpointShouldReturn401OnSignatureFailure() throws Exception {
        when(jwtService.extractUsername("tampered.signature.token"))
                .thenThrow(new SignatureException("JWT signature does not match"));

        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer tampered.signature.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Authentication required"));
    }

    @Test
    @DisplayName("Protected endpoint must return 401 when token is expired")
    void protectedEndpointShouldReturn401OnExpiredToken() throws Exception {
        when(jwtService.extractUsername("expired.jwt.token"))
                .thenThrow(new ExpiredJwtException(null, null, "JWT expired"));

        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer expired.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Authentication required"));
    }

    @Test
    @DisplayName("Protected endpoint must return 401 when token is invalid for loaded user")
    void protectedEndpointShouldReturn401WhenTokenInvalidForUser() throws Exception {
        when(jwtService.extractUsername("valid-format.token")).thenReturn("user@example.com");
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(
                new CustomUserDetails(
                        UUID.randomUUID(),
                        "user@example.com",
                        "pwd",
                        UserRole.ORGANIZATION_ADMIN,
                        UserStatus.ACTIVE,
                        null,
                        null,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ORGANIZATION_ADMIN"))
                )
        );
        when(jwtService.isTokenValid(eq("valid-format.token"), any())).thenReturn(false);

        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer valid-format.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
