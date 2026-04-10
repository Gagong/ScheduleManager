package ru.schedule.manager.it.base.dictionary.administered.dto;

import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.SimpleDictionary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SimpleDictionaryTest {

    @Test
    void constructor_ShouldSetKeyAndValue() {
        final SimpleDictionary dict = new SimpleDictionary("KEY", "VALUE");

        assertEquals("KEY", dict.getKey());
        assertEquals("VALUE", dict.getValue());
    }

    @Test
    void equalsAndHashCode_ShouldWorkAsExpected() {
        final SimpleDictionary dict1 = new SimpleDictionary("KEY", "VALUE");
        final SimpleDictionary dict2 = new SimpleDictionary("KEY", "VALUE");
        final SimpleDictionary dict3 = new SimpleDictionary("KEY2", "VALUE2");

        assertEquals(dict1, dict2);
        assertNotEquals(dict1, dict3);
        assertNotEquals(dict1, null);
        assertNotEquals(dict1, new Object());

        assertEquals(dict1.hashCode(), dict2.hashCode());
        assertNotEquals(dict1.hashCode(), dict3.hashCode());
    }

}
