package ru.schedule.manager.business.request;

import lombok.Data;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

@Data
public class GetScheduleRequest {

	private DictionaryDto professor;

	private DictionaryDto faculty;

	private DictionaryDto group;

	private DictionaryDto subgroup;

	private DictionaryDto semester;

}
