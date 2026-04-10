package ru.schedule.manager.it.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.schedule.manager.business.controller.ProfessorController;
import ru.schedule.manager.business.dto.ProfessorDepartmentLnkDto;
import ru.schedule.manager.business.dto.ProfessorDisciplineLnkDto;
import ru.schedule.manager.business.request.AddProfessorDisciplinesRequest;
import ru.schedule.manager.business.service.ProfessorDepartmentService;
import ru.schedule.manager.business.service.ProfessorDisciplineService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessorControllerTest {

    @Mock
    private ProfessorDisciplineService professorDisciplineService;

    @Mock
    private ProfessorDepartmentService professorDepartmentService;

    @InjectMocks
    private ProfessorController professorController;

    @Captor
    private ArgumentCaptor<ProfessorDisciplineLnkDto> disciplineLnkCaptor;

    private DictionaryDto professorDto;

    private DictionaryDto disciplineDto1;

    private DictionaryDto disciplineDto2;

    private DictionaryDto departmentDto;

    private ProfessorDisciplineLnkDto disciplineLnkDto;

    private ProfessorDepartmentLnkDto departmentLnkDto;

    private AddProfessorDisciplinesRequest addRequest;

    @BeforeEach
    void setUp() {
        professorDto = DictionaryDto.builder()
                .id(1L)
                .key("PROF_001")
                .value("Иванов И.И.")
                .build();

        disciplineDto1 = DictionaryDto.builder()
                .id(2L)
                .key("DISC_001")
                .value("Математика")
                .build();

        disciplineDto2 = DictionaryDto.builder()
                .id(3L)
                .key("DISC_002")
                .value("Физика")
                .build();

        departmentDto = DictionaryDto.builder()
                .id(4L)
                .key("DEP_001")
                .value("Кафедра информатики")
                .build();

        disciplineLnkDto = ProfessorDisciplineLnkDto.builder()
                .id(100L)
                .professor(professorDto)
                .discipline(disciplineDto1)
                .build();

        departmentLnkDto = ProfessorDepartmentLnkDto.builder()
                .id(200L)
                .professor(professorDto)
                .department(departmentDto)
                .build();

        addRequest = new AddProfessorDisciplinesRequest();
        addRequest.setProfessor(professorDto);
        addRequest.setDisciplines(Arrays.asList(disciplineDto1, disciplineDto2));
    }

    @Test
    void getProfessorDisciplines_ShouldReturnList() {
        // Подготовка
        final List<ProfessorDisciplineLnkDto> expectedList = List.of(disciplineLnkDto);
        when(professorDisciplineService.getProfessorDisciplines(professorDto)).thenReturn(expectedList);

        // Действие
        final List<ProfessorDisciplineLnkDto> result = professorController.getProfessorDisciplines(professorDto);

        // Проверка
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(disciplineLnkDto, result.get(0));
        verify(professorDisciplineService, times(1)).getProfessorDisciplines(professorDto);
    }

    @Test
    void getProfessorDisciplines_WithNullProfessor_ShouldPassToService() {
        // Подготовка
        when(professorDisciplineService.getProfessorDisciplines(null)).thenReturn(List.of());

        // Действие
        final List<ProfessorDisciplineLnkDto> result = professorController.getProfessorDisciplines(null);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(professorDisciplineService, times(1)).getProfessorDisciplines(null);
    }

    @Test
    void getProfessorDisciplines_WithNoDisciplines_ShouldReturnEmptyList() {
        // Подготовка
        when(professorDisciplineService.getProfessorDisciplines(professorDto)).thenReturn(List.of());

        // Действие
        final List<ProfessorDisciplineLnkDto> result = professorController.getProfessorDisciplines(professorDto);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addProfessorDiscipline_ShouldCreateForEachDiscipline() {
        // Подготовка
        when(professorDisciplineService.create(any(ProfessorDisciplineLnkDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Действие
        professorController.addProfessorDiscipline(addRequest);

        // Проверка
        verify(professorDisciplineService, times(2)).create(disciplineLnkCaptor.capture());

        final List<ProfessorDisciplineLnkDto> captured = disciplineLnkCaptor.getAllValues();
        assertEquals(2, captured.size());

        // Проверяем первый вызов
        assertEquals(professorDto, captured.get(0).getProfessor());
        assertEquals(disciplineDto1, captured.get(0).getDiscipline());

        // Проверяем второй вызов
        assertEquals(professorDto, captured.get(1).getProfessor());
        assertEquals(disciplineDto2, captured.get(1).getDiscipline());
    }

    @Test
    void addProfessorDiscipline_WithEmptyDisciplinesList_ShouldDoNothing() {
        // Подготовка
        addRequest.setDisciplines(List.of());

        // Действие
        professorController.addProfessorDiscipline(addRequest);

        // Проверка
        verify(professorDisciplineService, never()).create(any());
    }

    @Test
    void addProfessorDiscipline_WithNullDisciplines_ShouldThrowException() {
        // Подготовка
        addRequest.setDisciplines(null);

        // Действие и проверка
        assertThrows(NullPointerException.class, () -> professorController.addProfessorDiscipline(addRequest));
    }

    @Test
    void addProfessorDiscipline_WhenServiceThrowsException_ShouldPropagate() {
        // Подготовка
        when(professorDisciplineService.create(any())).thenThrow(new RuntimeException("Database error"));

        // Действие и проверка
        assertThrows(RuntimeException.class, () -> professorController.addProfessorDiscipline(addRequest));
    }

    @Test
    void deleteProfessorDiscipline_ShouldCallServiceWithId() {
        // Подготовка
        final Long idToDelete = 123L;

        // Действие
        professorController.deleteProfessorDiscipline(idToDelete);

        // Проверка
        final ArgumentCaptor<ProfessorDisciplineLnkDto> captor = ArgumentCaptor.forClass(ProfessorDisciplineLnkDto.class);
        verify(professorDisciplineService, times(1)).delete(captor.capture());

        final ProfessorDisciplineLnkDto captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(idToDelete, captured.getId());
        assertNull(captured.getProfessor());
        assertNull(captured.getDiscipline());
    }

    @Test
    void deleteProfessorDiscipline_WithNegativeId_ShouldStillCallService() {
        // Подготовка
        final Long negativeId = -5L;

        // Действие
        professorController.deleteProfessorDiscipline(negativeId);

        // Проверка
        final ArgumentCaptor<ProfessorDisciplineLnkDto> captor = ArgumentCaptor.forClass(ProfessorDisciplineLnkDto.class);
        verify(professorDisciplineService, times(1)).delete(captor.capture());

        assertEquals(negativeId, captor.getValue().getId());
    }

    @Test
    void deleteProfessorDiscipline_WhenServiceThrowsException_ShouldPropagate() {
        // Подготовка
        doThrow(new RuntimeException("Delete failed")).when(professorDisciplineService).delete(any());

        // Действие и проверка
        assertThrows(RuntimeException.class, () -> professorController.deleteProfessorDiscipline(123L));
    }

    @Test
    void getProfessorDepartment_ShouldReturnDepartment() {
        // Подготовка
        when(professorDepartmentService.getProfessorDepartment(professorDto)).thenReturn(departmentLnkDto);

        // Действие
        final ProfessorDepartmentLnkDto result = professorController.getProfessorDepartment(professorDto);

        // Проверка
        assertNotNull(result);
        assertEquals(departmentLnkDto, result);
        assertEquals(professorDto, result.getProfessor());
        assertEquals(departmentDto, result.getDepartment());
        verify(professorDepartmentService, times(1)).getProfessorDepartment(professorDto);
    }

    @Test
    void getProfessorDepartment_WhenNoDepartment_ShouldReturnNull() {
        // Подготовка
        when(professorDepartmentService.getProfessorDepartment(professorDto)).thenReturn(null);

        // Действие
        final ProfessorDepartmentLnkDto result = professorController.getProfessorDepartment(professorDto);

        // Проверка
        assertNull(result);
        verify(professorDepartmentService, times(1)).getProfessorDepartment(professorDto);
    }

    @Test
    void getProfessorDepartment_WithNullProfessor_ShouldPassToService() {
        // Подготовка
        when(professorDepartmentService.getProfessorDepartment(null)).thenReturn(null);

        // Действие
        final ProfessorDepartmentLnkDto result = professorController.getProfessorDepartment(null);

        // Проверка
        assertNull(result);
        verify(professorDepartmentService, times(1)).getProfessorDepartment(null);
    }

    @Test
    void getDepartmentProfessors_ShouldReturnListOfProfessors() {
        // Подготовка
        final ProfessorDepartmentLnkDto lnk1 = ProfessorDepartmentLnkDto.builder()
                .professor(professorDto)
                .department(departmentDto)
                .build();

        final DictionaryDto professor2 = DictionaryDto.builder().id(2L).value("Петров П.П.").build();
        final ProfessorDepartmentLnkDto lnk2 = ProfessorDepartmentLnkDto.builder()
                .professor(professor2)
                .department(departmentDto)
                .build();

        when(professorDepartmentService.getDepartmentProfessors(departmentDto))
                .thenReturn(List.of(lnk1, lnk2));

        // Действие
        final List<DictionaryDto> result = professorController.getDepartmentProfessors(departmentDto);

        // Проверка
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(professorDto, result.get(0));
        assertEquals(professor2, result.get(1));
        verify(professorDepartmentService, times(1)).getDepartmentProfessors(departmentDto);
    }

    @Test
    void getDepartmentProfessors_WithEmptyResult_ShouldReturnEmptyList() {
        // Подготовка
        when(professorDepartmentService.getDepartmentProfessors(departmentDto)).thenReturn(List.of());

        // Действие
        final List<DictionaryDto> result = professorController.getDepartmentProfessors(departmentDto);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDepartmentProfessors_WithNullDepartment_ShouldPassToService() {
        // Подготовка
        when(professorDepartmentService.getDepartmentProfessors(null)).thenReturn(List.of());

        // Действие
        final List<DictionaryDto> result = professorController.getDepartmentProfessors(null);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(professorDepartmentService, times(1)).getDepartmentProfessors(null);
    }

    @Test
    void updateDepartment_ShouldCreateAndReturnDto() {
        // Подготовка
        when(professorDepartmentService.create(departmentLnkDto)).thenReturn(departmentLnkDto);

        // Действие
        final ProfessorDepartmentLnkDto result = professorController.updateDepartment(departmentLnkDto);

        // Проверка
        assertNotNull(result);
        assertEquals(departmentLnkDto, result);
        verify(professorDepartmentService, times(1)).create(departmentLnkDto);
    }

    @Test
    void updateDepartment_WithNewProfessor_ShouldCreate() {
        // Подготовка
        final DictionaryDto newProfessor = DictionaryDto.builder().id(5L).build();
        final ProfessorDepartmentLnkDto newLnk = ProfessorDepartmentLnkDto.builder()
                .professor(newProfessor)
                .department(departmentDto)
                .build();

        when(professorDepartmentService.create(newLnk)).thenReturn(newLnk);

        // Действие
        final ProfessorDepartmentLnkDto result = professorController.updateDepartment(newLnk);

        // Проверка
        assertNotNull(result);
        assertEquals(newProfessor, result.getProfessor());
        verify(professorDepartmentService, times(1)).create(newLnk);
    }

    @Test
    void updateDepartment_WhenServiceThrowsException_ShouldPropagate() {
        // Подготовка
        when(professorDepartmentService.create(any())).thenThrow(new RuntimeException("Update failed"));

        // Действие и проверка
        assertThrows(RuntimeException.class, () -> professorController.updateDepartment(departmentLnkDto));
    }

    @Test
    void methods_ShouldHaveCorrectPreAuthorizeAnnotations() throws NoSuchMethodException {
        // Проверка addProfessorDiscipline
        final Method addMethod = ProfessorController.class.getMethod("addProfessorDiscipline", AddProfessorDisciplinesRequest.class);
        final PreAuthorize addAuth = addMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(addAuth);
        assertEquals("isAuthenticated()", addAuth.value());

        // Проверка deleteProfessorDiscipline
        final Method deleteMethod = ProfessorController.class.getMethod("deleteProfessorDiscipline", Long.class);
        final PreAuthorize deleteAuth = deleteMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(deleteAuth);
        assertEquals("isAuthenticated()", deleteAuth.value());

        // Проверка updateDepartment
        final Method updateMethod = ProfessorController.class.getMethod("updateDepartment", ProfessorDepartmentLnkDto.class);
        final PreAuthorize updateAuth = updateMethod.getAnnotation(PreAuthorize.class);
        assertNotNull(updateAuth);
        assertEquals("isAuthenticated()", updateAuth.value());

        // Проверка публичных методов (без аннотации)
        final Method getDisciplinesMethod = ProfessorController.class.getMethod("getProfessorDisciplines", DictionaryDto.class);
        assertNull(getDisciplinesMethod.getAnnotation(PreAuthorize.class));

        final Method getDepartmentMethod = ProfessorController.class.getMethod("getProfessorDepartment", DictionaryDto.class);
        assertNull(getDepartmentMethod.getAnnotation(PreAuthorize.class));

        final Method getDepartmentProfessorsMethod = ProfessorController.class.getMethod("getDepartmentProfessors", DictionaryDto.class);
        assertNull(getDepartmentProfessorsMethod.getAnnotation(PreAuthorize.class));
    }

    @Test
    void addProfessorDiscipline_WithNullProfessor_ShouldThrowException() {
        // Подготовка
        addRequest.setProfessor(null);

        // Действие и проверка
        assertDoesNotThrow(() -> professorController.addProfessorDiscipline(addRequest));
    }

    @Test
    void getProfessorDisciplines_ShouldReturnSortedList() {
        // Подготовка
        final DictionaryDto prof1 = DictionaryDto.builder().id(1L).build();
        final DictionaryDto prof2 = DictionaryDto.builder().id(2L).build();

        final List<ProfessorDisciplineLnkDto> expectedList = List.of(
                ProfessorDisciplineLnkDto.builder().professor(prof1).build(),
                ProfessorDisciplineLnkDto.builder().professor(prof2).build()
        );

        when(professorDisciplineService.getProfessorDisciplines(professorDto)).thenReturn(expectedList);

        // Действие
        final List<ProfessorDisciplineLnkDto> result = professorController.getProfessorDisciplines(professorDto);

        // Проверка
        assertEquals(2, result.size());
        assertEquals(prof1, result.get(0).getProfessor());
        assertEquals(prof2, result.get(1).getProfessor());
    }

}
