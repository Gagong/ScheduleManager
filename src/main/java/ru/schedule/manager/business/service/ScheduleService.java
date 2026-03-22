package ru.schedule.manager.business.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.entity.ScheduleItem;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;
import ru.schedule.manager.business.repository.ScheduleItemRepository;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static ru.schedule.manager.business.dictionary.AdministeredDictionaryType.CLASSROOM;
import static ru.schedule.manager.business.dictionary.AdministeredDictionaryType.PROFESSOR;
import static ru.schedule.manager.business.dictionary.SemesterType.AUTUMN;
import static ru.schedule.manager.business.dictionary.SemesterType.SPRING;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN;
import static ru.schedule.manager.business.utils.DateTimeUtils.isAfterOrEquals;
import static ru.schedule.manager.business.utils.DateTimeUtils.isBeforeOrEquals;
import static ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary.defaultSubgroup;

@Service
@RequiredArgsConstructor
public class ScheduleService implements BaseServiceAware<ScheduleItem, ScheduleItemDto> {

	private final AdministeredDictionaryService administeredDictionaryService;

	private final ScheduleItemRepository scheduleItemRepository;

	@Override
	public ScheduleItemDto fromEntity(final ScheduleItem entity) {
		return ScheduleItemDto.builder()
			.professor(administeredDictionaryService.dictionaryEntityToDto(entity.getProfessor()))
			.discipline(administeredDictionaryService.dictionaryEntityToDto(entity.getDiscipline()))
			.classroom(administeredDictionaryService.dictionaryEntityToDto(entity.getClassroom()))
			.disciplineType(administeredDictionaryService.dictionaryEntityToDto(entity.getDisciplineType()))
			.times(administeredDictionaryService.dictionaryEntityToDto(entity.getTimes()))
			.faculty(administeredDictionaryService.dictionaryEntityToDto(entity.getFaculty()))
			.group(administeredDictionaryService.dictionaryEntityToDto(entity.getGroup()))
			.subgroup(administeredDictionaryService.dictionaryEntityToDto(entity.getSubgroup()))
			.semester(administeredDictionaryService.dictionaryEntityToDto(entity.getSemester()))
			.row(entity.getRow())
			.col(entity.getCol())
			.value(entity.getValue())
			.id(entity.getId())
			.createdDateTime(entity.getCreatedDateTime())
			.updateDateTime(entity.getUpdateDateTime())
			.createdBy(Optional.ofNullable(entity.getCreatedByEmployee()).map(Employee::getFullName).orElse(null))
			.updatedBy(Optional.ofNullable(entity.getUpdatedByEmployee()).map(Employee::getFullName).orElse(null))
			.build();
	}

