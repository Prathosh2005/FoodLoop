package com.foodloop.backend.auth.security;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Should load existing user by email returning UserDetails")
    void shouldLoadExistingUserByEmail() {
        User user = new User(
                "Jane",
                "Doe",
                "jane@example.com",
                "encodedHash",
                "1234567890",
                UserRole.ORGANIZATION_ADMIN,
                UserStatus.ACTIVE,
                null
        );
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("jane@example.com");

        assertNotNull(details);
        assertEquals("jane@example.com", details.getUsername());
        assertEquals("encodedHash", details.getPassword());
        verify(userRepository, times(1)).findByEmail("jane@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("nonexistent@example.com"));

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
}
