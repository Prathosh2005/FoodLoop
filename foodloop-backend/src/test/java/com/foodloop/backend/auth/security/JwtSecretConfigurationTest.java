package com.foodloop.backend.auth.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtSecretConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(JwtService.class);

    @Test
    @DisplayName("Should reject null secret with clear error message")
    void shouldRejectNullSecret() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                new JwtService(null, 86400000L)
        );
        assertTrue(ex.getMessage().contains("JWT signing secret must be configured"));
    }

    @Test
    @DisplayName("Should reject empty or blank secret with clear error message")
    void shouldRejectBlankSecret() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                new JwtService("   ", 86400000L)
        );
        assertTrue(ex.getMessage().contains("cannot be blank"));
    }

    @Test
    @DisplayName("Should reject secret shorter than 256 bits (32 bytes)")
    void shouldRejectWeakSecret() {
        String weakSecret = "short-secret-under-32-chars";
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                new JwtService(weakSecret, 86400000L)
        );
        assertTrue(ex.getMessage().contains("at least 256 bits (32 bytes)"));
    }

    @Test
    @DisplayName("Should never expose or log the secret string in exception messages")
    void shouldNotExposeSecretInException() {
        String sensitiveSecret = "weak-secret-value-12345";
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                new JwtService(sensitiveSecret, 86400000L)
        );
        assertFalse(ex.getMessage().contains(sensitiveSecret), "Exception message must not reveal the secret");
    }

    @Test
    @DisplayName("Should accept valid secret of 256 bits (32 bytes) or greater")
    void shouldAcceptStrongSecret() {
        String strongSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        assertDoesNotThrow(() -> new JwtService(strongSecret, 86400000L));
    }

    @Test
    @DisplayName("Application context must fail startup when foodloop.jwt.secret is missing")
    void contextFailsWhenJwtSecretMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    @DisplayName("Application context must fail startup when foodloop.jwt.secret is weak")
    void contextFailsWhenJwtSecretIsWeak() {
        contextRunner
                .withPropertyValues("foodloop.jwt.secret=too-short-secret")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(IllegalStateException.class);
                    assertThat(org.springframework.core.NestedExceptionUtils.getRootCause(context.getStartupFailure()))
                            .hasMessageContaining("at least 256 bits (32 bytes)");
                });
    }

    @Test
    @DisplayName("Application context succeeds startup when foodloop.jwt.secret is sufficiently strong")
    void contextSucceedsWhenJwtSecretIsStrong() {
        contextRunner
                .withPropertyValues("foodloop.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(JwtService.class);
                });
    }
}
