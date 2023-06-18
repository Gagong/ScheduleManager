package ru.schedule.manager.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dto.BaseResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ScheduleItemDto extends BaseResponseDto {

	private static final long serialVersionUID = 1L;

	private Integer row;

	private Integer col;

	@Builder.Default
	private String value = "Нажмите для заполнения";

	private DictionaryDto discipline;

	private DictionaryDto professor;

	private DictionaryDto classroom;

}