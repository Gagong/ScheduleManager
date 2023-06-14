package ru.schedule.manager.infrastructure.base.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.schedule.manager.infrastructure.base.dictionary.SimpleDictionary;

public class BaseDictionarySerializer extends JsonSerializer<SimpleDictionary> {

	@Override
	public void serialize(final SimpleDictionary value,
						  final JsonGenerator gen,
						  final SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("key", value.getDictionaryKey());
		gen.writeStringField("value", value.getDictionaryValue());
		gen.writeEndObject();
	}

}
