package ru.schedule.manager.it.base.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.infrastructure.base.dictionary.SimpleDictionary;
import ru.schedule.manager.infrastructure.base.serializer.BaseDictionarySerializer;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BaseDictionarySerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializerProvider serializerProvider;

    private BaseDictionarySerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new BaseDictionarySerializer();
    }

    @Test
    void serialize_ShouldWriteKeyAndValue() throws IOException {
        final TestDictionary dictionary = new TestDictionary("TEST_KEY", "TEST_VALUE");

        serializer.serialize(dictionary, jsonGenerator, serializerProvider);

        verify(jsonGenerator).writeStartObject();
        verify(jsonGenerator).writeStringField("key", "TEST_KEY");
        verify(jsonGenerator).writeStringField("value", "TEST_VALUE");
        verify(jsonGenerator).writeEndObject();
    }

    private static class TestDictionary implements SimpleDictionary {
        private final String key;
        private final String value;

        TestDictionary(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getDictionaryKey() {
            return key;
        }

        @Override
        public String getDictionaryValue() {
            return value;
        }
    }

}
