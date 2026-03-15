package ru.schedule.manager.it.dictionary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AdministeredDictionaryTypeTest {

    @Test
    void enum_ShouldHaveAllExpectedValues() {
        assertEquals(10, AdministeredDictionaryType.values().length);

        assertEquals("Аудитория", AdministeredDictionaryType.CLASSROOM.getValue());
        assertEquals("Преподаватели", AdministeredDictionaryType.PROFESSOR.getValue());
        assertEquals("Дисциплина", AdministeredDictionaryType.DISCIPLINE.getValue());
        assertEquals("Тип занятия", AdministeredDictionaryType.DISCIPLINE_TYPE.getValue());
        assertEquals("Факультет", AdministeredDictionaryType.FACULTY.getValue());
        assertEquals("Группа", AdministeredDictionaryType.GROUP.getValue());
        assertEquals("Подгруппы", AdministeredDictionaryType.SUBGROUP.getValue());
        assertEquals("Подразделение", AdministeredDictionaryType.DEPARTMENT.getValue());
        assertEquals("Семестр", AdministeredDictionaryType.SEMESTER.getValue());
        assertEquals("Время занятий", AdministeredDictionaryType.LESSON_TIME.getValue());
    }

    @ParameterizedTest
    @EnumSource(AdministeredDictionaryType.class)
    void getDictionaryKey_ShouldReturnEnumName(final AdministeredDictionaryType type) {
        assertEquals(type.name(), type.getDictionaryKey());
    }

    @ParameterizedTest
    @EnumSource(AdministeredDictionaryType.class)
    void getDictionaryValue_ShouldReturnValue(final AdministeredDictionaryType type) {
        assertNotNull(type.getDictionaryValue());
        assertFalse(type.getDictionaryValue().isEmpty());
    }

}
