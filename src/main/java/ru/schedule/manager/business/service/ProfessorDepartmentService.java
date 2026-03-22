package ru.schedule.manager.business.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.schedule.manager.business.dto.ProfessorDepartmentLnkDto;
import ru.schedule.manager.business.entity.ProfessorDepartmentLnk;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;
import ru.schedule.manager.business.repository.ProfessorDepartmentLnkRepository;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.schedule.manager.business.exception.ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN;

@Service
@RequiredArgsConstructor
public class ProfessorDepartmentService implements BaseServiceAware<ProfessorDepartmentLnk, ProfessorDepartmentLnkDto> {

	private final AdministeredDictionaryService administeredDictionaryService;

	private final ProfessorDepartmentLnkRepository professorDepartmentLnkRepository;

	@Override
	public ProfessorDepartmentLnkDto fromEntity(final ProfessorDepartmentLnk entity) {
		if (Objects.isNull(entity)) {
			return null;
		}
		return ProfessorDepartmentLnkDto.builder()
			.professor(administeredDictionaryService.fromEntity(entity.getProfessor()))
			.department(administeredDictionaryService.fromEntity(entity.getDepartment()))
			.id(entity.getId())
			.createdDateTime(entity.getCreatedDateTime())
			.updateDateTime(entity.getUpdateDateTime())
			.createdBy(entity.getCreatedByEmployee().getFullName())
			.updatedBy(entity.getUpdatedByEmployee().getFullName())
			.build();
	}

	@Override
	public List<ProfessorDepartmentLnkDto> fromEntity(final List<ProfessorDepartmentLnk> entities) {
		return entities.stream()
			.filter(Objects::nonNull)
			.map(this::fromEntity)
			.sorted(Comparator.comparing(value -> value.getDepartment().getValue()))
			.collect(Collectors.toList());
	}

	@Override
	public ProfessorDepartmentLnkDto findById(final Long id) {
		return this.fromEntity(
			professorDepartmentLnkRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
					ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
					ProfessorDepartmentLnk.class.getSimpleName(),
					id
				)))
		);
	}

	@Override
	public ProfessorDepartmentLnkDto update(final ProfessorDepartmentLnkDto dto) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	@SneakyThrows
	public ProfessorDepartmentLnkDto create(final ProfessorDepartmentLnkDto dto) {
		final Dictionary professor = administeredDictionaryService.getOneAsEntity(dto.getProfessor());
		final Dictionary department = administeredDictionaryService.getOneAsEntity(dto.getDepartment());
		return this.fromEntity(
			professorDepartmentLnkRepository.save(
					professorDepartmentLnkRepository.findFirstByProfessor(professor)
							.map(lnk -> lnk.setDepartment(department))
							.orElseGet(
									() -> ProfessorDepartmentLnk.builder()
											.department(department)
											.professor(professor)
											.build()
							)
			)
		);
	}

	@Override
	public void delete(final ProfessorDepartmentLnkDto dto) {
		professorDepartmentLnkRepository.findById(dto.getId()).ifPresentOrElse(professorDepartmentLnkRepository::delete, () -> {
			throw new EntityNotFoundException(ExceptionMessageUtils.of(
				ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
				ProfessorDepartmentLnk.class.getSimpleName(),
				dto.getId()
			));
		});
	}

	public List<ProfessorDepartmentLnkDto> getDepartmentProfessors(final DictionaryDto department) {
		return this.fromEntity(
			professorDepartmentLnkRepository.findByDepartment(
				administeredDictionaryService.getOneAsEntity(department)
			)
		);
	}

	public ProfessorDepartmentLnkDto getProfessorDepartment(final DictionaryDto professor) {
		return this.fromEntity(
				professorDepartmentLnkRepository.findByProfessor(
						administeredDictionaryService.getOneAsEntity(professor)
				).orElse(null)
		);
	}

}
