package ru.schedule.manager.it.base.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.listener.BaseEntityListener;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BaseEntityListenerTest {

    private BaseEntityListener listener;

    private TestEntity entity;

    @BeforeEach
    void setUp() {
        listener = new BaseEntityListener();
        entity = new TestEntity();
    }

    @Test
    void onLoad_ShouldNotThrowException() {
        assertDoesNotThrow(() -> listener.onLoad(entity));
    }

    @Test
    void onCreate_ShouldNotThrowException() {
        assertDoesNotThrow(() -> listener.onCreate(entity));
    }

    @Test
    void onUpdate_ShouldNotThrowException() {
        assertDoesNotThrow(() -> listener.onUpdate(entity));
    }

    private static class TestEntity extends BaseEntity {
        // Test implementation
    }

}
