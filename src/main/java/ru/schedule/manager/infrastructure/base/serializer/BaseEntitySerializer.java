package ru.schedule.manager.infrastructure.base.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;

import java.io.IOException;

@Component
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
