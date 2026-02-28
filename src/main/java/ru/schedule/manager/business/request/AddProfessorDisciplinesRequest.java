package ru.schedule.manager.business.request;

import lombok.Data;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

import java.util.List;

@Data
public class AddProfessorDisciplinesRequest {

	private List<DictionaryDto> disciplines;

	private DictionaryDto professor;

}
