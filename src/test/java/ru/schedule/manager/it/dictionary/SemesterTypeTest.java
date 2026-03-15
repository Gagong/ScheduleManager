package ru.schedule.manager.it.dictionary;

import org.junit.jupiter.api.Test;
import ru.schedule.manager.business.dictionary.SemesterType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SemesterTypeTest {

    @Test
    void enumValues_ShouldHaveCorrectValues() {
        assertEquals("Осенний семестр", SemesterType.AUTUMN.getValue());
        assertEquals("Весенний семестр", SemesterType.SPRING.getValue());
    }

    @Test
    void enum_ShouldHaveTwoValues() {
        assertEquals(2, SemesterType.values().length);
    }

}
