package com.foodloop.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration enabling entity auditing (@CreatedDate, @LastModifiedDate).
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
