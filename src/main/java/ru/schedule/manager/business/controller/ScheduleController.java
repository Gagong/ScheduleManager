package ru.schedule.manager.business.controller;

import java.util.LinkedList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.schedule.manager.business.dataholder.ScheduleColDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleRowDataHolder;
import ru.schedule.manager.business.dto.ScheduleItemDto;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "schedule")
public class ScheduleController {

	//Not a good way, but its works ;)
	//TODO add entity and DB selector
	@GetMapping("getDefaultItems")
	public List<ScheduleRowDataHolder> getDefaultItems() {
		final List<ScheduleRowDataHolder> scheduleRowDataHolders = new LinkedList<>();
		for (int r = 0; r < 2; r++) {
			final ScheduleRowDataHolder scheduleRowDataHolder = new ScheduleRowDataHolder(new LinkedList<>());
			for (int c = 0; c < 6; c++) {
				final List<ScheduleItemDto> list = new LinkedList<>();
				for (int i = 0; i < 7; i++) {
					list.add(
						ScheduleItemDto.builder()
							.id((long) i)
							.row(r)
							.col(c)
							.build()
					);
				}
				scheduleRowDataHolder.getCols().add(new ScheduleColDataHolder(list));
			}
			scheduleRowDataHolders.add(scheduleRowDataHolder);
		}
		return scheduleRowDataHolders;
	}

}
