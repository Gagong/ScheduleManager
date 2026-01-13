package ru.schedule.manager.business.dataholder;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleRowDataHolder {

	private final List<ScheduleColDataHolder> cols;

}
