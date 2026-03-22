package ru.schedule.manager.it.base.dictionary.administered.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.repository.DictionaryRepository;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.UpsertSemesterService;
import ru.schedule.manager.infrastructure.base.entity.Employee;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.schedule.manager.business.dictionary.AdministeredDictionaryType.SUBGROUP;
import static ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary.DEFAULT_KEY;

@ExtendWith(MockitoExtension.class)
class AdministeredDictionaryServiceTest {

    @Mock
    private DictionaryRepository dictionaryRepository;

    @Captor
    private ArgumentCaptor<Dictionary> dictionaryCaptor;

    private Dictionary dictionaryEntity;

    private DictionaryDto dictionaryDto;

    private Dictionary defaultSubgroupEntity;

    private LocalDateTime now;

    private Employee admin;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        admin = new Employee();
        admin.setId(1L);

        defaultSubgroupEntity = Dictionary.builder()
                .id(1L)
                .dictionaryType(AdministeredDictionaryType.SUBGROUP)
                .dictionaryKey(DEFAULT_KEY)
                .dictionaryValue("DEFAULT")
                .active(true)
                .displayOrder(0)
                .createdDateTime(now)
                .updateDateTime(now)
                .createdByEmployee(admin)
                .updatedByEmployee(admin)
                .build();

        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(SUBGROUP, DEFAULT_KEY)).thenReturn(Optional.of(defaultSubgroupEntity));

        dictionaryEntity = Dictionary.builder()
                .id(100L)
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .active(true)
                .displayOrder(1)
                .createdDateTime(now)
                .updateDateTime(now)
                .createdByEmployee(admin)
                .updatedByEmployee(admin)
                .build();

        dictionaryDto = DictionaryDto.builder()
                .id(100L)
                .type(AdministeredDictionaryType.PROFESSOR.name())
                .key("PROF_001")
                .value("Иванов И.И.")
                .active(true)
                .displayOrder(1)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();
    }

    @Test
    void constructor_ShouldInitializeInstanceAndDefaultSubgroup() {
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.SUBGROUP, DEFAULT_KEY)).thenReturn(Optional.of(defaultSubgroupEntity));

        final AdministeredDictionaryService service = new AdministeredDictionaryService(dictionaryRepository);

        assertNotNull(service);
        assertNotNull(IAdministeredDictionary.dictionary());
        assertEquals(defaultSubgroupEntity, IAdministeredDictionary.defaultSubgroup());
    }

    @Test
    void constructor_WhenDefaultSubgroupNotFound_ShouldThrowException() {
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.SUBGROUP, DEFAULT_KEY)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> new AdministeredDictionaryService(dictionaryRepository));
    }

    @Test
    void fromEntity_ShouldConvertEntityToDto() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final DictionaryDto result = administeredDictionaryService.fromEntity(dictionaryEntity);

        assertNotNull(result);
        assertEquals(dictionaryEntity.getId(), result.getId());
        assertEquals(dictionaryEntity.getDictionaryType().name(), result.getType());
        assertEquals(dictionaryEntity.getDictionaryKey(), result.getKey());
        assertEquals(dictionaryEntity.getDictionaryValue(), result.getValue());
        assertEquals(dictionaryEntity.isActive(), result.isActive());
        assertEquals(dictionaryEntity.getDisplayOrder(), result.getDisplayOrder());
    }

    @Test
    void fromEntityList_ShouldConvertEntitiesToDtos() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final List<Dictionary> entities = new ArrayList<>();
        entities.add(dictionaryEntity);
        entities.add(null);

        final List<DictionaryDto> results = administeredDictionaryService.fromEntity(entities);

        assertEquals(1, results.size());
        final DictionaryDto result = results.get(0);
        assertEquals(dictionaryEntity.getDictionaryKey(), result.getKey());
    }

    @Test
    void findById_WhenEntityExists_ShouldReturnDto() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.of(dictionaryEntity));

        final DictionaryDto result = administeredDictionaryService.findById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void findById_WhenEntityNotFound_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findById(999L)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.findById(999L));

        assertTrue(exception.getMessage().contains("Dictionary"));
    }

    @Test
    void update_WhenDeactivating_ShouldUpdate() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final DictionaryDto deactivateDto = dictionaryDto.toBuilder().active(false).build();
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.of(dictionaryEntity));

        final DictionaryDto result = administeredDictionaryService.update(deactivateDto);

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(dictionaryRepository, atLeastOnce()).findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(any(), any());
    }

    @Test
    void update_WhenKeyAndValueExist_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final DictionaryDto updateDto = dictionaryDto.toBuilder().key("NEW_KEY").value("NEW_VALUE").build();
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.of(dictionaryEntity));
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "NEW_KEY")).thenReturn(Optional.of(new Dictionary()));
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "NEW_VALUE")).thenReturn(Optional.of(new Dictionary()));

        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> administeredDictionaryService.update(updateDto));

        assertTrue(exception.getMessage().contains("уже существуют"));
    }

    @Test
    void update_WhenEntityNotFound_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.update(dictionaryDto));
    }

    @Test
    void create_WithNewValues_ShouldCreate() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final DictionaryDto newDto = DictionaryDto.builder()
                .type(AdministeredDictionaryType.PROFESSOR.name())
                .key("NEW_PROF")
                .value("Петров П.П.")
                .build();

        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "NEW_PROF")).thenReturn(Optional.empty());
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "Петров П.П.")).thenReturn(Optional.empty());
        when(dictionaryRepository.getNextOrder(AdministeredDictionaryType.PROFESSOR)).thenReturn(Optional.of(5));
        when(dictionaryRepository.save(any(Dictionary.class))).thenReturn(dictionaryEntity);

        final DictionaryDto result = administeredDictionaryService.create(newDto);

        assertNotNull(result);
        verify(dictionaryRepository).save(dictionaryCaptor.capture());
        final Dictionary saved = dictionaryCaptor.getValue();
        assertEquals(AdministeredDictionaryType.PROFESSOR, saved.getDictionaryType());
        assertEquals("NEW_PROF", saved.getDictionaryKey());
        assertEquals("Петров П.П.", saved.getDictionaryValue());
        assertTrue(saved.isActive());
        assertEquals(6, saved.getDisplayOrder());
    }

    @Test
    void create_WhenKeyExists_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final DictionaryDto newDto = DictionaryDto.builder()
                .type(AdministeredDictionaryType.PROFESSOR.name())
                .key("EXISTING_KEY")
                .value("New Value")
                .build();

        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "EXISTING_KEY")).thenReturn(Optional.of(new Dictionary()));

        assertThrows(UnsupportedOperationException.class, () -> administeredDictionaryService.create(newDto));
    }

    @Test
    void create_WhenValueExists_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final DictionaryDto newDto = DictionaryDto.builder()
                .type(AdministeredDictionaryType.PROFESSOR.name())
                .key("New Key")
                .value("EXISTING_VALUE")
                .build();

        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "New Key")).thenReturn(Optional.empty());
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "EXISTING_VALUE")).thenReturn(Optional.of(new Dictionary()));

        assertThrows(UnsupportedOperationException.class, () -> administeredDictionaryService.create(newDto));
    }

    @Test
    void delete_ShouldDeactivateEntity() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.of(dictionaryEntity));

        administeredDictionaryService.delete(dictionaryDto);
    }

    @Test
    void delete_WhenEntityNotFound_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.delete(dictionaryDto));
    }

    @Test
    void lookupValue_WhenExists_ShouldReturnValue() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "PROF_001")).thenReturn(Optional.of(dictionaryEntity));

        final String result = administeredDictionaryService.lookupValue(AdministeredDictionaryType.PROFESSOR, "PROF_001");

        assertEquals("Иванов И.И.", result);
    }

    @Test
    void lookupValue_WhenDefaultKey_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final Dictionary defaultDict = Dictionary.builder()
                .dictionaryKey(DEFAULT_KEY)
                .dictionaryValue("DEFAULT")
                .build();

        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, DEFAULT_KEY)).thenReturn(Optional.of(defaultDict));

        assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.lookupValue(AdministeredDictionaryType.PROFESSOR, DEFAULT_KEY));
    }

    @Test
    void lookupValue_WhenNotFound_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "INVALID")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.lookupValue(AdministeredDictionaryType.PROFESSOR, "INVALID"));
    }

    @Test
    void lookupValue_WithNullArguments_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.lookupValue(null, "key"));

        assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.lookupValue(AdministeredDictionaryType.PROFESSOR, null));
    }

    @Test
    void lookupKey_WhenExists_ShouldReturnKey() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "Иванов И.И.")).thenReturn(Optional.of(dictionaryEntity));

        final String result = administeredDictionaryService.lookupKey(AdministeredDictionaryType.PROFESSOR, "Иванов И.И.");

        assertEquals("PROF_001", result);
    }

    @Test
    void lookupKey_WhenDefaultValue_ShouldThrowException() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final Dictionary defaultDict = Dictionary.builder()
                .dictionaryKey(DEFAULT_KEY)
                .dictionaryValue("DEFAULT")
                .build();

        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "DEFAULT")).thenReturn(Optional.of(defaultDict));

        assertThrows(EntityNotFoundException.class, () -> administeredDictionaryService.lookupKey(AdministeredDictionaryType.PROFESSOR, "DEFAULT"));
    }

    @Test
    void containsKey_WhenExists_ShouldReturnTrue() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "PROF_001")).thenReturn(Optional.of(dictionaryEntity));

        final boolean result = administeredDictionaryService.containsKey(AdministeredDictionaryType.PROFESSOR, "PROF_001");

        assertTrue(result);
    }

    @Test
    void containsKey_WhenNotExists_ShouldReturnFalse() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "INVALID")).thenReturn(Optional.empty());

        final boolean result = administeredDictionaryService.containsKey(AdministeredDictionaryType.PROFESSOR, "INVALID");

        assertFalse(result);
    }

    @Test
    void containsKey_WithDefaultKey_ShouldReturnFalse() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final Dictionary defaultDict = Dictionary.builder().dictionaryKey(DEFAULT_KEY).build();

        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, DEFAULT_KEY)).thenReturn(Optional.of(defaultDict));

        final boolean result = administeredDictionaryService.containsKey(AdministeredDictionaryType.PROFESSOR, DEFAULT_KEY);

        assertFalse(result);
    }

    @Test
    void containsValue_WhenExists_ShouldReturnTrue() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "Иванов И.И.")).thenReturn(Optional.of(dictionaryEntity));

        final boolean result = administeredDictionaryService.containsValue(AdministeredDictionaryType.PROFESSOR, "Иванов И.И.");

        assertTrue(result);
    }

    @Test
    void getAllByType_ShouldReturnFilteredList() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final Dictionary activeDict = dictionaryEntity;
        final Dictionary inactiveDict = Dictionary.builder()
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_002")
                .dictionaryValue("Петров П.П.")
                .active(false)
                .displayOrder(2)
                .build();
        final Dictionary defaultDict = Dictionary.builder()
                .dictionaryKey(DEFAULT_KEY)
                .build();

        when(dictionaryRepository.findAllByDictionaryTypeOrderByDisplayOrderDesc(AdministeredDictionaryType.PROFESSOR)).thenReturn(List.of(activeDict, inactiveDict, defaultDict));

        final List<DictionaryDto> results = administeredDictionaryService.getAllByType(AdministeredDictionaryType.PROFESSOR, true);

        assertEquals(1, results.size());
        assertEquals("PROF_001", results.get(0).getKey());
    }

    @Test
    void getAllEntitiesByType_ShouldReturnFilteredList() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final Dictionary activeDict = dictionaryEntity;
        final Dictionary inactiveDict = Dictionary.builder()
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_002")
                .dictionaryValue("Петров П.П.")
                .active(false)
                .displayOrder(2)
                .build();

        when(dictionaryRepository.findAllByDictionaryTypeOrderByDisplayOrderDesc(AdministeredDictionaryType.PROFESSOR)).thenReturn(List.of(activeDict, inactiveDict));

        final List<Dictionary> results = administeredDictionaryService.getAllEntitiesByType(AdministeredDictionaryType.PROFESSOR, true);

        assertEquals(1, results.size());
        assertTrue(results.get(0).isActive());
    }

    @Test
    void getByTypeAndKey_WhenExists_ShouldReturnDto() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "PROF_001")).thenReturn(Optional.of(dictionaryEntity));

        final DictionaryDto result = administeredDictionaryService.getByTypeAndKey(AdministeredDictionaryType.PROFESSOR, "PROF_001");

        assertNotNull(result);
        assertEquals("PROF_001", result.getKey());
    }

    @Test
    void getEntityByTypeAndKey_WhenExists_ShouldReturnEntity() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType.PROFESSOR, "PROF_001")).thenReturn(Optional.of(dictionaryEntity));

        final Dictionary result = administeredDictionaryService.getEntityByTypeAndKey(AdministeredDictionaryType.PROFESSOR, "PROF_001");

        assertNotNull(result);
        assertEquals("PROF_001", result.getDictionaryKey());
    }

    @Test
    void dictionaryEntityToDto_WithConsumer_ShouldAcceptIfNotNull() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final Consumer<DictionaryDto> consumer = mock(Consumer.class);

        administeredDictionaryService.dictionaryEntityToDto(dictionaryEntity, consumer);

        verify(consumer).accept(any(DictionaryDto.class));
    }

    @Test
    void dictionaryEntityToDto_WithConsumerAndNull_ShouldNotAccept() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final Consumer<DictionaryDto> consumer = mock(Consumer.class);

        administeredDictionaryService.dictionaryEntityToDto(null, consumer);

        verify(consumer, never()).accept(any());
    }

    @Test
    void dictionaryEntityToDto_ShouldReturnDtoOrNull() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final DictionaryDto result = administeredDictionaryService.dictionaryEntityToDto(dictionaryEntity);
        assertNotNull(result);

        final DictionaryDto nullResult = administeredDictionaryService.dictionaryEntityToDto(null);
        assertNull(nullResult);
    }

    @Test
    void getOneAsEntity_WithDto_ShouldReturnEntity() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.of(dictionaryEntity));

        final Dictionary result = administeredDictionaryService.getOneAsEntity(dictionaryDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void getOneAsEntity_WithId_ShouldReturnEntity() {
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        when(dictionaryRepository.findById(100L)).thenReturn(Optional.of(dictionaryEntity));

        final Dictionary result = administeredDictionaryService.getOneAsEntity(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void fillSemesters_ShouldCreateSemesters() {
        when(dictionaryRepository.save(any())).thenReturn(dictionaryEntity);
        final AdministeredDictionaryService administeredDictionaryService = new AdministeredDictionaryService(dictionaryRepository);
        final UpsertSemesterService upsertSemesterService = new UpsertSemesterService(administeredDictionaryService);
        upsertSemesterService.fillSemesters();
    }

}
