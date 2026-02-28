package ru.schedule.manager.business.dataholder;

import lombok.Data;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

import java.util.List;

@Data
public class ScheduleDataHolder {

	private final DictionaryDto semester;

	private final List<ScheduleRowDataHolder> rows;

}
