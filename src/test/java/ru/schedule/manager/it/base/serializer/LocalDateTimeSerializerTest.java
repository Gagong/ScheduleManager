package ru.schedule.manager.it.base.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.infrastructure.base.serializer.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DTO_DATE_TIME_FORMATTER;

@ExtendWith(MockitoExtension.class)
class LocalDateTimeSerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializerProvider serializerProvider;

    private LocalDateTimeSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new LocalDateTimeSerializer();
    }

    @Test
    void serialize_ShouldWriteFormattedDate() throws IOException {
        final LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        final String expected = dateTime.format(DTO_DATE_TIME_FORMATTER);

        serializer.serialize(dateTime, jsonGenerator, serializerProvider);

        verify(jsonGenerator).writeString(expected);
    }

}
