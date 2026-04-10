package ru.schedule.manager.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.schedule.manager.business.dto.ProfessorDepartmentLnkDto;
import ru.schedule.manager.business.dto.ProfessorDisciplineLnkDto;
import ru.schedule.manager.business.request.AddProfessorDisciplinesRequest;
import ru.schedule.manager.business.service.ProfessorDepartmentService;
import ru.schedule.manager.business.service.ProfessorDisciplineService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

import java.util.List;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "professor")
public class ProfessorController {

	private final ProfessorDisciplineService professorDisciplineService;

	private final ProfessorDepartmentService professorDepartmentService;

	@PostMapping("getProfessorDisciplines")
	public List<ProfessorDisciplineLnkDto> getProfessorDisciplines(@RequestBody final DictionaryDto professor) {
		return professorDisciplineService.getProfessorDisciplines(professor);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("addProfessorDiscipline")
	public void addProfessorDiscipline(@RequestBody final AddProfessorDisciplinesRequest data) {
		data.getDisciplines().stream()
			.map(discipline -> ProfessorDisciplineLnkDto.builder().professor(data.getProfessor()).discipline(discipline).build())
			.forEach(professorDisciplineService::create);
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("deleteProfessorDiscipline")
	public void deleteProfessorDiscipline(@RequestParam final Long id) {
		professorDisciplineService.delete(new ProfessorDisciplineLnkDto(id));
	}

	@PostMapping("getProfessorDepartment")
	public ProfessorDepartmentLnkDto getProfessorDepartment(@RequestBody final DictionaryDto professor) {
		return professorDepartmentService.getProfessorDepartment(professor);
	}

	@PostMapping("getDepartmentProfessors")
	public List<DictionaryDto> getDepartmentProfessors(@RequestBody final DictionaryDto department) {
		return professorDepartmentService.getDepartmentProfessors(department).stream()
				.map(ProfessorDepartmentLnkDto::getProfessor)
				.toList();
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("updateDepartment")
	public ProfessorDepartmentLnkDto updateDepartment(@RequestBody final ProfessorDepartmentLnkDto dto) {
		return professorDepartmentService.create(dto);
	}

}
