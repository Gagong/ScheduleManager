package ru.schedule.manager.business.controller;

import java.util.LinkedList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.schedule.manager.business.dataholder.ScheduleColDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleRowDataHolder;
import ru.schedule.manager.business.dictionary.Times;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.service.ScheduleService;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "schedule")
public class ScheduleController {

	private final ScheduleService scheduleService;

	//Not a good way, but its works ;)
	@GetMapping("getSchedule")
	public List<ScheduleRowDataHolder> getSchedule() {
		final List<ScheduleRowDataHolder> scheduleRowDataHolders = new LinkedList<>();
		for (int r = 0; r < 2; r++) {
			final ScheduleRowDataHolder scheduleRowDataHolder = new ScheduleRowDataHolder(new LinkedList<>());
			for (int c = 0; c < 6; c++) {
				final List<ScheduleItemDto> list = new LinkedList<>();
				for (int i = 0; i < 7; i++) {
					list.add(
						scheduleService.findByRowAndColAndTimes(r, c, Times.values()[i])
					);
				}
				scheduleRowDataHolder.getCols().add(new ScheduleColDataHolder(list));
			}
			scheduleRowDataHolders.add(scheduleRowDataHolder);
		}
		return scheduleRowDataHolders;
	}

	@PostMapping("save")
	public void save(@RequestBody final List<ScheduleRowDataHolder> rows) {
		for (final ScheduleRowDataHolder row : rows) {
			for (final ScheduleColDataHolder col : row.getCols()) {
				for (final ScheduleItemDto item : col.getItems()) {
					if (scheduleService.isPresent(item)) {
						scheduleService.update(item);
					} else {
						scheduleService.create(item);
					}
				}
			}
		}
	}

	@DeleteMapping("delete/{id}")
	public void delete(@PathVariable final Long id) {
		scheduleService.delete(ScheduleItemDto.builder().id(id).build());
	}

}
