package ru.schedule.manager.infrastructure.base.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.schedule.manager.infrastructure.base.deserializer.LocalDateTimeDeserializer;
import ru.schedule.manager.infrastructure.base.serializer.LocalDateTimeSerializer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public abstract class BaseResponseDto implements Serializable {

	protected Long id;

	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	protected LocalDateTime createdDateTime;

	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	protected LocalDateTime updateDateTime;

}
