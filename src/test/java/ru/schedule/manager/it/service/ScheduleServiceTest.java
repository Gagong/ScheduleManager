package ru.schedule.manager.it.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.entity.ScheduleItem;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.repository.ScheduleItemRepository;
import ru.schedule.manager.business.service.ScheduleService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private AdministeredDictionaryService administeredDictionaryService;

    @Mock
    private ScheduleItemRepository scheduleItemRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private Dictionary professorEntity;

    private Dictionary disciplineEntity;

    private Dictionary classroomEntity;

    private Dictionary disciplineTypeEntity;

    private Dictionary timesEntity;

    private Dictionary facultyEntity;

    private Dictionary groupEntity;

    private Dictionary subgroupEntity;

    private Dictionary semesterEntity;

    private DictionaryDto professorDto;

    private DictionaryDto disciplineDto;

    private DictionaryDto classroomDto;

    private DictionaryDto disciplineTypeDto;

    private DictionaryDto timesDto;

    private DictionaryDto facultyDto;

    private DictionaryDto groupDto;

    private DictionaryDto subgroupDto;

    private DictionaryDto semesterDto;

    private ScheduleItem entity;

    private ScheduleItemDto dto;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        professorEntity = createDictionary(1L, "Professor");
        disciplineEntity = createDictionary(2L, "Discipline");
        classroomEntity = createDictionary(3L, "Classroom");
        disciplineTypeEntity = createDictionary(4L, "Lecture");
        timesEntity = createDictionary(5L, "9:00-10:30");
        facultyEntity = createDictionary(6L, "Faculty");
        groupEntity = createDictionary(7L, "Group");
        subgroupEntity = createDictionary(8L, "Subgroup 1");
        semesterEntity = createDictionary(9L, "Semester 1");

        professorDto = createDictionaryDto(1L, "Professor");
        disciplineDto = createDictionaryDto(2L, "Discipline");
        classroomDto = createDictionaryDto(3L, "Classroom");
        disciplineTypeDto = createDictionaryDto(4L, "Lecture");
        timesDto = createDictionaryDto(5L, "9:00-10:30", 1);
        facultyDto = createDictionaryDto(6L, "Faculty");
        groupDto = createDictionaryDto(7L, "Group");
        subgroupDto = createDictionaryDto(8L, "Subgroup 1");
        semesterDto = createDictionaryDto(9L, "Semester 1");

        entity = ScheduleItem.builder()
                .id(100L)
                .professor(professorEntity)
                .discipline(disciplineEntity)
                .classroom(classroomEntity)
                .disciplineType(disciplineTypeEntity)
                .times(timesEntity)
                .faculty(facultyEntity)
                .group(groupEntity)
                .subgroup(subgroupEntity)
                .semester(semesterEntity)
                .row(0)
                .col(1)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        dto = ScheduleItemDto.builder()
                .id(100L)
                .professor(professorDto)
                .discipline(disciplineDto)
                .classroom(classroomDto)
                .disciplineType(disciplineTypeDto)
                .times(timesDto)
                .faculty(facultyDto)
                .group(groupDto)
                .subgroup(subgroupDto)
                .semester(semesterDto)
                .row(0)
                .col(1)
                .value("Classroom, Professor, Lecture, Discipline")
                .createdDateTime(now)
                .updateDateTime(now)
                .build();
    }

    private Dictionary createDictionary(final Long id, final String value) {
        final Dictionary dict = new Dictionary();
        dict.setId(id);
        dict.setDictionaryValue(value);
        return dict;
    }

    private DictionaryDto createDictionaryDto(final Long id, final String value) {
        return DictionaryDto.builder().id(id).value(value).build();
    }

    private DictionaryDto createDictionaryDto(final Long id, final String value, final Integer displayOrder) {
        return DictionaryDto.builder().id(id).value(value).displayOrder(displayOrder).build();
    }

    @Test
    void fromEntity_ShouldConvertEntityToDto() {
        when(administeredDictionaryService.dictionaryEntityToDto(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.dictionaryEntityToDto(disciplineEntity)).thenReturn(disciplineDto);
        when(administeredDictionaryService.dictionaryEntityToDto(classroomEntity)).thenReturn(classroomDto);
        when(administeredDictionaryService.dictionaryEntityToDto(disciplineTypeEntity)).thenReturn(disciplineTypeDto);
        when(administeredDictionaryService.dictionaryEntityToDto(timesEntity)).thenReturn(timesDto);
        when(administeredDictionaryService.dictionaryEntityToDto(facultyEntity)).thenReturn(facultyDto);
        when(administeredDictionaryService.dictionaryEntityToDto(groupEntity)).thenReturn(groupDto);
        when(administeredDictionaryService.dictionaryEntityToDto(subgroupEntity)).thenReturn(subgroupDto);
        when(administeredDictionaryService.dictionaryEntityToDto(semesterEntity)).thenReturn(semesterDto);

        final ScheduleItemDto result = scheduleService.fromEntity(entity);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(0, result.getRow());
        assertEquals(1, result.getCol());
        assertEquals(professorDto, result.getProfessor());
        assertEquals(disciplineDto, result.getDiscipline());
        assertEquals(classroomDto, result.getClassroom());
        assertNotNull(result.getValue());
    }

    @Test
    void fromEntityList_ShouldConvertEntitiesToDtos() {
        when(administeredDictionaryService.dictionaryEntityToDto(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.dictionaryEntityToDto(disciplineEntity)).thenReturn(disciplineDto);
        when(administeredDictionaryService.dictionaryEntityToDto(classroomEntity)).thenReturn(classroomDto);
        when(administeredDictionaryService.dictionaryEntityToDto(disciplineTypeEntity)).thenReturn(disciplineTypeDto);
        when(administeredDictionaryService.dictionaryEntityToDto(timesEntity)).thenReturn(timesDto);
        when(administeredDictionaryService.dictionaryEntityToDto(facultyEntity)).thenReturn(facultyDto);
        when(administeredDictionaryService.dictionaryEntityToDto(groupEntity)).thenReturn(groupDto);
        when(administeredDictionaryService.dictionaryEntityToDto(subgroupEntity)).thenReturn(subgroupDto);
        when(administeredDictionaryService.dictionaryEntityToDto(semesterEntity)).thenReturn(semesterDto);

        final List<ScheduleItem> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(null);
        final List<ScheduleItemDto> results = scheduleService.fromEntity(entities);

        assertEquals(1, results.size());
        final ScheduleItemDto result = results.get(0);
        assertEquals(100L, result.getId());
        assertEquals(timesDto, result.getTimes());
    }

    @Test
    void findById_WhenEntityExists_ShouldReturnDto() {
        when(scheduleItemRepository.findById(100L)).thenReturn(Optional.of(entity));
        when(administeredDictionaryService.dictionaryEntityToDto(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.dictionaryEntityToDto(disciplineEntity)).thenReturn(disciplineDto);
        when(administeredDictionaryService.dictionaryEntityToDto(classroomEntity)).thenReturn(classroomDto);
        when(administeredDictionaryService.dictionaryEntityToDto(disciplineTypeEntity)).thenReturn(disciplineTypeDto);
        when(administeredDictionaryService.dictionaryEntityToDto(timesEntity)).thenReturn(timesDto);
        when(administeredDictionaryService.dictionaryEntityToDto(facultyEntity)).thenReturn(facultyDto);
        when(administeredDictionaryService.dictionaryEntityToDto(groupEntity)).thenReturn(groupDto);
        when(administeredDictionaryService.dictionaryEntityToDto(subgroupEntity)).thenReturn(subgroupDto);
        when(administeredDictionaryService.dictionaryEntityToDto(semesterEntity)).thenReturn(semesterDto);

        final ScheduleItemDto result = scheduleService.findById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void findById_WhenEntityNotFound_ShouldThrowException() {
        when(scheduleItemRepository.findById(999L)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> scheduleService.findById(999L));

        assertTrue(exception.getMessage().contains("ScheduleItem"));
    }

    @Test
    void update_WhenEntityExists_ShouldUpdateAndReturnDto() {
        when(scheduleItemRepository.findById(100L)).thenReturn(Optional.of(entity));
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(administeredDictionaryService.getOneAsEntity(disciplineDto)).thenReturn(disciplineEntity);
        when(administeredDictionaryService.getOneAsEntity(classroomDto)).thenReturn(classroomEntity);
        when(administeredDictionaryService.getOneAsEntity(disciplineTypeDto)).thenReturn(disciplineTypeEntity);
        when(administeredDictionaryService.getOneAsEntity(facultyDto)).thenReturn(facultyEntity);
        when(administeredDictionaryService.getOneAsEntity(groupDto)).thenReturn(groupEntity);
        when(administeredDictionaryService.getOneAsEntity(subgroupDto)).thenReturn(subgroupEntity);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(administeredDictionaryService.dictionaryEntityToDto(any())).thenReturn(professorDto);

        final ScheduleItemDto result = scheduleService.update(dto);

        assertNotNull(result);
        verify(scheduleItemRepository).findById(100L);
    }

    @Test
    void create_WithAllFields_ShouldCreateAndReturnDto() {
        when(administeredDictionaryService.getOneAsEntity(classroomDto)).thenReturn(classroomEntity);
        when(administeredDictionaryService.getOneAsEntity(disciplineDto)).thenReturn(disciplineEntity);
        when(administeredDictionaryService.getOneAsEntity(disciplineTypeDto)).thenReturn(disciplineTypeEntity);
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(administeredDictionaryService.getOneAsEntity(timesDto)).thenReturn(timesEntity);
        when(administeredDictionaryService.getOneAsEntity(facultyDto)).thenReturn(facultyEntity);
        when(administeredDictionaryService.getOneAsEntity(groupDto)).thenReturn(groupEntity);
        when(administeredDictionaryService.getOneAsEntity(subgroupDto)).thenReturn(subgroupEntity);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(scheduleItemRepository.save(any(ScheduleItem.class))).thenReturn(entity);
        when(administeredDictionaryService.dictionaryEntityToDto(any())).thenReturn(professorDto);

        final ScheduleItemDto result = scheduleService.create(dto);

        assertNotNull(result);
        verify(scheduleItemRepository).save(any(ScheduleItem.class));
    }

    @Test
    void create_WithMissingFields_ShouldReturnDtoWithoutSaving() {
        final ScheduleItemDto incompleteDto = ScheduleItemDto.builder()
                .professor(professorDto)
                .discipline(disciplineDto)
                .classroom(null)
                .build();

        final ScheduleItemDto result = scheduleService.create(incompleteDto);

        assertEquals(incompleteDto, result);
        verify(scheduleItemRepository, never()).save(any());
    }

    @Test
    void delete_WhenEntityExists_ShouldDelete() {
        when(scheduleItemRepository.findById(100L)).thenReturn(Optional.of(entity));

        scheduleService.delete(dto);

        verify(scheduleItemRepository).delete(entity);
    }

    @Test
    void delete_WhenEntityNotFound_ShouldThrowException() {
        when(scheduleItemRepository.findById(100L)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> scheduleService.delete(dto));

        assertTrue(exception.getMessage().contains("ScheduleItem"));
    }

    @Test
    void isPresent_WhenExists_ShouldReturnTrue() {
        when(scheduleItemRepository.existsById(100L)).thenReturn(true);

        final boolean result = scheduleService.isPresent(dto);

        assertTrue(result);
    }

    @Test
    void isPresent_WhenNotExists_ShouldReturnFalse() {
        when(scheduleItemRepository.existsById(100L)).thenReturn(false);

        final boolean result = scheduleService.isPresent(dto);

        assertFalse(result);
    }

    @Test
    void getFreeClassRoomsAndProfessors_ShouldReturnMaps() {
        final ScheduleItemDto itemDto = ScheduleItemDto.builder()
                .row(0)
                .col(1)
                .times(timesDto)
                .semester(semesterDto)
                .build();

        final List<DictionaryDto> allClassRooms = List.of(classroomDto, createDictionaryDto(10L, "Room 2"));
        final List<DictionaryDto> allProfessors = List.of(professorDto, createDictionaryDto(11L, "Professor 2"));

        when(administeredDictionaryService.getAllByType(AdministeredDictionaryType.CLASSROOM, true)).thenReturn(allClassRooms);
        when(administeredDictionaryService.getAllByType(AdministeredDictionaryType.PROFESSOR, true)).thenReturn(allProfessors);
        when(administeredDictionaryService.getOneAsEntity(timesDto)).thenReturn(timesEntity);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(scheduleItemRepository.findAllByRowAndColAndTimesAndSemesterAndClassroomIsNotNullAndProfessorIsNotNull(0, 1, timesEntity, semesterEntity)).thenReturn(List.of(entity));
        when(administeredDictionaryService.fromEntity(classroomEntity)).thenReturn(classroomDto);
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);

        final Map<AdministeredDictionaryType, List<DictionaryDto>> result = scheduleService.getFreeClassRoomsAndProfessors(itemDto);

        assertNotNull(result);
        assertTrue(result.containsKey(AdministeredDictionaryType.CLASSROOM));
        assertTrue(result.containsKey(AdministeredDictionaryType.PROFESSOR));

        final List<DictionaryDto> freeClassRooms = result.get(AdministeredDictionaryType.CLASSROOM);
        final List<DictionaryDto> freeProfessors = result.get(AdministeredDictionaryType.PROFESSOR);

        assertTrue(freeClassRooms.contains(createDictionaryDto(10L, "Room 2")));
        assertFalse(freeClassRooms.contains(classroomDto));
        assertTrue(freeProfessors.contains(createDictionaryDto(11L, "Professor 2")));
        assertFalse(freeProfessors.contains(professorDto));
    }

}
