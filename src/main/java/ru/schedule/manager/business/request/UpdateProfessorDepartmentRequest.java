package ru.schedule.manager.business.request;

import lombok.Data;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

@Data
public class UpdateProfessorDepartmentRequest {

	private DictionaryDto department;

	private DictionaryDto professor;

}
