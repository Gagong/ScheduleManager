package ru.schedule.manager.it.base.entity;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.entity.AbstractEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractEntityTest {

    @Test
    void defaultConstructor_ShouldSetNewToTrue() {
        final TestEntity entity = new TestEntity();

        assertTrue(entity.isNew());
    }

    @Test
    void constructor_ShouldSetNewValue() {
        final TestEntity entity = new TestEntity(false);

        assertFalse(entity.isNew());
    }

    @Test
    void setNew_ShouldUpdateValue() {
        final TestEntity entity = new TestEntity();

        entity.setNew(false);
        assertFalse(entity.isNew());

        entity.setNew(true);
        assertTrue(entity.isNew());
    }

    @Test
    void toString_ShouldContainClassName() {
        final TestEntity entity = new TestEntity();

        final String toString = entity.toString();

        assertTrue(toString.contains("TestEntity"));
    }

    @NoArgsConstructor
    private static class TestEntity extends AbstractEntity {

        TestEntity(final boolean isNew) {
            super(isNew);
        }

    }

}
