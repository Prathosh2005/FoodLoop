package com.foodloop.backend.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service for issuing, parsing, and validating JSON Web Tokens (JWT).
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${foodloop.jwt.secret}") String secretKeyString,
            @Value("${foodloop.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.signingKey = validateAndCreateSigningKey(secretKeyString);
        this.expirationMs = expirationMs;
    }

    private SecretKey validateAndCreateSigningKey(String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT signing secret must be configured via FOODLOOP_JWT_SECRET and cannot be blank.");
        }

        byte[] keyBytes = null;
        try {
            byte[] decoded = Decoders.BASE64.decode(secret.trim());
            // Only accept Base64 decoding if the decoded byte array meets HMAC-SHA256 key size
            if (decoded.length >= 32) {
                keyBytes = decoded;
            }
        } catch (Exception ignored) {
            // Not a valid Base64 string; fallback to raw UTF-8 bytes
        }

        if (keyBytes == null) {
            keyBytes = secret.trim().getBytes(StandardCharsets.UTF_8);
        }

        // HMAC-SHA256 requires at least 256 bits (32 bytes) of key material
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT signing secret is insufficiently strong. HMAC-SHA256 requires at least 256 bits (32 bytes) of key material."
            );
        }

        try {
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize HMAC-SHA256 JWT signing key.");
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private SecretKey getSigningKey() {
        return this.signingKey;
    }

    /**
     * Generates a signed JWT access token for the given user.
     * Note: organisationVerificationStatus claim included here is purely informational for client UI
     * rendering. Authoritative access control for operational activities (donations, collections)
     * must always check the live database via OrganisationVerificationService.
     */
    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userDetails.getId().toString());
        extraClaims.put("email", userDetails.getEmail());
        extraClaims.put("role", userDetails.getRole().name());

        if (userDetails.getOrganizationId() != null) {
            extraClaims.put("organizationId", userDetails.getOrganizationId().toString());
        }
        if (userDetails.getOrganizationVerificationStatus() != null) {
            extraClaims.put("organizationVerificationStatus", userDetails.getOrganizationVerificationStatus().name());
        }

        return buildToken(extraClaims, userDetails.getUsername(), expirationMs);
    }

    public String buildToken(Map<String, Object> extraClaims, String subject, long expirationTimeMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationTimeMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    public UUID extractOrganizationId(String token) {
        Claims claims = extractAllClaims(token);
        String orgIdStr = claims.get("organizationId", String.class);
        return orgIdStr != null ? UUID.fromString(orgIdStr) : null;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            return true;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
