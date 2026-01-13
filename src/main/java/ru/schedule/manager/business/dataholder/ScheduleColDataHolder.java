package ru.schedule.manager.business.dataholder;

import java.util.List;

import lombok.Builder;
import lombok.Data;

import ru.schedule.manager.business.dto.ScheduleItemDto;

@Data
public class ScheduleColDataHolder {

	private final List<ScheduleItemDto> items;

}
