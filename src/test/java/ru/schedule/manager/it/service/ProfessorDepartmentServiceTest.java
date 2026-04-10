package ru.schedule.manager.it.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.business.dto.ProfessorDepartmentLnkDto;
import ru.schedule.manager.business.entity.ProfessorDepartmentLnk;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.repository.ProfessorDepartmentLnkRepository;
import ru.schedule.manager.business.service.ProfessorDepartmentService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessorDepartmentServiceTest {

    @Mock
    private AdministeredDictionaryService administeredDictionaryService;

    @Mock
    private ProfessorDepartmentLnkRepository professorDepartmentLnkRepository;

    @InjectMocks
    private ProfessorDepartmentService professorDepartmentService;

    private Dictionary professorEntity;

    private Dictionary departmentEntity;

    private DictionaryDto professorDto;

    private DictionaryDto departmentDto;

    private ProfessorDepartmentLnk entity;

    private ProfessorDepartmentLnkDto dto;

    private LocalDateTime now;

    private Employee admin;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        admin = new Employee();
        admin.setId(1L);

        professorEntity = new Dictionary();
        professorEntity.setId(1L);
        professorEntity.setDictionaryValue("Professor Value");

        departmentEntity = new Dictionary();
        departmentEntity.setId(2L);
        departmentEntity.setDictionaryValue("Department Value");

        professorDto = DictionaryDto.builder()
                .id(1L)
                .value("Professor Value")
                .build();

        departmentDto = DictionaryDto.builder()
                .id(2L)
                .value("Department Value")
                .build();

        entity = ProfessorDepartmentLnk.builder()
                .id(100L)
                .professor(professorEntity)
                .department(departmentEntity)
                .createdDateTime(now)
                .updateDateTime(now)
                .createdByEmployee(admin)
                .updatedByEmployee(admin)
                .build();

        dto = ProfessorDepartmentLnkDto.builder()
                .id(100L)
                .professor(professorDto)
                .department(departmentDto)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();
    }

    @Test
    void fromEntity_WithNonNullEntity_ShouldConvertEntityToDto() {
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(departmentEntity)).thenReturn(departmentDto);

        final ProfessorDepartmentLnkDto result = professorDepartmentService.fromEntity(entity);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(professorDto, result.getProfessor());
        assertEquals(departmentDto, result.getDepartment());
        assertEquals(now, result.getCreatedDateTime());
        assertEquals(now, result.getUpdateDateTime());
    }

    @Test
    void fromEntity_WithNullEntity_ShouldReturnNull() {
        final ProfessorDepartmentLnkDto result = professorDepartmentService.fromEntity((ProfessorDepartmentLnk) null);

        assertNull(result);
    }

    @Test
    void fromEntityList_ShouldConvertEntitiesToDtos() {
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(departmentEntity)).thenReturn(departmentDto);

        final List<ProfessorDepartmentLnk> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(null);
        final List<ProfessorDepartmentLnkDto> results = professorDepartmentService.fromEntity(entities);

        assertEquals(1, results.size());
        final ProfessorDepartmentLnkDto result = results.get(0);
        assertEquals(departmentDto, result.getDepartment());
    }

    @Test
    void findById_WhenEntityExists_ShouldReturnDto() {
        when(professorDepartmentLnkRepository.findById(100L)).thenReturn(Optional.of(entity));
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(departmentEntity)).thenReturn(departmentDto);

        final ProfessorDepartmentLnkDto result = professorDepartmentService.findById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void findById_WhenEntityNotFound_ShouldThrowException() {
        when(professorDepartmentLnkRepository.findById(999L)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> professorDepartmentService.findById(999L));

        assertTrue(exception.getMessage().contains("ProfessorDepartmentLnk"));
    }

    @Test
    void update_ShouldThrowUnsupportedOperationException() {
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> professorDepartmentService.update(dto));

        assertEquals(BaseServiceAware.UNSUPPORTED_OPERATION, exception.getMessage());
    }

    @Test
    void create_WhenLinkExists_ShouldUpdateExisting() {
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(administeredDictionaryService.getOneAsEntity(departmentDto)).thenReturn(departmentEntity);
        when(professorDepartmentLnkRepository.findFirstByProfessor(professorEntity)).thenReturn(Optional.of(entity));
        when(professorDepartmentLnkRepository.save(any(ProfessorDepartmentLnk.class))).thenReturn(entity.setDepartment(departmentEntity));
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(departmentEntity)).thenReturn(departmentDto);

        final ProfessorDepartmentLnkDto result = professorDepartmentService.create(dto);

        assertNotNull(result);
        verify(professorDepartmentLnkRepository).save(entity);
    }

    @Test
    void create_WhenLinkDoesNotExist_ShouldCreateNew() {
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(administeredDictionaryService.getOneAsEntity(departmentDto)).thenReturn(departmentEntity);
        when(professorDepartmentLnkRepository.findFirstByProfessor(professorEntity)).thenReturn(Optional.empty());
        when(professorDepartmentLnkRepository.save(any(ProfessorDepartmentLnk.class))).thenReturn(entity);
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(departmentEntity)).thenReturn(departmentDto);

        final ProfessorDepartmentLnkDto result = professorDepartmentService.create(dto);

        assertNotNull(result);
        verify(professorDepartmentLnkRepository).save(any(ProfessorDepartmentLnk.class));
    }

    @Test
    void delete_WhenEntityExists_ShouldDelete() {
        when(professorDepartmentLnkRepository.findById(100L)).thenReturn(Optional.of(entity));

        professorDepartmentService.delete(dto);

        verify(professorDepartmentLnkRepository).delete(entity);
    }

    @Test
    void delete_WhenEntityNotFound_ShouldThrowException() {
        when(professorDepartmentLnkRepository.findById(100L)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> professorDepartmentService.delete(dto));

        assertTrue(exception.getMessage().contains("ProfessorDepartmentLnk"));
    }

    @Test
    void getDepartmentProfessors_ShouldReturnList() {
        when(administeredDictionaryService.getOneAsEntity(departmentDto)).thenReturn(departmentEntity);
        when(professorDepartmentLnkRepository.findByDepartment(departmentEntity)).thenReturn(List.of(entity));
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(departmentEntity)).thenReturn(departmentDto);

        final List<ProfessorDepartmentLnkDto> results = professorDepartmentService.getDepartmentProfessors(departmentDto);

        assertEquals(1, results.size());
        assertEquals(100L, results.get(0).getId());
    }

    @Test
    void getProfessorDepartment_WhenExists_ShouldReturnDto() {
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(professorDepartmentLnkRepository.findByProfessor(professorEntity)).thenReturn(Optional.of(entity));
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(departmentEntity)).thenReturn(departmentDto);

        final ProfessorDepartmentLnkDto result = professorDepartmentService.getProfessorDepartment(professorDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void getProfessorDepartment_WhenNotExists_ShouldReturnNull() {
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(professorDepartmentLnkRepository.findByProfessor(professorEntity)).thenReturn(Optional.empty());

        final ProfessorDepartmentLnkDto result = professorDepartmentService.getProfessorDepartment(professorDto);

        assertNull(result);
    }

}
