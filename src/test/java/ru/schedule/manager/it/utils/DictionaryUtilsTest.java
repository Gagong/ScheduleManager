package ru.schedule.manager.it.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.utils.DictionaryUtils;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class DictionaryUtilsTest {

    @Test
    void setMappedValueIfPresent_WithNonNullGetter_ShouldSetMappedValue() {
        try (MockedStatic<AdministeredDictionaryType> mockedStatic = mockStatic(AdministeredDictionaryType.class)) {
            mockedStatic.when(() -> AdministeredDictionaryType.lookupValue(eq(AdministeredDictionaryType.PROFESSOR), eq("key"))).thenReturn("mappedValue");

            final AtomicReference<String> setterValue = new AtomicReference<>();

            DictionaryUtils.setMappedValueIfPresent(
                    () -> "key",
                    setterValue::set,
                    AdministeredDictionaryType.PROFESSOR
            );

            assertEquals("mappedValue", setterValue.get());
            mockedStatic.verify(() -> AdministeredDictionaryType.lookupValue(AdministeredDictionaryType.PROFESSOR, "key"));
        }
    }

    @Test
    void setMappedValueIfPresent_WithNullGetter_ShouldNotSetValue() {
        try (MockedStatic<AdministeredDictionaryType> mockedStatic = mockStatic(AdministeredDictionaryType.class)) {
            final AtomicReference<String> setterValue = new AtomicReference<>("initial");

            DictionaryUtils.setMappedValueIfPresent(
                    () -> null,
                    setterValue::set,
                    AdministeredDictionaryType.PROFESSOR
            );

            assertEquals("initial", setterValue.get());
            mockedStatic.verify(() -> AdministeredDictionaryType.lookupValue(any(), any()), never());
        }
    }

    @Test
    void setMappedKeyIfPresent_WithNonNullGetter_ShouldSetMappedKey() {
        try (MockedStatic<AdministeredDictionaryType> mockedStatic = mockStatic(AdministeredDictionaryType.class)) {
            mockedStatic.when(() -> AdministeredDictionaryType.lookupKey(eq(AdministeredDictionaryType.PROFESSOR), eq("value"))).thenReturn("mappedKey");

            final AtomicReference<String> setterValue = new AtomicReference<>();

            DictionaryUtils.setMappedKeyIfPresent(
                    () -> "value",
                    setterValue::set,
                    AdministeredDictionaryType.PROFESSOR
            );

            assertEquals("mappedKey", setterValue.get());
            mockedStatic.verify(() -> AdministeredDictionaryType.lookupKey(AdministeredDictionaryType.PROFESSOR, "value"));
        }
    }

    @Test
    void setMappedKeyIfPresent_WithNullGetter_ShouldNotSetValue() {
        try (MockedStatic<AdministeredDictionaryType> mockedStatic = mockStatic(AdministeredDictionaryType.class)) {
            final AtomicReference<String> setterValue = new AtomicReference<>("initial");

            DictionaryUtils.setMappedKeyIfPresent(
                    () -> null,
                    setterValue::set,
                    AdministeredDictionaryType.PROFESSOR
            );

            assertEquals("initial", setterValue.get());
            mockedStatic.verify(() -> AdministeredDictionaryType.lookupKey(any(), any()), never());
        }
    }

    @Test
    void getField_ShouldReturnField() {
        class TestClass {
            private String testField;
        }

        final Field field = DictionaryUtils.getField("testField", TestClass.class);

        assertNotNull(field);
        assertEquals("testField", field.getName());
        assertEquals(String.class, field.getType());
    }

    @Test
    void getField_WithInvalidField_ShouldThrowException() {
        assertThrows(NoSuchFieldException.class, () -> DictionaryUtils.getField("nonExistentField", String.class));
    }

    @Test
    void getField_ShouldWorkWithInaccessibleFields() {
        class TestClass {
            private String privateField;
        }

        final Field field = DictionaryUtils.getField("privateField", TestClass.class);

        assertNotNull(field);
        assertTrue(field.canAccess(new TestClass()));
    }

}
