package ru.schedule.manager.it.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.business.dataholder.ScheduleDataHolder;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.entity.ScheduleItem;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.repository.ScheduleItemRepository;
import ru.schedule.manager.business.request.GetScheduleRequest;
import ru.schedule.manager.business.service.ExcelService;
import ru.schedule.manager.business.service.ScheduleService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;
import ru.schedule.manager.infrastructure.base.entity.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest2 {

    @Mock
    private AdministeredDictionaryService administeredDictionaryService;

    @Mock
    private ScheduleItemRepository scheduleItemRepository;

    @Mock
    private ExcelService excelService;

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleItemDto testScheduleItemDto;
    private ScheduleItem testScheduleItem;
    private Dictionary testDictionary;
    private DictionaryDto testDictionaryDto;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = Employee.builder()
                .fullName("Test User")
                .build();

        testDictionary = Dictionary.builder()
                .id(1L)
                .displayOrder(1)
                .build();

        testDictionaryDto = DictionaryDto.builder()
                .id(1L)
                .displayOrder(1)
                .build();

        testScheduleItem = ScheduleItem.builder()
                .id(1L)
                .row(0)
                .col(0)
                .professor(testDictionary)
                .classroom(testDictionary)
                .discipline(testDictionary)
                .disciplineType(testDictionary)
                .times(testDictionary)
                .faculty(testDictionary)
                .group(testDictionary)
                .subgroup(testDictionary)
                .semester(testDictionary)
                .createdByEmployee(testEmployee)
                .updatedByEmployee(testEmployee)
                .build();

        testScheduleItemDto = ScheduleItemDto.builder()
                .id(1L)
                .row(0)
                .col(0)
                .professor(testDictionaryDto)
                .classroom(testDictionaryDto)
                .discipline(testDictionaryDto)
                .disciplineType(testDictionaryDto)
                .times(testDictionaryDto)
                .faculty(testDictionaryDto)
                .group(testDictionaryDto)
                .subgroup(testDictionaryDto)
                .semester(testDictionaryDto)
                .build();
    }

    @Test
    void fromEntity_ShouldConvertToDto() {
        // Act
        final ScheduleItemDto result = scheduleService.fromEntity(testScheduleItem);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testScheduleItem.getId());
        assertThat(result.getRow()).isEqualTo(testScheduleItem.getRow());
        assertThat(result.getCol()).isEqualTo(testScheduleItem.getCol());
        assertThat(result.getCreatedBy()).isEqualTo("Test User");
        assertThat(result.getUpdatedBy()).isEqualTo("Test User");
        verify(administeredDictionaryService, times(9)).dictionaryEntityToDto(any(Dictionary.class));
    }

    @Test
    void fromEntity_WithNullCreatedByEmployee_ShouldReturnNullCreatedBy() {
        // Arrange
        testScheduleItem.setCreatedByEmployee(null);

        // Act
        final ScheduleItemDto result = scheduleService.fromEntity(testScheduleItem);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCreatedBy()).isNull();
    }

    @Test
    void findById_WhenExists_ShouldReturnDto() {
        // Arrange
        when(scheduleItemRepository.findById(1L)).thenReturn(Optional.of(testScheduleItem));

        // Act
        final ScheduleItemDto result = scheduleService.findById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(scheduleItemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> scheduleService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ScheduleItem");
    }

    @Test
    void update_ShouldUpdateAndReturnDto() {
        // Arrange
        when(scheduleItemRepository.findById(1L)).thenReturn(Optional.of(testScheduleItem));
        when(administeredDictionaryService.getOneAsEntity(any(DictionaryDto.class))).thenReturn(testDictionary);

        // Act
        final ScheduleItemDto result = scheduleService.update(testScheduleItemDto);

        // Assert
        assertThat(result).isNotNull();
        verify(scheduleItemRepository).findById(1L);
        verify(administeredDictionaryService, times(8)).getOneAsEntity(any(DictionaryDto.class));
    }

    @Test
    void create_WithValidData_ShouldCreateAndReturnDto() {
        // Arrange
        when(administeredDictionaryService.getOneAsEntity(any(DictionaryDto.class))).thenReturn(testDictionary);
        when(scheduleItemRepository.save(any(ScheduleItem.class))).thenReturn(testScheduleItem);

        // Act
        final ScheduleItemDto result = scheduleService.create(testScheduleItemDto);

        // Assert
        assertThat(result).isNotNull();
        verify(scheduleItemRepository).save(any(ScheduleItem.class));
    }

    @Test
    void create_WithNullRequiredFields_ShouldReturnDtoWithoutCreating() {
        // Arrange
        testScheduleItemDto.setClassroom(null);

        // Act
        final ScheduleItemDto result = scheduleService.create(testScheduleItemDto);

        // Assert
        assertThat(result).isEqualTo(testScheduleItemDto);
        verify(scheduleItemRepository, never()).save(any());
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        // Arrange
        when(scheduleItemRepository.findById(1L)).thenReturn(Optional.of(testScheduleItem));

        // Act
        scheduleService.delete(testScheduleItemDto);

        // Assert
        verify(scheduleItemRepository).delete(testScheduleItem);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(scheduleItemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> scheduleService.delete(testScheduleItemDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getSchedule_WithProfessor_ShouldReturnSchedule() {
        // Arrange
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(testDictionaryDto);
        request.setProfessor(testDictionaryDto);

        final Map<Integer, Dictionary> timesMap = createTimesMap();
        when(administeredDictionaryService.getAllEntitiesByType(eq(AdministeredDictionaryType.LESSON_TIME), eq(true)))
                .thenReturn(new ArrayList<>(timesMap.values()));
        when(administeredDictionaryService.getOneAsEntity(any(DictionaryDto.class))).thenReturn(testDictionary);
        when(scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndProfessor(
                anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(Optional.of(testScheduleItem));

        // Act
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRows()).hasSize(2);
    }

    @Test
    void findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup_WhenExists_ShouldReturnDto() {
        // Arrange
        when(scheduleItemRepository.getMaxId()).thenReturn(Optional.of(0L));
        when(scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubgroup(
                anyInt(), anyInt(), any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(testScheduleItem));

        // Act
        final ScheduleItemDto result = scheduleService.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(
                0, 0, testDictionary, testDictionary, testDictionary, testDictionary, testDictionary, true);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findByRowAndColAndTimesAndSemesterAndProfessor_WhenExists_ShouldReturnDto() {
        // Arrange
        when(scheduleItemRepository.getMaxId()).thenReturn(Optional.of(0L));
        when(scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndProfessor(
                anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(Optional.of(testScheduleItem));

        // Act
        final ScheduleItemDto result = scheduleService.findByRowAndColAndTimesAndSemesterAndProfessor(
                0, 0, testDictionary, testDictionary, testDictionary, true);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup_WhenNotExists_ShouldCreateNew() {
        // Arrange
        when(scheduleItemRepository.getMaxId()).thenReturn(Optional.of(0L));
        when(scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubgroup(
                anyInt(), anyInt(), any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubgroup(
                anyInt(), anyInt(), any(), any(), any(), any(), eq(ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary.defaultSubgroup())))
                .thenReturn(Optional.empty());
        when(scheduleItemRepository.findByRowAndColAndTimesAndSemesterAndFacultyAndGroup(
                anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        // Act
        final ScheduleItemDto result = scheduleService.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(
                0, 0, testDictionary, testDictionary, testDictionary, testDictionary, testDictionary, true);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotEqualTo(1L);
    }

    @Test
    void isPresent_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(scheduleItemRepository.existsById(1L)).thenReturn(true);

        // Act
        final boolean result = scheduleService.isPresent(testScheduleItemDto);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void getFreeClassRoomsAndProfessors_ShouldReturnFreeResources() {
        // Arrange
        final List<DictionaryDto> allClassRooms = Arrays.asList(
                DictionaryDto.builder().id(1L).build(),
                DictionaryDto.builder().id(2L).build()
        );
        final List<DictionaryDto> allProfessors = Arrays.asList(
                DictionaryDto.builder().id(1L).build(),
                DictionaryDto.builder().id(2L).build()
        );
        final List<ScheduleItem> busySchedule = Collections.singletonList(testScheduleItem);

        when(administeredDictionaryService.getAllByType(AdministeredDictionaryType.CLASSROOM, true))
                .thenReturn(allClassRooms);
        when(administeredDictionaryService.getAllByType(AdministeredDictionaryType.PROFESSOR, true))
                .thenReturn(allProfessors);
        when(scheduleItemRepository.findAllByRowAndColAndTimesAndSemesterAndClassroomIsNotNullAndProfessorIsNotNull(
                anyInt(), anyInt(), any(), any()))
                .thenReturn(busySchedule);
        when(administeredDictionaryService.getOneAsEntity(any(DictionaryDto.class))).thenReturn(testDictionary);
        when(administeredDictionaryService.fromEntity(any(Dictionary.class))).thenReturn(testDictionaryDto);

        // Act
        final Map<AdministeredDictionaryType, List<DictionaryDto>> result =
                scheduleService.getFreeClassRoomsAndProfessors(testScheduleItemDto);

        // Assert
        assertThat(result).containsKeys(AdministeredDictionaryType.CLASSROOM, AdministeredDictionaryType.PROFESSOR);
        assertThat(result.get(AdministeredDictionaryType.CLASSROOM)).hasSize(1);
        assertThat(result.get(AdministeredDictionaryType.PROFESSOR)).hasSize(1);
    }

    @Test
    void getAllSchedule_WhenNoSchedulesExist_ShouldThrowException() {
        // Arrange
        when(scheduleItemRepository.getExistedSchedules()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> scheduleService.getAllSchedule())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Не найдено ни одного сохраненного расписания");
    }

    private Map<Integer, Dictionary> createTimesMap() {
        final Map<Integer, Dictionary> timesMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            final Dictionary dict = new Dictionary();
            dict.setDisplayOrder(i);
            timesMap.put(i, dict);
        }
        return timesMap;
    }

}
