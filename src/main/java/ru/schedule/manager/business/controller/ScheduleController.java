package ru.schedule.manager.business.controller;

import java.util.LinkedList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.schedule.manager.business.dto.ScheduleItemDto;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "schedule")
public class ScheduleController {

	@GetMapping("getDefaultItems")
	public List<Row> getDefaultItems() {
		final List<Row> rows = new LinkedList<>();
		for (int r = 0; r < 2; r++) {
			final Row row = new Row(new LinkedList<>());
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
				row.getCols().add(new Col(list));
			}
			rows.add(row);
		}
		return rows;
	}

	@Data
	private static class Row {

		@Builder.Default
		private final List<Col> cols;

	}

	@Data
	private static class Col {

		@Builder.Default
		private final List<ScheduleItemDto> items;

	}

}
