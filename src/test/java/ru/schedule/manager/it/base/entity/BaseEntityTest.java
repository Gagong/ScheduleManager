package ru.schedule.manager.it.base.entity;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.entity.Employee;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseEntityTest {

    private static final Employee ADMIN = new Employee();

    @Test
    void builder_ShouldCreateEntity() {
        final LocalDateTime now = LocalDateTime.now();

        final TestEntity entity = TestEntity.builder()
                .id(1L)
                .createdDateTime(now)
                .updateDateTime(now)
                .isNew(false)
                .build();

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(now, entity.getCreatedDateTime());
        assertEquals(now, entity.getUpdateDateTime());
        assertFalse(entity.isNew());
    }

    @Test
    void defaultValues_ShouldBeSet() {
        final TestEntity entity = new TestEntity();

        assertNotNull(entity.getCreatedDateTime());
        assertNotNull(entity.getUpdateDateTime());
        assertTrue(entity.isNew());
    }

    @Test
    void toString_ShouldContainIdAndClass() {
        final TestEntity entity = new TestEntity(1L, LocalDateTime.now(), LocalDateTime.now(), ADMIN, ADMIN);

        final String toString = entity.toString();

        assertTrue(toString.contains("TestEntity"));
        assertTrue(toString.contains("id=1"));
    }

    @Test
    void equals_ShouldUseId() {
        final LocalDateTime now = LocalDateTime.now();
        final TestEntity entity1 = new TestEntity(1L, now, now, ADMIN, ADMIN);
        final TestEntity entity2 = new TestEntity(1L, now, now, ADMIN, ADMIN);
        final TestEntity entity3 = new TestEntity(2L, now, now, ADMIN, ADMIN);

        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1, null);
        assertNotEquals(entity1, new Object());
    }

    @SuperBuilder
    @NoArgsConstructor
    private static class TestEntity extends BaseEntity {
        TestEntity(final Long id, final LocalDateTime createdDateTime, final LocalDateTime updateDateTime, final Employee createdBy, final Employee updateBy) {
            super(id, 1L, createdDateTime, updateDateTime, createdBy, updateBy, 1L);
        }

    }

}
