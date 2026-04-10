package ru.schedule.manager.it.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.business.dto.ProfessorDisciplineLnkDto;
import ru.schedule.manager.business.entity.ProfessorDisciplineLnk;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.repository.ProfessorDisciplineLnkRepository;
import ru.schedule.manager.business.service.ProfessorDisciplineService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessorDisciplineServiceTest {

    @Mock
    private AdministeredDictionaryService administeredDictionaryService;

    @Mock
    private ProfessorDisciplineLnkRepository professorDisciplineLnkRepository;

    @InjectMocks
    private ProfessorDisciplineService professorDisciplineService;

    private Dictionary professorEntity;

    private Dictionary disciplineEntity;

    private DictionaryDto professorDto;

    private DictionaryDto disciplineDto;

    private ProfessorDisciplineLnk entity;

    private ProfessorDisciplineLnkDto dto;

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

        disciplineEntity = new Dictionary();
        disciplineEntity.setId(2L);
        disciplineEntity.setDictionaryValue("Discipline Value");

        professorDto = DictionaryDto.builder()
                .id(1L)
                .value("Professor Value")
                .build();

        disciplineDto = DictionaryDto.builder()
                .id(2L)
                .value("Discipline Value")
                .build();

        entity = ProfessorDisciplineLnk.builder()
                .id(100L)
                .professor(professorEntity)
                .discipline(disciplineEntity)
                .createdDateTime(now)
                .updateDateTime(now)
                .createdByEmployee(admin)
                .updatedByEmployee(admin)
                .build();

        dto = ProfessorDisciplineLnkDto.builder()
                .id(100L)
                .professor(professorDto)
                .discipline(disciplineDto)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();
    }

    @Test
    void fromEntity_ShouldConvertEntityToDto() {
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(disciplineEntity)).thenReturn(disciplineDto);

        final ProfessorDisciplineLnkDto result = professorDisciplineService.fromEntity(entity);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(professorDto, result.getProfessor());
        assertEquals(disciplineDto, result.getDiscipline());
        assertEquals(now, result.getCreatedDateTime());
        assertEquals(now, result.getUpdateDateTime());
    }

    @Test
    void fromEntityList_ShouldConvertEntitiesToDtos() {
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(disciplineEntity)).thenReturn(disciplineDto);

        final List<ProfessorDisciplineLnk> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(null);
        final List<ProfessorDisciplineLnkDto> results = professorDisciplineService.fromEntity(entities);

        assertEquals(1, results.size());
        final ProfessorDisciplineLnkDto result = results.get(0);
        assertEquals(disciplineDto, result.getDiscipline());
    }

    @Test
    void findById_WhenEntityExists_ShouldReturnDto() {
        when(professorDisciplineLnkRepository.findById(100L)).thenReturn(Optional.of(entity));
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(disciplineEntity)).thenReturn(disciplineDto);

        final ProfessorDisciplineLnkDto result = professorDisciplineService.findById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void findById_WhenEntityNotFound_ShouldThrowException() {
        when(professorDisciplineLnkRepository.findById(999L)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> professorDisciplineService.findById(999L));

        assertTrue(exception.getMessage().contains("ProfessorDisciplineLnk"));
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void update_ShouldThrowUnsupportedOperationException() {
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> professorDisciplineService.update(dto));

        assertEquals(BaseServiceAware.UNSUPPORTED_OPERATION, exception.getMessage());
    }

    @Test
    void create_WhenLinkDoesNotExist_ShouldCreateNewLink() {
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(administeredDictionaryService.getOneAsEntity(disciplineDto)).thenReturn(disciplineEntity);
        when(professorDisciplineLnkRepository.findByProfessorAndDiscipline(professorEntity, disciplineEntity)).thenReturn(Optional.empty());
        when(professorDisciplineLnkRepository.save(any(ProfessorDisciplineLnk.class))).thenReturn(entity);
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(disciplineEntity)).thenReturn(disciplineDto);

        final ProfessorDisciplineLnkDto result = professorDisciplineService.create(dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(professorDisciplineLnkRepository).save(any(ProfessorDisciplineLnk.class));
    }

    @Test
    void create_WhenLinkAlreadyExists_ShouldReturnDtoWithoutSaving() {
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(administeredDictionaryService.getOneAsEntity(disciplineDto)).thenReturn(disciplineEntity);
        when(professorDisciplineLnkRepository.findByProfessorAndDiscipline(professorEntity, disciplineEntity)).thenReturn(Optional.of(entity));

        final ProfessorDisciplineLnkDto result = professorDisciplineService.create(dto);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(professorDisciplineLnkRepository, never()).save(any());
    }

    @Test
    void delete_WhenEntityExists_ShouldDelete() {
        when(professorDisciplineLnkRepository.findById(100L)).thenReturn(Optional.of(entity));

        professorDisciplineService.delete(dto);

        verify(professorDisciplineLnkRepository).delete(entity);
    }

    @Test
    void delete_WhenEntityNotFound_ShouldThrowException() {
        when(professorDisciplineLnkRepository.findById(100L)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> professorDisciplineService.delete(dto));

        assertTrue(exception.getMessage().contains("ProfessorDisciplineLnk"));
        verify(professorDisciplineLnkRepository, never()).delete(any());
    }

    @Test
    void getProfessorDisciplines_ShouldReturnList() {
        when(administeredDictionaryService.getOneAsEntity(professorDto)).thenReturn(professorEntity);
        when(professorDisciplineLnkRepository.findByProfessor(professorEntity)).thenReturn(List.of(entity));
        when(administeredDictionaryService.fromEntity(professorEntity)).thenReturn(professorDto);
        when(administeredDictionaryService.fromEntity(disciplineEntity)).thenReturn(disciplineDto);

        final List<ProfessorDisciplineLnkDto> results = professorDisciplineService.getProfessorDisciplines(professorDto);

        assertEquals(1, results.size());
        assertEquals(100L, results.get(0).getId());
    }

    @Test
    void createProfessorDisciplineLnk_ShouldReturnDto() {
        final ProfessorDisciplineLnkDto dto = new ProfessorDisciplineLnkDto(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

}
