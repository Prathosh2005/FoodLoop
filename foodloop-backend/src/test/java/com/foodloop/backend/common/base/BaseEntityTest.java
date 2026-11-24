package com.foodloop.backend.common.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BaseEntityTest {

    static class TestEntity extends BaseEntity {
    }

    @Test
    @DisplayName("Should correctly set and get BaseEntity fields")
    void shouldSetAndGetFields() {
        TestEntity entity = new TestEntity();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertEquals(id, entity.getId());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should verify equals and hashCode contracts")
    void shouldVerifyEqualsAndHashCode() {
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();
        UUID id = UUID.randomUUID();

        entity1.setId(id);
        entity2.setId(id);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());

        entity2.setId(UUID.randomUUID());
        assertNotEquals(entity1, entity2);
    }
}
