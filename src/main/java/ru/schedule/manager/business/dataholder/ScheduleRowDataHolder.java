package ru.schedule.manager.business.dataholder;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class ScheduleRowDataHolder {

	@Builder.Default
	private final List<ScheduleColDataHolder> cols;

}
