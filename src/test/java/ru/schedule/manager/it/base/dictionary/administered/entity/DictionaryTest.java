package ru.schedule.manager.it.base.dictionary.administered.entity;

import org.junit.jupiter.api.Test;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DictionaryTest {

    @Test
    void builder_ShouldCreateDictionary() {
        final LocalDateTime now = LocalDateTime.now();

        final Dictionary dictionary = Dictionary.builder()
                .id(1L)
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .active(true)
                .displayOrder(10)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        assertNotNull(dictionary);
        assertEquals(1L, dictionary.getId());
        assertEquals(AdministeredDictionaryType.PROFESSOR, dictionary.getDictionaryType());
        assertEquals("PROF_001", dictionary.getDictionaryKey());
        assertEquals("Иванов И.И.", dictionary.getDictionaryValue());
        assertTrue(dictionary.isActive());
        assertEquals(10, dictionary.getDisplayOrder());
        assertEquals(now, dictionary.getCreatedDateTime());
        assertEquals(now, dictionary.getUpdateDateTime());
    }

    @Test
    void toString_ShouldContainKeyInformation() {
        final Dictionary dictionary = Dictionary.builder()
                .id(1L)
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .build();

        final String toString = dictionary.toString();

        assertTrue(toString.contains("PROFESSOR"));
        assertTrue(toString.contains("PROF_001"));
        assertTrue(toString.contains("Иванов И.И."));
        assertTrue(toString.contains("id=1"));
    }

    @Test
    void equals_ShouldCompareByTypeAndKeyAndValue() {
        final Dictionary dict1 = Dictionary.builder()
                .id(1L)
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .build();

        final Dictionary dict2 = Dictionary.builder()
                .id(2L)
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .build();

        final Dictionary dict3 = Dictionary.builder()
                .dictionaryType(AdministeredDictionaryType.DISCIPLINE)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .build();

        assertEquals(dict1, dict2);
        assertNotEquals(dict1, dict3);
        assertNotEquals(dict1, null);
        assertNotEquals(dict1, new Object());
    }

    @Test
    void hashCode_ShouldBeConsistentWithEquals() {
        final Dictionary dict1 = Dictionary.builder()
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .build();

        final Dictionary dict2 = Dictionary.builder()
                .dictionaryType(AdministeredDictionaryType.PROFESSOR)
                .dictionaryKey("PROF_001")
                .dictionaryValue("Иванов И.И.")
                .build();

        assertEquals(dict1.hashCode(), dict2.hashCode());
    }

    @Test
    void defaultValues_ShouldBeSet() {
        final Dictionary dictionary = new Dictionary();

        assertTrue(dictionary.isActive());
        assertEquals(0, dictionary.getDisplayOrder());
    }

}
