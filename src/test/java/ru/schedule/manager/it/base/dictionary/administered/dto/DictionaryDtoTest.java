package ru.schedule.manager.it.base.dictionary.administered.dto;

import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DictionaryDtoTest {

    @Test
    void builder_ShouldCreateDto() {
        final LocalDateTime now = LocalDateTime.now();

        final DictionaryDto dto = DictionaryDto.builder()
                .id(1L)
                .type("PROFESSOR")
                .key("PROF_001")
                .value("Иванов И.И.")
                .active(true)
                .displayOrder(10)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("PROFESSOR", dto.getType());
        assertEquals("PROF_001", dto.getKey());
        assertEquals("Иванов И.И.", dto.getValue());
        assertTrue(dto.isActive());
        assertEquals(10, dto.getDisplayOrder());
        assertEquals(now, dto.getCreatedDateTime());
        assertEquals(now, dto.getUpdateDateTime());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDto() {
        final DictionaryDto dto = new DictionaryDto();

        assertNull(dto.getId());
        assertNull(dto.getType());
        assertNull(dto.getKey());
        assertNull(dto.getValue());
        assertFalse(dto.isActive());
        assertEquals(0, dto.getDisplayOrder());
        assertNull(dto.getCreatedDateTime());
        assertNull(dto.getUpdateDateTime());
    }

    @Test
    void setters_ShouldUpdateValues() {
        final LocalDateTime now = LocalDateTime.now();
        final DictionaryDto dto = new DictionaryDto();

        dto.setId(1L);
        dto.setType("PROFESSOR");
        dto.setKey("PROF_001");
        dto.setValue("Иванов И.И.");
        dto.setActive(true);
        dto.setDisplayOrder(10);
        dto.setCreatedDateTime(now);
        dto.setUpdateDateTime(now);

        assertEquals(1L, dto.getId());
        assertEquals("PROFESSOR", dto.getType());
        assertEquals("PROF_001", dto.getKey());
        assertEquals("Иванов И.И.", dto.getValue());
        assertTrue(dto.isActive());
        assertEquals(10, dto.getDisplayOrder());
        assertEquals(now, dto.getCreatedDateTime());
        assertEquals(now, dto.getUpdateDateTime());
    }

    @Test
    void toBuilder_ShouldCreateCopy() {
        final LocalDateTime now = LocalDateTime.now();
        final DictionaryDto original = DictionaryDto.builder()
                .id(1L)
                .type("PROFESSOR")
                .key("PROF_001")
                .value("Иванов И.И.")
                .active(true)
                .displayOrder(10)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        final DictionaryDto copy = original.toBuilder().build();

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getType(), copy.getType());
        assertEquals(original.getKey(), copy.getKey());
        assertEquals(original.getValue(), copy.getValue());
        assertEquals(original.isActive(), copy.isActive());
        assertEquals(original.getDisplayOrder(), copy.getDisplayOrder());
        assertEquals(original.getCreatedDateTime(), copy.getCreatedDateTime());
        assertEquals(original.getUpdateDateTime(), copy.getUpdateDateTime());
    }

    @Test
    void toBuilder_ShouldAllowModification() {
        final LocalDateTime now = LocalDateTime.now();
        final DictionaryDto original = DictionaryDto.builder()
                .id(1L)
                .type("PROFESSOR")
                .key("PROF_001")
                .value("Иванов И.И.")
                .active(true)
                .displayOrder(10)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        final DictionaryDto modified = original.toBuilder()
                .key("PROF_002")
                .value("Петров П.П.")
                .active(false)
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("PROFESSOR", modified.getType());
        assertEquals("PROF_002", modified.getKey());
        assertEquals("Петров П.П.", modified.getValue());
        assertFalse(modified.isActive());
        assertEquals(10, modified.getDisplayOrder());

        // Original should not change
        assertEquals("PROF_001", original.getKey());
        assertEquals("Иванов И.И.", original.getValue());
        assertTrue(original.isActive());
    }

    @Test
    void equalsAndHashCode_ShouldWorkAsExpected() {
        final LocalDateTime now = LocalDateTime.now();
        final DictionaryDto dto1 = DictionaryDto.builder()
                .id(1L)
                .type("PROFESSOR")
                .key("PROF_001")
                .value("Иванов И.И.")
                .active(true)
                .displayOrder(10)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        final DictionaryDto dto2 = DictionaryDto.builder()
                .id(1L)
                .type("PROFESSOR")
                .key("PROF_001")
                .value("Иванов И.И.")
                .active(true)
                .displayOrder(10)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        final DictionaryDto dto3 = DictionaryDto.builder()
                .id(2L)
                .type("DISCIPLINE")
                .key("DISC_001")
                .value("Математика")
                .active(true)
                .displayOrder(5)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());

        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

}
