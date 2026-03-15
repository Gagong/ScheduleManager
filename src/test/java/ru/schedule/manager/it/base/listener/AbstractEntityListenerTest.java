package ru.schedule.manager.it.base.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.entity.AbstractEntity;
import ru.schedule.manager.infrastructure.base.listener.AbstractEntityListener;

import static org.junit.jupiter.api.Assertions.assertFalse;

class AbstractEntityListenerTest {

    private AbstractEntityListener listener;

    private TestEntity entity;

    @BeforeEach
    void setUp() {
        listener = new AbstractEntityListener();
        entity = new TestEntity();
        entity.setNew(true);
    }

    @Test
    void onLoad_ShouldSetNewToFalse() {
        listener.onLoad(entity);

        assertFalse(entity.isNew());
    }

    @Test
    void postPersist_ShouldSetNewToFalse() {
        listener.postPersist(entity);

        assertFalse(entity.isNew());
    }

    private static class TestEntity extends AbstractEntity {
        // Test implementation
    }

}
