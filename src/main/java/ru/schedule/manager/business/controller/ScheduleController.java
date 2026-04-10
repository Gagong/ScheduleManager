package ru.schedule.manager.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.schedule.manager.business.dataholder.ScheduleColDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleRowDataHolder;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.request.GetScheduleRequest;
import ru.schedule.manager.business.service.ScheduleService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;

import java.util.List;
import java.util.Map;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "schedule")
public class ScheduleController {

	private final ScheduleService scheduleService;

	private final AdministeredDictionaryService administeredDictionaryService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("getAllSchedule")
	public ResponseEntity<StreamingResponseBody> getAllSchedule() {
		return scheduleService.getAllSchedule();
	}

	@PostMapping("getSingleSchedule")
	public ResponseEntity<StreamingResponseBody> getSingleSchedule(@RequestBody final GetScheduleRequest request) {
		return scheduleService.getSingleSchedule(request);
	}

	@PostMapping("getSchedule")
	public ScheduleDataHolder getSchedule(@RequestBody final GetScheduleRequest request, @RequestParam(required = false, defaultValue = "true") final boolean editable) {
		return scheduleService.getSchedule(request, editable);
	}

	@PostMapping("getFreeClassRoomsAndProfessors")
	public Map<AdministeredDictionaryType, List<DictionaryDto>> getFreeClassRoomsAndProfessors(@RequestBody final ScheduleItemDto item) {
		return scheduleService.getFreeClassRoomsAndProfessors(item);
	}

	@GetMapping("getCurrentSemester")
	public DictionaryDto getCurrentSemester() {
		return administeredDictionaryService.fromEntity(scheduleService.getCurrentSemester());
	}

	@PostMapping("save")
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public void delete(@PathVariable final Long id) {
		scheduleService.delete(ScheduleItemDto.builder().id(id).build());
	}

}
