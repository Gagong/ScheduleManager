package ru.schedule.manager.infrastructure.base.dictionary.administered.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType;

import static ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType.lookupKey;
import static ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType.lookupValue;

@UtilityClass
public class DictionaryUtils {

	public static void setMappedValueIfPresent(final Supplier<String> getter, final Consumer<String> setter, final AdministeredDictionaryType type) {
		Optional.ofNullable(getter.get()).map(value -> lookupValue(type, value)).ifPresent(setter);
	}

	public static void setMappedKeyIfPresent(final Supplier<String> getter, final Consumer<String> setter, final AdministeredDictionaryType type) {
		Optional.ofNullable(getter.get()).map(key -> lookupKey(type, key)).ifPresent(setter);
	}

}
