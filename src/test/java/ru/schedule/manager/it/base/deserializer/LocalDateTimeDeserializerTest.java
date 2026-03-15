package ru.schedule.manager.it.base.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.infrastructure.base.deserializer.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DTO_DATE_TIME_FORMATTER;

@ExtendWith(MockitoExtension.class)
class LocalDateTimeDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    private LocalDateTimeDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new LocalDateTimeDeserializer();
    }

    @Test
    void deserialize_WithValidDate_ShouldReturnLocalDateTime() throws Exception {
        final LocalDateTime expected = LocalDateTime.now();
        final String dateString = expected.format(DTO_DATE_TIME_FORMATTER);
        when(jsonParser.getText()).thenReturn(dateString);

        final LocalDateTime result = deserializer.deserialize(jsonParser, deserializationContext);

        assertNotNull(result);
        assertEquals(expected.format(DTO_DATE_TIME_FORMATTER), result.format(DTO_DATE_TIME_FORMATTER));
    }

    @Test
    void deserialize_WithEmptyString_ShouldReturnNull() throws Exception {
        when(jsonParser.getText()).thenReturn("");

        final LocalDateTime result = deserializer.deserialize(jsonParser, deserializationContext);

        assertNull(result);
    }

    @Test
    void deserialize_WithNull_ShouldReturnNull() throws Exception {
        when(jsonParser.getText()).thenReturn(null);

        final LocalDateTime result = deserializer.deserialize(jsonParser, deserializationContext);

        assertNull(result);
    }

}
