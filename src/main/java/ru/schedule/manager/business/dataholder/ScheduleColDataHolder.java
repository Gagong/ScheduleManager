package ru.schedule.manager.business.dataholder;

import lombok.Data;
import ru.schedule.manager.business.dto.ScheduleItemDto;

import java.util.List;

@Data
public class ScheduleColDataHolder {

	private final List<ScheduleItemDto> items;

}
