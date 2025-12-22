package ru.schedule.manager.business.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

import ru.schedule.manager.business.dto.ProfessorDisciplineLnkDto;
import ru.schedule.manager.business.entity.ProfessorDisciplineLnk;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;
import ru.schedule.manager.business.repository.ProfessorDisciplineLnkRepository;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.repository.DictionaryRepository;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import static ru.schedule.manager.business.exception.ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN;

@Service
@RequiredArgsConstructor
public class ProfessorDisciplineService implements BaseServiceAware<ProfessorDisciplineLnk, ProfessorDisciplineLnkDto> {

	private final AdministeredDictionaryService administeredDictionaryService;

	private final ProfessorDisciplineLnkRepository professorDisciplineLnkRepository;

	private final DictionaryRepository dictionaryRepository;

	@Override
	public ProfessorDisciplineLnkDto fromEntity(final ProfessorDisciplineLnk entity) {
		return ProfessorDisciplineLnkDto.builder()
			.professor(administeredDictionaryService.fromEntity(entity.getProfessor()))
			.discipline(administeredDictionaryService.fromEntity(entity.getDiscipline()))
			.id(entity.getId())
			.createdDateTime(entity.getCreatedDateTime())
			.updateDateTime(entity.getUpdateDateTime())
			.build();
	}

	@Override
	public List<ProfessorDisciplineLnkDto> fromEntity(final List<ProfessorDisciplineLnk> entities) {
		return entities.stream()
			.filter(Objects::nonNull)
			.map(this::fromEntity)
			.sorted(Comparator.comparing(value -> value.getDiscipline().getValue()))
			.collect(Collectors.toList());
	}

	@Override
	public ProfessorDisciplineLnkDto findById(final Long id) {
		return this.fromEntity(
			professorDisciplineLnkRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
					ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
					ProfessorDisciplineLnk.class.getSimpleName(),
					id
				)))
		);
	}

	@Override
	public ProfessorDisciplineLnkDto update(final ProfessorDisciplineLnkDto dto) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	@SneakyThrows
	public ProfessorDisciplineLnkDto create(final ProfessorDisciplineLnkDto dto) {
		if (professorDisciplineLnkRepository.findByProfessorAndDiscipline(
			dictionaryRepository.findById(dto.getProfessor().getId()).orElseThrow(),
			dictionaryRepository.findById(dto.getDiscipline().getId()).orElseThrow()).isPresent()) {
			throw new IllegalAccessException("Запись с такими параметрами уже существует");
		}
		return this.fromEntity(
			professorDisciplineLnkRepository.save(
				ProfessorDisciplineLnk.builder()
					.discipline(dictionaryRepository.findById(dto.getDiscipline().getId()).orElseThrow())
					.professor(dictionaryRepository.findById(dto.getProfessor().getId()).orElseThrow())
					.build()
			)
		);
	}

	@Override
	public void delete(final ProfessorDisciplineLnkDto dto) {
		professorDisciplineLnkRepository.findById(dto.getId()).ifPresentOrElse(professorDisciplineLnkRepository::delete, () -> {
			throw new EntityNotFoundException(ExceptionMessageUtils.of(
				ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
				ProfessorDisciplineLnk.class.getSimpleName(),
				dto.getId()
			));
		});
	}

	public List<ProfessorDisciplineLnkDto> getProfessorDisciplines(final DictionaryDto professor) {
		return this.fromEntity(
			professorDisciplineLnkRepository.findByProfessor(
				dictionaryRepository.findById(professor.getId()).orElseThrow()
			)
		);
	}

}
