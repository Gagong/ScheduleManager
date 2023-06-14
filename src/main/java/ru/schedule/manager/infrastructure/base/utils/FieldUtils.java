package ru.schedule.manager.infrastructure.base.utils;

import java.lang.reflect.Field;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType;

@UtilityClass
public class FieldUtils {

	@SneakyThrows
	public static Field getField(final String fieldName, final Class clazz) {
		return clazz.getDeclaredField(fieldName);
	}

	public static AdministeredDictionaryType getDictionaryTypeFromField(final Field field) {
		return field.getDeclaredAnnotation(AdministeredDictionary.class).value();
	}

}
