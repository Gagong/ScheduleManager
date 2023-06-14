package ru.schedule.manager.infrastructure.base.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;

public class BaseEntitySerializer extends JsonSerializer<BaseEntity> {

	@Override
	public void serialize(final BaseEntity value,
	                      final JsonGenerator gen,
	                      final SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("id", value.getId());
		gen.writeEndObject();
	}

}
