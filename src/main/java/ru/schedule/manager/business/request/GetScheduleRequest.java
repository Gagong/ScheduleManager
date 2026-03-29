package ru.schedule.manager.business.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

@Data
@NoArgsConstructor
public class GetScheduleRequest {

	private DictionaryDto professor;

	private DictionaryDto faculty;

	private DictionaryDto group;

	private DictionaryDto subgroup;

	private DictionaryDto semester;

	public GetScheduleRequest(final DictionaryDto semester, final DictionaryDto faculty, final DictionaryDto group) {
		this.semester = semester;
		this.faculty = faculty;
		this.group = group;
	}

}
