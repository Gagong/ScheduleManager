package ru.schedule.manager.it.dictionary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.schedule.manager.business.dictionary.ProfileNavigator;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileNavigatorTest {

    @Test
    void getAll_ShouldReturnAllNavigators() {
        final Map<String, Map<String, Object>> result = ProfileNavigator.getAll();

        assertEquals(5, result.size());
        assertTrue(result.containsKey("_0_GENERAL_SCHEDULE"));
        assertTrue(result.containsKey("_1_CREATE_SCHEDULE"));
        assertTrue(result.containsKey("_2_DICTIONARY"));
        assertTrue(result.containsKey("_3_PROFESSOR"));
        assertTrue(result.containsKey("_4_EMPLOYEE"));
    }

    @Test
    void getUnsecured_ShouldReturnOnlyUnsecuredNavigators() {
        final Map<String, Map<String, Object>> result = ProfileNavigator.getUnsecured();

        assertEquals(1, result.size());
        assertTrue(result.containsKey("_0_GENERAL_SCHEDULE"));
        assertFalse(result.containsKey("_1_CREATE_SCHEDULE"));

        final Map<String, Object> navigator = result.get("_0_GENERAL_SCHEDULE");
        assertEquals("Расписание", navigator.get("label"));
        assertEquals("/", navigator.get("path"));
        assertFalse((Boolean) navigator.get("secured"));
    }

    @ParameterizedTest
    @EnumSource(ProfileNavigator.class)
    void toMap_ShouldReturnCorrectMap(final ProfileNavigator navigator) {
        final Map<String, Object> result = ProfileNavigator.toMap(navigator);

        assertEquals(3, result.size());
        assertEquals(navigator.getLabel(), result.get("label"));
        assertEquals(navigator.getValue(), result.get("path"));
        assertEquals(navigator.isSecured(), result.get("secured"));
    }

    @Test
    void enumValues_ShouldHaveCorrectProperties() {
        assertEquals("Расписание", ProfileNavigator._0_GENERAL_SCHEDULE.getLabel());
        assertEquals("/", ProfileNavigator._0_GENERAL_SCHEDULE.getValue());
        assertFalse(ProfileNavigator._0_GENERAL_SCHEDULE.isSecured());

        assertEquals("Составление расписания", ProfileNavigator._1_CREATE_SCHEDULE.getLabel());
        assertEquals("/create-schedule", ProfileNavigator._1_CREATE_SCHEDULE.getValue());
        assertTrue(ProfileNavigator._1_CREATE_SCHEDULE.isSecured());

        assertEquals(ProfileNavigator._0_GENERAL_SCHEDULE.name(), ProfileNavigator._0_GENERAL_SCHEDULE.getDictionaryKey());
        assertEquals(ProfileNavigator._0_GENERAL_SCHEDULE.getValue(), ProfileNavigator._0_GENERAL_SCHEDULE.getDictionaryValue());
    }

}
