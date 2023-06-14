package ru.schedule.manager.infrastructure.base.deserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import lombok.SneakyThrows;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DTO_DATE_TIME_FORMATTER;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	@SneakyThrows
	public LocalDateTime deserialize(final JsonParser jsonParser,
																	 final DeserializationContext deserializationContext) {
		return Optional.ofNullable(jsonParser.getText())
				.filter(StringUtils::isNotEmpty)
				.map(value -> LocalDateTime.parse(value, DTO_DATE_TIME_FORMATTER))
				.orElse(null);
	}

}
