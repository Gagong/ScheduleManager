package ru.schedule.manager.it.base.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.serializer.BaseEntitySerializer;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BaseEntitySerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializerProvider serializerProvider;

    private BaseEntitySerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new BaseEntitySerializer();
    }

    @Test
    void serialize_ShouldWriteId() throws IOException {
        final TestEntity entity = new TestEntity(123L);

        serializer.serialize(entity, jsonGenerator, serializerProvider);

        verify(jsonGenerator).writeStartObject();
        verify(jsonGenerator).writeNumberField("id", 123L);
        verify(jsonGenerator).writeEndObject();
    }

    private static class TestEntity extends BaseEntity {
        TestEntity(final Long id) {
            super(id, null, null);
        }
    }

}
