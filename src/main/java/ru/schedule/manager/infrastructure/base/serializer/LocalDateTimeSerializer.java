package ru.schedule.manager.infrastructure.base.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DTO_DATE_TIME_FORMATTER;

@Component
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

	@Override
	public void serialize(final LocalDateTime value,
	                      final JsonGenerator gen,
	                      final SerializerProvider serializers) throws IOException {
		gen.writeString(value.format(DTO_DATE_TIME_FORMATTER));
	}

}
