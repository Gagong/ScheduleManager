package ru.schedule.manager.infrastructure.base.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

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
