package ru.schedule.manager.it.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.schedule.manager.business.controller.DictionaryController;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.SimpleDictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictionaryControllerTest {

    @Mock
    private AdministeredDictionaryService administeredDictionaryService;

    @InjectMocks
    private DictionaryController dictionaryController;

    private DictionaryDto testDictionaryDto;

    private AdministeredDictionaryType testType;

    private List<AdministeredDictionaryType> testTypes;

    @BeforeEach
    void setUp() {
        testType = AdministeredDictionaryType.PROFESSOR;
        testTypes = Arrays.asList(
                AdministeredDictionaryType.PROFESSOR,
                AdministeredDictionaryType.DISCIPLINE,
                AdministeredDictionaryType.CLASSROOM
        );

        testDictionaryDto = DictionaryDto.builder()
                .id(1L)
                .type(testType.name())
                .key("TEST_KEY")
                .value("TEST_VALUE")
                .active(true)
                .displayOrder(1)
                .build();
    }

    @Test
    void delete_ShouldCallService() {
        // Действие
        dictionaryController.delete(testDictionaryDto);

        // Проверка
        verify(administeredDictionaryService, times(1)).delete(testDictionaryDto);
    }

    @Test
    void delete_WithNullDto_ShouldPassToService() {
        // Действие
        dictionaryController.delete(null);

        // Проверка
        verify(administeredDictionaryService, times(1)).delete(null);
    }

    @Test
    void create_ShouldReturnCreatedDto() {
        // Подготовка
        when(administeredDictionaryService.create(testDictionaryDto)).thenReturn(testDictionaryDto);

        // Действие
        final DictionaryDto result = dictionaryController.create(testDictionaryDto);

        // Проверка
        assertNotNull(result);
        assertEquals(testDictionaryDto, result);
        verify(administeredDictionaryService, times(1)).create(testDictionaryDto);
    }

    @Test
    void create_WithInvalidData_ShouldThrowException() {
        // Подготовка
        when(administeredDictionaryService.create(any())).thenThrow(new UnsupportedOperationException("Invalid data"));

        // Действие и проверка
        assertThrows(UnsupportedOperationException.class, () -> dictionaryController.create(new DictionaryDto()));
    }

    @Test
    void update_ShouldReturnUpdatedDto() {
        // Подготовка
        when(administeredDictionaryService.update(testDictionaryDto)).thenReturn(testDictionaryDto);

        // Действие
        final DictionaryDto result = dictionaryController.update(testDictionaryDto);

        // Проверка
        assertNotNull(result);
        assertEquals(testDictionaryDto, result);
        verify(administeredDictionaryService, times(1)).update(testDictionaryDto);
    }

    @Test
    void getAllByType_ShouldReturnList() {
        // Подготовка
        final List<DictionaryDto> expectedList = List.of(testDictionaryDto);
        when(administeredDictionaryService.getAllByType(testType, true)).thenReturn(expectedList);

        // Действие
        final List<DictionaryDto> result = dictionaryController.getAllByType(testType, true);

        // Проверка
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDictionaryDto, result.get(0));
        verify(administeredDictionaryService, times(1)).getAllByType(testType, true);
    }

    @Test
    void getAllByType_WithOnlyActiveFalse_ShouldPassToService() {
        // Подготовка
        when(administeredDictionaryService.getAllByType(testType, false)).thenReturn(List.of());

        // Действие
        final List<DictionaryDto> result = dictionaryController.getAllByType(testType, false);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(administeredDictionaryService, times(1)).getAllByType(testType, false);
    }

    @Test
    void getAllByType_WithDefaultParameter_ShouldUseTrue() {
        // Подготовка
        when(administeredDictionaryService.getAllByType(testType, true)).thenReturn(List.of());

        // Действие
        final List<DictionaryDto> result = dictionaryController.getAllByType(testType, true);

        // Проверка - null преобразуется в defaultValue = true
        assertNotNull(result);
        verify(administeredDictionaryService, times(1)).getAllByType(testType, true);
    }

    @Test
    void getAllByTypes_ShouldReturnMap() {
        // Подготовка
        final List<DictionaryDto> professorList = List.of(testDictionaryDto);
        final List<DictionaryDto> disciplineList = List.of(
                DictionaryDto.builder().id(2L).type("DISCIPLINE").build()
        );
        final List<DictionaryDto> classroomList = List.of();

        when(administeredDictionaryService.getAllByType(AdministeredDictionaryType.PROFESSOR, true)).thenReturn(professorList);
        when(administeredDictionaryService.getAllByType(AdministeredDictionaryType.DISCIPLINE, true)).thenReturn(disciplineList);
        when(administeredDictionaryService.getAllByType(AdministeredDictionaryType.CLASSROOM, true)).thenReturn(classroomList);

        // Действие
        final Map<AdministeredDictionaryType, List<DictionaryDto>> result =
                dictionaryController.getAllByTypes(testTypes, true);

        // Проверка
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(professorList, result.get(AdministeredDictionaryType.PROFESSOR));
        assertEquals(disciplineList, result.get(AdministeredDictionaryType.DISCIPLINE));
        assertEquals(classroomList, result.get(AdministeredDictionaryType.CLASSROOM));

        verify(administeredDictionaryService, times(1)).getAllByType(AdministeredDictionaryType.PROFESSOR, true);
        verify(administeredDictionaryService, times(1)).getAllByType(AdministeredDictionaryType.DISCIPLINE, true);
        verify(administeredDictionaryService, times(1)).getAllByType(AdministeredDictionaryType.CLASSROOM, true);
    }

    @Test
    void getAllByTypes_WithEmptyList_ShouldReturnEmptyMap() {
        // Действие
        final Map<AdministeredDictionaryType, List<DictionaryDto>> result = dictionaryController.getAllByTypes(List.of(), true);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(administeredDictionaryService, never()).getAllByType(any(), anyBoolean());
    }

    @Test
    void getByTypeAndKey_ShouldReturnDto() {
        // Подготовка
        when(administeredDictionaryService.getByTypeAndKey(testType, "TEST_KEY")).thenReturn(testDictionaryDto);

        // Действие
        final DictionaryDto result = dictionaryController.getByTypeAndKey(testType, "TEST_KEY");

        // Проверка
        assertNotNull(result);
        assertEquals(testDictionaryDto, result);
        verify(administeredDictionaryService, times(1)).getByTypeAndKey(testType, "TEST_KEY");
    }

    @Test
    void containsKey_ShouldReturnTrue() {
        // Подготовка
        when(administeredDictionaryService.containsKey(testType, "TEST_KEY")).thenReturn(true);

        // Действие
        final Boolean result = dictionaryController.containsKey(testType, "TEST_KEY");

        // Проверка
        assertTrue(result);
        verify(administeredDictionaryService, times(1)).containsKey(testType, "TEST_KEY");
    }

    @Test
    void containsKey_ShouldReturnFalse() {
        // Подготовка
        when(administeredDictionaryService.containsKey(testType, "INVALID_KEY")).thenReturn(false);

        // Действие
        final Boolean result = dictionaryController.containsKey(testType, "INVALID_KEY");

        // Проверка
        assertFalse(result);
        verify(administeredDictionaryService, times(1)).containsKey(testType, "INVALID_KEY");
    }

    @Test
    void containsValue_ShouldReturnTrue() {
        // Подготовка
        when(administeredDictionaryService.containsValue(testType, "TEST_VALUE")).thenReturn(true);

        // Действие
        final Boolean result = dictionaryController.containsValue(testType, "TEST_VALUE");

        // Проверка
        assertTrue(result);
        verify(administeredDictionaryService, times(1)).containsValue(testType, "TEST_VALUE");
    }

    @Test
    void containsValue_ShouldReturnFalse() {
        // Подготовка
        when(administeredDictionaryService.containsValue(testType, "INVALID_VALUE")).thenReturn(false);

        // Действие
        final Boolean result = dictionaryController.containsValue(testType, "INVALID_VALUE");

        // Проверка
        assertFalse(result);
        verify(administeredDictionaryService, times(1)).containsValue(testType, "INVALID_VALUE");
    }

    @Test
    void getAllTypes_ShouldReturnAllTypeKeys() {
        // Действие
        final Set<String> result = dictionaryController.getAllTypes();

        // Проверка
        assertNotNull(result);
        assertEquals(AdministeredDictionaryType.values().length, result.size());
        assertTrue(result.contains("PROFESSOR"));
        assertTrue(result.contains("DISCIPLINE"));
        assertTrue(result.contains("CLASSROOM"));
        assertTrue(result.contains("FACULTY"));
        assertTrue(result.contains("GROUP"));
    }

    @Test
    void getAll_ShouldReturnAllSimpleDictionaries() {
        // Действие
        final List<SimpleDictionary> result = dictionaryController.getAll();

        // Проверка
        assertNotNull(result);
        assertEquals(AdministeredDictionaryType.values().length, result.size());

        // Проверяем несколько значений
        assertTrue(result.stream().anyMatch(sd ->
                "PROFESSOR".equals(sd.getKey()) && "Преподаватели".equals(sd.getValue())
        ));
        assertTrue(result.stream().anyMatch(sd ->
                "DISCIPLINE".equals(sd.getKey()) && "Дисциплина".equals(sd.getValue())
        ));
        assertTrue(result.stream().anyMatch(sd ->
                "CLASSROOM".equals(sd.getKey()) && "Аудитория".equals(sd.getValue())
        ));
    }

    @Test
    void methods_ShouldHaveCorrectPreAuthorizeAnnotations() throws NoSuchMethodException {
        // Проверка delete
        final Method deleteMethod = DictionaryController.class.getMethod("delete", DictionaryDto.class);
        final PreAuthorize deleteAuth = deleteMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(deleteAuth);
        assertEquals("isAuthenticated()", deleteAuth.value());

        // Проверка create
        final Method createMethod = DictionaryController.class.getMethod("create", DictionaryDto.class);
        final PreAuthorize createAuth = createMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(createAuth);
        assertEquals("isAuthenticated()", createAuth.value());

        // Проверка update
        final Method updateMethod = DictionaryController.class.getMethod("update", DictionaryDto.class);
        final PreAuthorize updateAuth = updateMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(updateAuth);
        assertEquals("isAuthenticated()", updateAuth.value());

        // Проверка публичных методов (без аннотации)
        final Method getAllTypesMethod = DictionaryController.class.getMethod("getAllTypes");
        assertNull(getAllTypesMethod.getAnnotation(PreAuthorize.class));
    }

    @Test
    void getAllByTypes_WithOnlyActiveFalse_ShouldPassToService() {
        // Подготовка
        when(administeredDictionaryService.getAllByType(any(AdministeredDictionaryType.class), eq(false))).thenReturn(List.of());

        // Действие
        final Map<AdministeredDictionaryType, List<DictionaryDto>> result = dictionaryController.getAllByTypes(testTypes, false);

        // Проверка
        assertNotNull(result);
        verify(administeredDictionaryService, times(3)).getAllByType(any(AdministeredDictionaryType.class), eq(false));
    }

}
