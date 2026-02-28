package ru.schedule.manager.business.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionary;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static ru.schedule.manager.business.dictionary.AdministeredDictionaryType.lookupKey;
import static ru.schedule.manager.business.dictionary.AdministeredDictionaryType.lookupValue;

@UtilityClass
public class DictionaryUtils {

	public static void setMappedValueIfPresent(final Supplier<String> getter, final Consumer<String> setter, final AdministeredDictionaryType type) {
		Optional.ofNullable(getter.get()).map(value -> lookupValue(type, value)).ifPresent(setter);
	}

	public static void setMappedKeyIfPresent(final Supplier<String> getter, final Consumer<String> setter, final AdministeredDictionaryType type) {
		Optional.ofNullable(getter.get()).map(key -> lookupKey(type, key)).ifPresent(setter);
	}

	@SneakyThrows
	public static Field getField(final String fieldName, final Class clazz) {
		return clazz.getDeclaredField(fieldName);
	}

	public static AdministeredDictionaryType getDictionaryTypeFromField(final Field field) {
		return field.getDeclaredAnnotation(AdministeredDictionary.class).value();
	}

}