	@Override
	public List<ScheduleItemDto> fromEntity(final List<ScheduleItem> entities) {
		return entities.stream()
			.filter(Objects::nonNull)
			.map(this::fromEntity)
			.sorted(Comparator.comparing(value -> value.getTimes().getDisplayOrder()))
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
				.setClassroom(administeredDictionaryService.getOneAsEntity(dto.getClassroom()))
				.setDiscipline(administeredDictionaryService.getOneAsEntity(dto.getDiscipline()))
				.setProfessor(administeredDictionaryService.getOneAsEntity(dto.getProfessor()))
				.setDisciplineType(administeredDictionaryService.getOneAsEntity(dto.getDisciplineType()))
				.setFaculty(administeredDictionaryService.getOneAsEntity(dto.getFaculty()))
				.setGroup(administeredDictionaryService.getOneAsEntity(dto.getGroup()))
				.setSubgroup(administeredDictionaryService.getOneAsEntity(dto.getSubgroup()))
				.setSemester(administeredDictionaryService.getOneAsEntity(dto.getSemester()))
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
					.classroom(administeredDictionaryService.getOneAsEntity(dto.getClassroom()))
					.discipline(administeredDictionaryService.getOneAsEntity(dto.getDiscipline()))
					.disciplineType(administeredDictionaryService.getOneAsEntity(dto.getDisciplineType()))
					.professor(administeredDictionaryService.getOneAsEntity(dto.getProfessor()))
					.times(administeredDictionaryService.getOneAsEntity(dto.getTimes()))
					.faculty(administeredDictionaryService.getOneAsEntity(dto.getFaculty()))
					.group(administeredDictionaryService.getOneAsEntity(dto.getGroup()))
					.subgroup(administeredDictionaryService.getOneAsEntity(dto.getSubgroup()))
					.semester(administeredDictionaryService.getOneAsEntity(dto.getSemester()))
					.row(dto.getRow())
					.col(dto.getCol())
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

	public ScheduleItemDto findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(final Integer row,
																						   final Integer col,
																						   final Dictionary times,
																						   final Dictionary semester,
																						   final Dictionary faculty,
																						   final Dictionary group,
																						   final Dictionary subgroup,
																						   final boolean editable) {
		final Long lastId = scheduleItemRepository.getMaxId().orElse(0L);
		return this.fromEntity(
				scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubgroup(row, col, times, semester, faculty, group, subgroup)
						.or(() -> scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubgroup(row, col, times, semester, faculty, group, defaultSubgroup()))
						.or(() -> scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndFacultyAndGroup(row, col, times, semester, faculty, group))
						.orElseGet(() -> ScheduleItem.builder()
								.id(ThreadLocalRandom.current().nextLong(lastId + 1000000L, lastId + 10000000L))
								.semester(semester)
								.faculty(faculty)
								.group(group)
								.subgroup(subgroup)
								.row(row)
								.col(col)
								.times(times)
								.build()
						).setEditable(editable)
		);
	}

	public ScheduleItemDto findByRowAndColAndTimesAndSemesterAndProfessor(final Integer row,
																						   final Integer col,
																						   final Dictionary times,
																						   final Dictionary semester,
																						   final Dictionary professor,
																						   final boolean editable) {
		final Long lastId = scheduleItemRepository.getMaxId().orElse(0L);
		return this.fromEntity(
				scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndProfessor(row, col, times, semester, professor)
						.orElseGet(() -> ScheduleItem.builder()
								.id(ThreadLocalRandom.current().nextLong(lastId + 1000000L, lastId + 10000000L))
								.semester(semester)
								.professor(professor)
								.row(row)
								.col(col)
								.times(times)
								.build()
						).setEditable(editable)
		);
	}

	public boolean isPresent(final ScheduleItemDto dto) {
		return scheduleItemRepository.existsById(dto.getId());
	}

	/**
	 * Сентябрь - декабрь = Осенний семестр ${year}/${year} + 1
	 * Январь - август = Весенний семестр ${year} - 1/${year}
	 */
	public Dictionary getCurrentSemester() {
		final LocalDate date = LocalDate.now();
		final LocalDate firstDayOfSeptember = LocalDate.of(date.getYear(), Month.SEPTEMBER, 1);
		final LocalDate lastDayOfDecember = LocalDate.of(date.getYear(), Month.DECEMBER, 31);
		if (isAfterOrEquals(date, firstDayOfSeptember) && isBeforeOrEquals(date, lastDayOfDecember)) {
			return administeredDictionaryService.getEntityByTypeAndKey(AdministeredDictionaryType.SEMESTER, AUTUMN.name() + "_" + date.getYear() + "_" + (date.getYear() + 1));
		} else {
			return administeredDictionaryService.getEntityByTypeAndKey(AdministeredDictionaryType.SEMESTER, SPRING.name() + "_" + (date.getYear() - 1) + "_" + date.getYear());
		}
	}

	public Map<AdministeredDictionaryType, List<DictionaryDto>> getFreeClassRoomsAndProfessors(final ScheduleItemDto item) {
		final List<DictionaryDto> allClassRooms = new ArrayList<>(administeredDictionaryService.getAllByType(CLASSROOM, true));
		final List<DictionaryDto> allProfessors = new ArrayList<>(administeredDictionaryService.getAllByType(PROFESSOR, true));
		final List<ScheduleItem> getFilledSchedule = scheduleItemRepository.findAllByRowAndColAndTimesAndSemesterAndClassroomIsNotNullAndProfessorIsNotNull(
				item.getRow(),
				item.getCol(),
				administeredDictionaryService.getOneAsEntity(item.getTimes()),
				administeredDictionaryService.getOneAsEntity(item.getSemester())
		);
		final List<DictionaryDto> busyClassRooms = getFilledSchedule.stream()
				.map(ScheduleItem::getClassroom)
				.map(administeredDictionaryService::fromEntity)
				.collect(Collectors.toList());
		final List<DictionaryDto> busyProfessors = getFilledSchedule.stream()
				.map(ScheduleItem::getProfessor)
				.map(administeredDictionaryService::fromEntity)
				.collect(Collectors.toList());
		allClassRooms.removeIf(busyClassRooms::contains);
		allProfessors.removeIf(busyProfessors::contains);
		return Map.of(
				CLASSROOM, allClassRooms,
				PROFESSOR, allProfessors
		);
	}

}
