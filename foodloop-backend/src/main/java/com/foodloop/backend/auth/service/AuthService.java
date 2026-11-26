package com.foodloop.backend.auth.service;

import com.foodloop.backend.auth.dto.AuthResponse;
import com.foodloop.backend.auth.dto.LoginRequest;
import com.foodloop.backend.auth.dto.RegisterRequest;
import com.foodloop.backend.auth.dto.UserRegistrationResponse;
import com.foodloop.backend.auth.security.CustomUserDetails;
import com.foodloop.backend.auth.security.JwtService;
import com.foodloop.backend.common.exception.DuplicateResourceException;
import com.foodloop.backend.organization.dto.OrganisationResponse;
import com.foodloop.backend.organization.entity.Organisation;
import com.foodloop.backend.organization.entity.OrganisationType;
import com.foodloop.backend.organization.entity.OrganisationVerificationStatus;
import com.foodloop.backend.organization.repository.OrganisationRepository;
import com.foodloop.backend.user.dto.UserResponse;
import com.foodloop.backend.user.entity.User;
import com.foodloop.backend.user.entity.UserRole;
import com.foodloop.backend.user.entity.UserStatus;
import com.foodloop.backend.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating user and organisation authentication and registration business flows.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            OrganisationRepository organisationRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new organisation and its first administrator user in a single transaction.
     *
     * @param request Registration details
     * @return UserRegistrationResponse containing safe user and organisation views
     */
    @Transactional
    public UserRegistrationResponse register(RegisterRequest request) {
        // Enforce organisation type: must be PROVIDER or NGO
        if (request.getOrganisationType() != OrganisationType.PROVIDER &&
                request.getOrganisationType() != OrganisationType.NGO) {
            throw new IllegalArgumentException("Organisation type must be either PROVIDER or NGO");
        }

        // Validate uniqueness of user email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A user with this email address already exists: " + request.getEmail());
        }

        // Validate uniqueness of organisation registration number
        if (organisationRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("An organisation with this registration number already exists: " + request.getRegistrationNumber());
        }

        // Validate uniqueness of organisation email
        if (organisationRepository.existsByEmail(request.getOrganisationEmail())) {
            throw new DuplicateResourceException("An organisation with this email address already exists: " + request.getOrganisationEmail());
        }

        // Create organisation with PENDING verification status
        Organisation organisation = new Organisation(
                request.getOrganisationName(),
                request.getOrganisationType(),
                request.getDescription(),
                request.getOrganisationEmail(),
                request.getOrganisationPhone(),
                request.getRegistrationNumber(),
                request.getAddress(),
                OrganisationVerificationStatus.PENDING
        );
        Organisation savedOrganisation = organisationRepository.save(organisation);

        // First user must always receive ORGANIZATION_ADMIN role and ACTIVE status
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhoneNumber(),
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                savedOrganisation
        );
        User savedUser = userRepository.save(user);

        return new UserRegistrationResponse(
                UserResponse.fromEntity(savedUser),
                OrganisationResponse.fromEntity(savedOrganisation),
                "Registration successful. Organisation verification is currently pending review."
        );
    }

    /**
     * Authenticates a user with email and password and returns a JWT response.
     *
     * @param request Login credentials
     * @return AuthResponse with JWT token and user/organisation metadata
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new LockedException("User account is locked. Please contact support.");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("User account is inactive. Please contact your organization administrator.");
        }

        CustomUserDetails userDetails = CustomUserDetails.fromUser(user);
        String token = jwtService.generateToken(userDetails);

        OrganisationResponse orgResponse = user.getOrganization() != null
                ? OrganisationResponse.fromEntity(user.getOrganization())
                : null;

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                UserResponse.fromEntity(user),
                orgResponse
        );
    }
}
