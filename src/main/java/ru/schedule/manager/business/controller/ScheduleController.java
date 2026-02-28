package ru.schedule.manager.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.schedule.manager.business.dataholder.ScheduleColDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleRowDataHolder;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.entity.ScheduleItem;
import ru.schedule.manager.business.request.GetScheduleRequest;
import ru.schedule.manager.business.service.ScheduleService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "schedule")
public class ScheduleController {

	private final ScheduleService scheduleService;

	private final AdministeredDictionaryService administeredDictionaryService;

	//Not a good way, but its works ;)
	@PostMapping("getSchedule")
	public ScheduleDataHolder getSchedule(@RequestBody final GetScheduleRequest request, @RequestParam(required = false, defaultValue = "true") final boolean editable) {
		final Map<Integer, Dictionary> times = administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)
				.stream()
				.collect(Collectors.toMap(Dictionary::getDisplayOrder, Function.identity()));
		if (ObjectUtils.allNotNull(request.getSemester(), request.getFaculty(), request.getGroup(), request.getSubgroup())) {
			final List<ScheduleRowDataHolder> scheduleRowDataHolders = new LinkedList<>();
			for (int r = 0; r < 2; r++) {
				final ScheduleRowDataHolder scheduleRowDataHolder = new ScheduleRowDataHolder(new LinkedList<>());
				for (int c = 0; c < 6; c++) {
					final List<ScheduleItemDto> list = new LinkedList<>();
					for (int i = 0; i < 7; i++) {
						list.add(
								scheduleService.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(
										r,
										c,
										times.get(i),
										administeredDictionaryService.getOneAsEntity(request.getSemester()),
										administeredDictionaryService.getOneAsEntity(request.getFaculty()),
										administeredDictionaryService.getOneAsEntity(request.getGroup()),
										administeredDictionaryService.getOneAsEntity(request.getSubgroup()),
										editable
								)
						);
					}
					scheduleRowDataHolder.getCols().add(new ScheduleColDataHolder(list));
				}
				scheduleRowDataHolders.add(scheduleRowDataHolder);
			}
			return new ScheduleDataHolder(
					request.getSemester(),
					scheduleRowDataHolders
			);
		} else if (ObjectUtils.allNotNull(request.getSemester(), request.getProfessor())) {
			final List<ScheduleRowDataHolder> scheduleRowDataHolders = new LinkedList<>();
			for (int r = 0; r < 2; r++) {
				final ScheduleRowDataHolder scheduleRowDataHolder = new ScheduleRowDataHolder(new LinkedList<>());
				for (int c = 0; c < 6; c++) {
					final List<ScheduleItemDto> list = new LinkedList<>();
					for (int i = 0; i < 7; i++) {
						list.add(
								scheduleService.findByRowAndColAndTimesAndSemesterAndProfessor(
										r,
										c,
										times.get(i),
										administeredDictionaryService.getOneAsEntity(request.getSemester()),
										administeredDictionaryService.getOneAsEntity(request.getProfessor()),
										editable
								)
						);
					}
					scheduleRowDataHolder.getCols().add(new ScheduleColDataHolder(list));
				}
				scheduleRowDataHolders.add(scheduleRowDataHolder);
			}
			return new ScheduleDataHolder(
					request.getSemester(),
					scheduleRowDataHolders
			);
		} else {
			final Dictionary currentSemester = scheduleService.getCurrentSemester();
			final List<ScheduleRowDataHolder> scheduleRowDataHolders = new LinkedList<>();
			for (int r = 0; r < 2; r++) {
				final ScheduleRowDataHolder scheduleRowDataHolder = new ScheduleRowDataHolder(new LinkedList<>());
				for (int c = 0; c < 6; c++) {
					final List<ScheduleItemDto> list = new LinkedList<>();
					for (int i = 0; i < 7; i++) {
						list.add(
								scheduleService.fromEntity(
										ScheduleItem.builder()
												.id(ThreadLocalRandom.current().nextLong(100000000L, 1000000000L))
												.semester(currentSemester)
												.row(r)
												.col(c)
												.times(times.get(i))
												.build()
												.setEditable(editable)
								)
						);
					}
					scheduleRowDataHolder.getCols().add(new ScheduleColDataHolder(list));
				}
				scheduleRowDataHolders.add(scheduleRowDataHolder);
			}
			return new ScheduleDataHolder(
					administeredDictionaryService.fromEntity(currentSemester),
					scheduleRowDataHolders
			);
		}
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
