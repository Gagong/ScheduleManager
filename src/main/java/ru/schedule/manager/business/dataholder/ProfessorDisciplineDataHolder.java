package ru.schedule.manager.business.dataholder;

import java.util.List;

import lombok.Data;

import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

@Data
public class ProfessorDisciplineDataHolder {

	private List<DictionaryDto> disciplines;

	private DictionaryDto professor;

}
