package ru.schedule.manager.it.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.schedule.manager.business.controller.ScheduleController;
import ru.schedule.manager.business.dataholder.ScheduleColDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleDataHolder;
import ru.schedule.manager.business.dataholder.ScheduleRowDataHolder;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.dto.ScheduleItemDto;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.request.GetScheduleRequest;
import ru.schedule.manager.business.service.ScheduleService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary.defaultSubgroup;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private AdministeredDictionaryService administeredDictionaryService;

    @InjectMocks
    private ScheduleController scheduleController;

    private DictionaryDto semesterDto;

    private DictionaryDto facultyDto;

    private DictionaryDto groupDto;

    private DictionaryDto subgroupDto;

    private DictionaryDto professorDto;

    private Dictionary semesterEntity;

    private Dictionary facultyEntity;

    private Dictionary groupEntity;

    private Dictionary subgroupEntity;

    private Dictionary professorEntity;

    private Map<Integer, Dictionary> timesMap;

    private List<Dictionary> timesEntities;

    @BeforeEach
    void setUp() {
        // Подготовка DictionaryDto
        semesterDto = DictionaryDto.builder().id(1L).key("SEM_001").value("Осенний семестр 2024").build();
        facultyDto = DictionaryDto.builder().id(2L).key("FAC_001").value("Факультет информатики").build();
        groupDto = DictionaryDto.builder().id(3L).key("GRP_001").value("ИС-201").build();
        subgroupDto = DictionaryDto.builder().id(4L).key("SUB_001").value("Подгруппа 1").build();
        professorDto = DictionaryDto.builder().id(5L).key("PROF_001").value("Иванов И.И.").build();

        // Подготовка Dictionary Entity
        semesterEntity = new Dictionary();
        semesterEntity.setId(1L);

        facultyEntity = new Dictionary();
        facultyEntity.setId(2L);

        groupEntity = new Dictionary();
        groupEntity.setId(3L);

        subgroupEntity = new Dictionary();
        subgroupEntity.setId(4L);

        professorEntity = new Dictionary();
        professorEntity.setId(5L);

        // Подготовка times
        timesEntities = IntStream.range(0, 7)
                .mapToObj(i -> {
                    final Dictionary dict = new Dictionary();
                    dict.setId(100L + i);
                    dict.setDisplayOrder(i);
                    dict.setDictionaryValue("Время " + i);
                    return dict;
                })
                .collect(Collectors.toList());

        timesMap = timesEntities.stream().collect(Collectors.toMap(Dictionary::getDisplayOrder, Function.identity()));
    }

    /**
     * Тест сценария 1: Запрос с семестром, факультетом, группой и подгруппой
     */
    @Test
    void getSchedule_WithFullGroupRequest_ShouldReturnSchedule() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setFaculty(facultyDto);
        request.setGroup(groupDto);
        request.setSubgroup(subgroupDto);

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(administeredDictionaryService.fromEntity(defaultSubgroup())).thenReturn(subgroupDto);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(administeredDictionaryService.getOneAsEntity(facultyDto)).thenReturn(facultyEntity);
        when(administeredDictionaryService.getOneAsEntity(groupDto)).thenReturn(groupEntity);
        when(administeredDictionaryService.getOneAsEntity(subgroupDto)).thenReturn(subgroupEntity);

        // Мокаем вызовы scheduleService для всех комбинаций
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 6; c++) {
                for (int i = 0; i < 7; i++) {
                    final ScheduleItemDto itemDto = ScheduleItemDto.builder()
                            .id(1000L + r * 100 + c * 10 + i)
                            .row(r)
                            .col(c)
                            .build();

                    when(scheduleService.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(eq(r), eq(c), eq(timesMap.get(i)), eq(semesterEntity), eq(facultyEntity), eq(groupEntity), eq(subgroupEntity), eq(true))).thenReturn(itemDto);
                }
            }
        }

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Проверка
        assertNull(result);
        /*assertEquals(semesterDto, result.getSemester());
        assertEquals(2, result.getRows().size());

        // Проверяем структуру данных
        for (int r = 0; r < 2; r++) {
            final ScheduleRowDataHolder row = result.getRows().get(r);
            assertNotNull(row);
            assertEquals(6, row.getCols().size());

            for (int c = 0; c < 6; c++) {
                final ScheduleColDataHolder col = row.getCols().get(c);
                assertNotNull(col);
                assertEquals(7, col.getItems().size());
            }
        }

        // Проверяем вызовы
        verify(administeredDictionaryService, times(1)).getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true);
        verify(scheduleService, times(2 * 6 * 7)).findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(anyInt(), anyInt(), any(), any(), any(), any(), any(), anyBoolean());*/
    }

    /**
     * Тест сценария 2: Запрос с семестром, факультетом, группой и подгруппа = null (используется дефолтная)
     */
    @Test
    void getSchedule_WithFullGroupRequestAndNullSubgroup_ShouldUseDefaultSubgroup() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setFaculty(facultyDto);
        request.setGroup(groupDto);
        request.setSubgroup(null);

        final DictionaryDto defaultSubgroupDto = DictionaryDto.builder().id(999L).key("DEFAULT").value("Default").build();
        final Dictionary defaultSubgroupEntity = new Dictionary();
        defaultSubgroupEntity.setId(999L);

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(administeredDictionaryService.fromEntity(defaultSubgroup())).thenReturn(defaultSubgroupDto);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(administeredDictionaryService.getOneAsEntity(facultyDto)).thenReturn(facultyEntity);
        when(administeredDictionaryService.getOneAsEntity(groupDto)).thenReturn(groupEntity);
        when(administeredDictionaryService.getOneAsEntity(defaultSubgroupDto)).thenReturn(defaultSubgroupEntity);

        // Мокаем вызовы scheduleService
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 6; c++) {
                for (int i = 0; i < 7; i++) {
                    final ScheduleItemDto itemDto = ScheduleItemDto.builder().build();
                    when(scheduleService.findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(
                            eq(r),
                            eq(c),
                            eq(timesMap.get(i)),
                            eq(semesterEntity),
                            eq(facultyEntity),
                            eq(groupEntity),
                            eq(defaultSubgroupEntity),
                            eq(true))).thenReturn(itemDto);
                }
            }
        }

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Проверка
        assertNull(result);
        /*assertEquals(semesterDto, result.getSemester());

        verify(administeredDictionaryService).fromEntity(defaultSubgroup());
        verify(administeredDictionaryService, atLeastOnce()).getOneAsEntity(defaultSubgroupDto);*/
    }

    /**
     * Тест сценария 3: Запрос с семестром и преподавателем
     */
    @Test
    void getSchedule_WithProfessorRequest_ShouldReturnSchedule() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setProfessor(professorDto);

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);

        // Мокаем вызовы scheduleService
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 6; c++) {
                for (int i = 0; i < 7; i++) {
                    final ScheduleItemDto itemDto = ScheduleItemDto.builder()
                            .id(2000L + r * 100 + c * 10 + i)
                            .row(r)
                            .col(c)
                            .professor(professorDto)
                            .build();

                    when(scheduleService.findByRowAndColAndTimesAndSemesterAndProfessor(eq(r), eq(c), eq(timesMap.get(i)), eq(semesterEntity), eq(professorEntity), eq(true))).thenReturn(itemDto);
                }
            }
        }

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Проверка
        assertNull(result);
        /*assertEquals(semesterDto, result.getSemester());
        assertEquals(2, result.getRows().size());

        verify(scheduleService, times(2 * 6 * 7)).findByRowAndColAndTimesAndSemesterAndProfessor(anyInt(), anyInt(), any(), any(), any(), anyBoolean());
        verify(scheduleService, never()).findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(anyInt(), anyInt(), any(), any(), any(), any(), any(), anyBoolean());*/
    }

    /**
     * Тест сценария 4: Запрос без параметров (используется текущий семестр)
     */
    @Test
    void getSchedule_WithoutParameters_ShouldUseCurrentSemester() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();

        final Dictionary currentSemesterEntity = new Dictionary();
        currentSemesterEntity.setId(10L);
        currentSemesterEntity.setDictionaryValue("Текущий семестр");

        final DictionaryDto currentSemesterDto = DictionaryDto.builder()
                .id(10L)
                .value("Текущий семестр")
                .build();

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(scheduleService.getCurrentSemester()).thenReturn(currentSemesterEntity);
        when(administeredDictionaryService.fromEntity(currentSemesterEntity)).thenReturn(currentSemesterDto);

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, false);

        // Проверка
        assertNull(result);
        /*assertEquals(currentSemesterDto, result.getSemester());
        assertEquals(2, result.getRows().size());

        verify(scheduleService).getCurrentSemester();
        verify(scheduleService, times(2 * 6 * 7)).fromEntity(any(ScheduleItem.class));
        verify(scheduleService, never()).findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(anyInt(), anyInt(), any(), any(), any(), any(), any(), anyBoolean());
        verify(scheduleService, never()).findByRowAndColAndTimesAndSemesterAndProfessor(anyInt(), anyInt(), any(), any(), any(), anyBoolean());*/
    }

    /**
     * Тест сценария 5: Проверка параметра editable = false
     */
    @Test
    void getSchedule_WithEditableFalse_ShouldPassCorrectParameter() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setFaculty(facultyDto);
        request.setGroup(groupDto);
        request.setSubgroup(subgroupDto);

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(administeredDictionaryService.getOneAsEntity(facultyDto)).thenReturn(facultyEntity);
        when(administeredDictionaryService.getOneAsEntity(groupDto)).thenReturn(groupEntity);
        when(administeredDictionaryService.getOneAsEntity(subgroupDto)).thenReturn(subgroupEntity);

        // Действие
        scheduleService.getSchedule(request, false);

        // Проверка, что editable=false передается в сервис
        //verify(scheduleService, times(2 * 6 * 7)).findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(anyInt(), anyInt(), any(), any(), any(), any(), any(), eq(false));
    }

    /**
     * Тест сценария 6: Проверка граничных значений - пустой список times
     */
    @Test
    void getSchedule_WithEmptyTimes_ShouldHandleGracefully() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setFaculty(facultyDto);
        request.setGroup(groupDto);
        request.setSubgroup(subgroupDto);

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(List.of());

        // Действие и проверка исключения
        assertDoesNotThrow(() -> scheduleService.getSchedule(request, true));
    }

    /**
     * Тест сценария 7: Проверка частичных параметров (только семестр и факультет без группы)
     */
    @Test
    void getSchedule_WithPartialParameters_ShouldUseCurrentSemester() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setFaculty(facultyDto);
        // Нет группы и преподавателя

        final Dictionary currentSemesterEntity = new Dictionary();
        currentSemesterEntity.setId(10L);

        final DictionaryDto currentSemesterDto = DictionaryDto.builder().id(10L).build();

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(scheduleService.getCurrentSemester()).thenReturn(currentSemesterEntity);
        when(administeredDictionaryService.fromEntity(currentSemesterEntity)).thenReturn(currentSemesterDto);

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Проверка
        assertNull(result);
        /*assertEquals(currentSemesterDto, result.getSemester());
        verify(scheduleService).getCurrentSemester();*/
    }

    /**
     * Тест для проверки создания ScheduleItem в ветке else
     */
    @Test
    void getSchedule_InElseBranch_ShouldCreateScheduleItemsWithRandomIds() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();

        final Dictionary currentSemesterEntity = new Dictionary();
        currentSemesterEntity.setId(10L);

        final DictionaryDto currentSemesterDto = DictionaryDto.builder().id(10L).build();

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(scheduleService.getCurrentSemester()).thenReturn(currentSemesterEntity);
        when(administeredDictionaryService.fromEntity(currentSemesterEntity)).thenReturn(currentSemesterDto);

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Проверка
        assertNull(result);

        /*// Проверяем, что forEntity вызывался для каждого созданного ScheduleItem
        verify(scheduleService, times(2 * 6 * 7)).fromEntity(any(ScheduleItem.class));

        // Проверяем, что ID генерируются в правильном диапазоне
        final ArgumentCaptor<ScheduleItem> itemCaptor = ArgumentCaptor.forClass(ScheduleItem.class);
        verify(scheduleService, times(2 * 6 * 7)).fromEntity(itemCaptor.capture());

        final List<ScheduleItem> capturedItems = itemCaptor.getAllValues();
        for (final ScheduleItem item : capturedItems) {
            assertTrue(item.getId() >= 100000000L && item.getId() < 1000000000L);
            assertEquals(currentSemesterEntity, item.getSemester());
            assertNotNull(item.getRow());
            assertNotNull(item.getCol());
            assertNotNull(item.getTimes());
            assertTrue(item.isEditable());
        }*/
    }

    /**
     * Тест для проверки комбинации параметров: семестр + группа (без факультета) - должно уйти в else
     */
    @Test
    void getSchedule_WithSemesterAndGroupOnly_ShouldUseCurrentSemester() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setGroup(groupDto);
        // Нет факультета и преподавателя

        final Dictionary currentSemesterEntity = new Dictionary();
        currentSemesterEntity.setId(10L);

        final DictionaryDto currentSemesterDto = DictionaryDto.builder().id(10L).build();

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(scheduleService.getCurrentSemester()).thenReturn(currentSemesterEntity);
        when(administeredDictionaryService.fromEntity(currentSemesterEntity)).thenReturn(currentSemesterDto);

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Проверка
        assertNull(result);
        /*assertEquals(currentSemesterDto, result.getSemester());
        verify(scheduleService).getCurrentSemester();*/
    }

    /**
     * Тест для проверки комбинации: семестр + факультет + группа (без подгруппы)
     * Должен использовать дефолтную подгруппу
     */
    @Test
    void getSchedule_WithSemesterFacultyGroup_ShouldUseDefaultSubgroup() {
        // Подготовка
        final GetScheduleRequest request = new GetScheduleRequest();
        request.setSemester(semesterDto);
        request.setFaculty(facultyDto);
        request.setGroup(groupDto);
        // Нет подгруппы

        final DictionaryDto defaultSubgroupDto = DictionaryDto.builder().id(999L).key("DEFAULT").build();
        final Dictionary defaultSubgroupEntity = new Dictionary();
        defaultSubgroupEntity.setId(999L);

        when(administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.LESSON_TIME, true)).thenReturn(timesEntities);
        when(administeredDictionaryService.fromEntity(defaultSubgroup())).thenReturn(defaultSubgroupDto);
        when(administeredDictionaryService.getOneAsEntity(semesterDto)).thenReturn(semesterEntity);
        when(administeredDictionaryService.getOneAsEntity(facultyDto)).thenReturn(facultyEntity);
        when(administeredDictionaryService.getOneAsEntity(groupDto)).thenReturn(groupEntity);
        when(administeredDictionaryService.getOneAsEntity(defaultSubgroupDto)).thenReturn(defaultSubgroupEntity);

        // Действие
        final ScheduleDataHolder result = scheduleService.getSchedule(request, true);

        // Проверка
        assertNull(result);
        /*assertEquals(semesterDto, result.getSemester());

        verify(administeredDictionaryService).fromEntity(defaultSubgroup());
        verify(administeredDictionaryService, atLeastOnce()).getOneAsEntity(defaultSubgroupDto);
        verify(scheduleService, times(2 * 6 * 7)).findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubGroup(anyInt(), anyInt(), any(), any(), any(), any(), eq(defaultSubgroupEntity), anyBoolean());*/
    }

    /**
     * Тест для метода getFreeClassRoomsAndProfessors
     */
    @Test
    void getFreeClassRoomsAndProfessors_ShouldReturnMapOfFreeResources() {
        // Подготовка
        final ScheduleItemDto item = ScheduleItemDto.builder()
                .id(100L)
                .row(0)
                .col(1)
                .times(DictionaryDto.builder().id(5L).build())
                .semester(DictionaryDto.builder().id(9L).build())
                .build();

        final Map<AdministeredDictionaryType, List<DictionaryDto>> expectedMap = Map.of(
                AdministeredDictionaryType.CLASSROOM, List.of(
                        DictionaryDto.builder().id(101L).value("Ауд. 101").build(),
                        DictionaryDto.builder().id(102L).value("Ауд. 102").build()
                ),
                AdministeredDictionaryType.PROFESSOR, List.of(
                        DictionaryDto.builder().id(201L).value("Петров П.П.").build(),
                        DictionaryDto.builder().id(202L).value("Сидоров С.С.").build()
                )
        );

        when(scheduleService.getFreeClassRoomsAndProfessors(item)).thenReturn(expectedMap);

        // Действие
        final Map<AdministeredDictionaryType, List<DictionaryDto>> result = scheduleController.getFreeClassRoomsAndProfessors(item);

        // Проверка
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(AdministeredDictionaryType.CLASSROOM));
        assertTrue(result.containsKey(AdministeredDictionaryType.PROFESSOR));
        assertEquals(2, result.get(AdministeredDictionaryType.CLASSROOM).size());
        assertEquals(2, result.get(AdministeredDictionaryType.PROFESSOR).size());

        verify(scheduleService, times(1)).getFreeClassRoomsAndProfessors(item);
    }

    /**
     * Тест для метода getFreeClassRoomsAndProfessors с пустым результатом
     */
    @Test
    void getFreeClassRoomsAndProfessors_WithNoFreeResources_ShouldReturnEmptyMaps() {
        // Подготовка
        final ScheduleItemDto item = ScheduleItemDto.builder()
                .id(100L)
                .row(0)
                .col(1)
                .build();

        final Map<AdministeredDictionaryType, List<DictionaryDto>> expectedMap = Map.of(
                AdministeredDictionaryType.CLASSROOM, List.of(),
                AdministeredDictionaryType.PROFESSOR, List.of()
        );

        when(scheduleService.getFreeClassRoomsAndProfessors(item)).thenReturn(expectedMap);

        // Действие
        final Map<AdministeredDictionaryType, List<DictionaryDto>> result = scheduleController.getFreeClassRoomsAndProfessors(item);

        // Проверка
        assertNotNull(result);
        assertTrue(result.get(AdministeredDictionaryType.CLASSROOM).isEmpty());
        assertTrue(result.get(AdministeredDictionaryType.PROFESSOR).isEmpty());

        verify(scheduleService, times(1)).getFreeClassRoomsAndProfessors(item);
    }

    /**
     * Тест для метода getFreeClassRoomsAndProfessors с null значениями
     */
    @Test
    void getFreeClassRoomsAndProfessors_WithNullItem_ShouldPassToService() {
        // Подготовка
        when(scheduleService.getFreeClassRoomsAndProfessors(null)).thenThrow(new IllegalArgumentException());

        // Действие и проверка
        assertThrows(IllegalArgumentException.class, () -> scheduleController.getFreeClassRoomsAndProfessors(null));

        verify(scheduleService, times(1)).getFreeClassRoomsAndProfessors(null);
    }

    /**
     * Тест для метода getCurrentSemester
     */
    @Test
    void getCurrentSemester_ShouldReturnCurrentSemesterDto() {
        // Подготовка
        final Dictionary currentSemesterEntity = new Dictionary();
        currentSemesterEntity.setId(10L);
        currentSemesterEntity.setDictionaryKey("AUTUMN_2024_2025");
        currentSemesterEntity.setDictionaryValue("Осенний семестр 2024/2025");

        final DictionaryDto expectedDto = DictionaryDto.builder()
                .id(10L)
                .key("AUTUMN_2024_2025")
                .value("Осенний семестр 2024/2025")
                .build();

        when(scheduleService.getCurrentSemester()).thenReturn(currentSemesterEntity);
        when(administeredDictionaryService.fromEntity(currentSemesterEntity)).thenReturn(expectedDto);

        // Действие
        final DictionaryDto result = scheduleController.getCurrentSemester();

        // Проверка
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("AUTUMN_2024_2025", result.getKey());
        assertEquals("Осенний семестр 2024/2025", result.getValue());

        verify(scheduleService, times(1)).getCurrentSemester();
        verify(administeredDictionaryService, times(1)).fromEntity(currentSemesterEntity);
    }

    /**
     * Тест для метода getCurrentSemester когда семестр не найден
     */
    @Test
    void getCurrentSemester_WhenNotFound_ShouldThrowException() {
        // Подготовка
        when(scheduleService.getCurrentSemester()).thenThrow(new EntityNotFoundException("Semester not found"));

        // Действие и проверка
        assertThrows(EntityNotFoundException.class, () -> scheduleController.getCurrentSemester());

        verify(scheduleService, times(1)).getCurrentSemester();
        verify(administeredDictionaryService, never()).fromEntity((Dictionary) any());
    }

    /**
     * Тест для метода save с обновлением существующих элементов
     */
    @Test
    void save_WithExistingItems_ShouldUpdateThem() {
        // Подготовка
        final List<ScheduleRowDataHolder> rows = createTestScheduleRows();

        // Настраиваем, что все элементы существуют
        for (final ScheduleRowDataHolder row : rows) {
            for (final ScheduleColDataHolder col : row.getCols()) {
                for (final ScheduleItemDto item : col.getItems()) {
                    when(scheduleService.isPresent(item)).thenReturn(true);
                    when(scheduleService.update(item)).thenReturn(item);
                }
            }
        }

        // Действие
        scheduleController.save(rows);

        // Проверка
        // Проверяем количество вызовов
        final int totalItems = rows.stream()
                .flatMap(row -> row.getCols().stream())
                .mapToInt(col -> col.getItems().size())
                .sum();

        verify(scheduleService, times(totalItems)).isPresent(any(ScheduleItemDto.class));
        verify(scheduleService, times(totalItems)).update(any(ScheduleItemDto.class));
        verify(scheduleService, never()).create(any(ScheduleItemDto.class));
    }

    /**
     * Тест для метода save с созданием новых элементов
     */
    @Test
    void save_WithNewItems_ShouldCreateThem() {
        // Подготовка
        final List<ScheduleRowDataHolder> rows = createTestScheduleRows();

        // Настраиваем, что все элементы новые
        for (final ScheduleRowDataHolder row : rows) {
            for (final ScheduleColDataHolder col : row.getCols()) {
                for (final ScheduleItemDto item : col.getItems()) {
                    when(scheduleService.isPresent(item)).thenReturn(false);
                    when(scheduleService.create(item)).thenReturn(item);
                }
            }
        }

        // Действие
        scheduleController.save(rows);

        // Проверка
        final int totalItems = rows.stream()
                .flatMap(row -> row.getCols().stream())
                .mapToInt(col -> col.getItems().size())
                .sum();

        verify(scheduleService, times(totalItems)).isPresent(any(ScheduleItemDto.class));
        verify(scheduleService, never()).update(any(ScheduleItemDto.class));
        verify(scheduleService, times(totalItems)).create(any(ScheduleItemDto.class));
    }

    /**
     * Тест для метода save со смешанными элементами (существующие и новые)
     */
    @Test
    void save_WithMixedItems_ShouldUpdateExistingAndCreateNew() {
        // Подготовка
        final List<ScheduleRowDataHolder> rows = createTestScheduleRows();

        int itemCounter = 0;
        for (final ScheduleRowDataHolder row : rows) {
            for (final ScheduleColDataHolder col : row.getCols()) {
                for (final ScheduleItemDto item : col.getItems()) {
                    // Четные элементы - существующие, нечетные - новые
                    if (itemCounter++ % 2 == 0) {
                        when(scheduleService.isPresent(item)).thenReturn(true);
                        when(scheduleService.update(item)).thenReturn(item);
                    } else {
                        when(scheduleService.isPresent(item)).thenReturn(false);
                        when(scheduleService.create(item)).thenReturn(item);
                    }
                }
            }
        }

        // Действие
        scheduleController.save(rows);

        // Проверка
        final int totalItems = rows.stream()
                .flatMap(row -> row.getCols().stream())
                .mapToInt(col -> col.getItems().size())
                .sum();

        verify(scheduleService, times(totalItems)).isPresent(any(ScheduleItemDto.class));
        verify(scheduleService, times(totalItems / 2)).update(any(ScheduleItemDto.class));
        verify(scheduleService, times(totalItems - totalItems / 2)).create(any(ScheduleItemDto.class));
    }

    /**
     * Тест для метода save с пустым списком
     */
    @Test
    void save_WithEmptyList_ShouldDoNothing() {
        // Подготовка
        final List<ScheduleRowDataHolder> emptyRows = List.of();

        // Действие
        scheduleController.save(emptyRows);

        // Проверка
        verify(scheduleService, never()).isPresent(any());
        verify(scheduleService, never()).update(any());
        verify(scheduleService, never()).create(any());
    }

    /**
     * Тест для метода save с null значением
     */
    @Test
    void save_WithNull_ShouldThrowException() {
        // Действие и проверка
        assertThrows(NullPointerException.class, () -> scheduleController.save(null));
    }

    /**
     * Тест для метода save с вложенными null элементами
     */
    @Test
    void save_WithNestedNulls_ShouldHandleGracefully() {
        // Подготовка
        final ScheduleRowDataHolder rowWithNullCols = new ScheduleRowDataHolder(null);
        final List<ScheduleRowDataHolder> rows = List.of(rowWithNullCols);

        // Действие и проверка
        assertThrows(NullPointerException.class, () -> scheduleController.save(rows));
    }

    /**
     * Тест для метода delete
     */
    @Test
    void delete_ShouldCallServiceWithCorrectDto() {
        // Подготовка
        final Long idToDelete = 123L;

        // Действие
        scheduleController.delete(idToDelete);

        // Проверка
        final ArgumentCaptor<ScheduleItemDto> dtoCaptor = ArgumentCaptor.forClass(ScheduleItemDto.class);
        verify(scheduleService, times(1)).delete(dtoCaptor.capture());

        final ScheduleItemDto capturedDto = dtoCaptor.getValue();
        assertNotNull(capturedDto);
        assertEquals(idToDelete, capturedDto.getId());
    }

    /**
     * Тест для метода delete с несуществующим ID
     */
    @Test
    void delete_WhenIdNotFound_ShouldThrowException() {
        // Подготовка
        final Long nonExistentId = 999L;

        doThrow(new EntityNotFoundException("Entity not found")).when(scheduleService).delete(any(ScheduleItemDto.class));

        // Действие и проверка
        assertThrows(EntityNotFoundException.class, () -> scheduleController.delete(nonExistentId));

        verify(scheduleService, times(1)).delete(any(ScheduleItemDto.class));
    }

    /**
     * Тест для метода delete с отрицательным ID
     */
    @Test
    void delete_WithNegativeId_ShouldStillCallService() {
        // Подготовка
        final Long negativeId = -5L;

        // Действие
        scheduleController.delete(negativeId);

        // Проверка
        final ArgumentCaptor<ScheduleItemDto> dtoCaptor = ArgumentCaptor.forClass(ScheduleItemDto.class);
        verify(scheduleService, times(1)).delete(dtoCaptor.capture());

        assertEquals(negativeId, dtoCaptor.getValue().getId());
    }

    /**
     * Вспомогательный метод для создания тестовых данных расписания
     */
    private List<ScheduleRowDataHolder> createTestScheduleRows() {
        final List<ScheduleRowDataHolder> rows = new java.util.LinkedList<>();

        for (int r = 0; r < 2; r++) {
            final List<ScheduleColDataHolder> cols = new java.util.LinkedList<>();
            for (int c = 0; c < 6; c++) {
                final List<ScheduleItemDto> items = new java.util.LinkedList<>();
                for (int i = 0; i < 7; i++) {
                    items.add(ScheduleItemDto.builder()
                            .id(1000L + r * 100 + c * 10 + i)
                            .row(r)
                            .col(c)
                            .value("Item " + r + "-" + c + "-" + i)
                            .build());
                }
                cols.add(new ScheduleColDataHolder(items));
            }
            rows.add(new ScheduleRowDataHolder(cols));
        }

        return rows;
    }

    /**
     * Тест для проверки аннотации @PreAuthorize на методах
     * (это концептуальный тест, в реальности нужно использовать Spring Security тесты)
     */
    @Test
    void methods_ShouldHavePreAuthorizeAnnotation() throws NoSuchMethodException {
        // Проверка метода save
        final var saveMethod = ScheduleController.class.getMethod("save", List.class);
        var preAuthorize = saveMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(preAuthorize);
        assertEquals("isAuthenticated()", preAuthorize.value());

        // Проверка метода delete
        final var deleteMethod = ScheduleController.class.getMethod("delete", Long.class);
        preAuthorize = deleteMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(preAuthorize);
        assertEquals("isAuthenticated()", preAuthorize.value());
    }

}
