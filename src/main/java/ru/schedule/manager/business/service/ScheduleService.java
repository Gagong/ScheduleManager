package ru.schedule.manager.business.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import ru.schedule.manager.business.dictionary.Times;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.entity.ScheduleItem;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;
import ru.schedule.manager.business.repository.ScheduleItemRepository;
import ru.schedule.manager.infrastructure.base.dictionary.administered.repository.DictionaryRepository;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import static ru.schedule.manager.business.exception.ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN;

@Service
@RequiredArgsConstructor
public class ScheduleService implements BaseServiceAware<ScheduleItem, ScheduleItemDto> {

	private final AdministeredDictionaryService administeredDictionaryService;

	private final ScheduleItemRepository scheduleItemRepository;

	private final DictionaryRepository dictionaryRepository;

	@Override
	public ScheduleItemDto fromEntity(final ScheduleItem entity) {
		final ScheduleItemDto.ScheduleItemDtoBuilder builder = ScheduleItemDto.builder();
		Optional.ofNullable(entity.getProfessor()).map(administeredDictionaryService::fromEntity).ifPresent(builder::professor);
		Optional.ofNullable(entity.getDiscipline()).map(administeredDictionaryService::fromEntity).ifPresent(builder::discipline);
		Optional.ofNullable(entity.getClassroom()).map(administeredDictionaryService::fromEntity).ifPresent(builder::classroom);
		Optional.ofNullable(entity.getDisciplineType()).map(administeredDictionaryService::fromEntity).ifPresent(builder::disciplineType);
		return (ScheduleItemDto) builder
			.row(entity.getRow())
			.col(entity.getCol())
			.times(entity.getTimes())
			.value(entity.getValue())
			.id(entity.getId())
			.createdDateTime(entity.getCreatedDateTime())
			.updateDateTime(entity.getUpdateDateTime())
			.build();
	}

	@Override
	public List<ScheduleItemDto> fromEntity(final List<ScheduleItem> entities) {
		return entities.stream()
			.filter(Objects::nonNull)
			.map(this::fromEntity)
			.sorted(Comparator.comparing(value -> value.getTimes().ordinal()))
			.collect(Collectors.toList());
	}

	@Override
	public ScheduleItemDto findById(final Long id) {
		return this.fromEntity(
			scheduleItemRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
					ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
					ScheduleItem.class.getSimpleName(),
					id
				)))
		);
	}

	@Override
	@Transactional
	public ScheduleItemDto update(final ScheduleItemDto dto) {
		return this.fromEntity(
			scheduleItemRepository.findById(dto.getId())
				.orElseThrow()
				.setClassroom(dictionaryRepository.findById(dto.getClassroom().getId()).orElseThrow())
				.setDiscipline(dictionaryRepository.findById(dto.getDiscipline().getId()).orElseThrow())
				.setProfessor(dictionaryRepository.findById(dto.getProfessor().getId()).orElseThrow())
				.setDisciplineType(dictionaryRepository.findById(dto.getDisciplineType().getId()).orElseThrow())
		);
	}

	@Override
	public ScheduleItemDto create(final ScheduleItemDto dto) {
		if (ObjectUtils.anyNull(dto.getClassroom(), dto.getDiscipline(), dto.getDisciplineType(), dto.getProfessor())) {
			return dto;
		}
		return this.fromEntity(
			scheduleItemRepository.save(
				ScheduleItem.builder()
					.classroom(dictionaryRepository.findById(dto.getClassroom().getId()).orElseThrow())
					.discipline(dictionaryRepository.findById(dto.getDiscipline().getId()).orElseThrow())
					.disciplineType(dictionaryRepository.findById(dto.getDisciplineType().getId()).orElseThrow())
					.professor(dictionaryRepository.findById(dto.getProfessor().getId()).orElseThrow())
					.row(dto.getRow())
					.col(dto.getCol())
					.times(dto.getTimes())
					.build()
			)
		);
	}

	@Override
	public void delete(final ScheduleItemDto dto) {
		scheduleItemRepository.findById(dto.getId()).ifPresentOrElse(scheduleItemRepository::delete, () -> {
			throw new EntityNotFoundException(ExceptionMessageUtils.of(
				ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
				ScheduleItem.class.getSimpleName(),
				dto.getId()
			));
		});
	}

	public ScheduleItemDto findByRowAndColAndTimes(final Integer row, final Integer col, final Times times) {
		final Long lastId = scheduleItemRepository.getMaxId();
		return this.fromEntity(
			scheduleItemRepository.findByRowAndColAndTimes(row, col, times)
				.orElseGet(() -> ScheduleItem.builder()
					.id(ThreadLocalRandom.current().nextLong(lastId + 1L, lastId + 10000L))
					.row(row)
					.col(col)
					.times(times)
					.build())
		);
	}

	public boolean isPresent(final ScheduleItemDto dto) {
		return scheduleItemRepository.existsById(dto.getId());
	}

}
