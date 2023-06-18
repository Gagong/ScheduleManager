package ru.schedule.manager.business.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.schedule.manager.business.dataholder.ProfessorDisciplineDataHolder;
import ru.schedule.manager.business.dto.ProfessorDisciplineLnkDto;
import ru.schedule.manager.business.service.ProfessorDisciplineService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "professorDiscipline")
public class ProfessorDisciplineController {

	private final ProfessorDisciplineService professorDisciplineService;

	@PostMapping("getProfessorDisciplines")
	public List<ProfessorDisciplineLnkDto> getProfessorDisciplines(@RequestBody final DictionaryDto professor) {
		return professorDisciplineService.getProfessorDisciplines(professor);
	}

	@PostMapping("addProfessorDiscipline")
	public void addProfessorDiscipline(@RequestBody final ProfessorDisciplineDataHolder data) {
		data.getDisciplines().stream()
			.map(discipline -> ProfessorDisciplineLnkDto.builder().professor(data.getProfessor()).discipline(discipline).build())
			.forEach(professorDisciplineService::create);
	}

}
