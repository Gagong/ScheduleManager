package ru.schedule.manager.infrastructure.base.dictionary.administered.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import ru.schedule.manager.infrastructure.base.dto.BaseResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DictionaryDto extends BaseResponseDto {

	private static final long serialVersionUID = 1L;

	private String type;

	private String key;

	private String value;

}
