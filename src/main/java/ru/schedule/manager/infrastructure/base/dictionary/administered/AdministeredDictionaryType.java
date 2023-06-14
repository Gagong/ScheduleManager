package ru.schedule.manager.infrastructure.base.dictionary.administered;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import ru.schedule.manager.infrastructure.base.dictionary.SimpleDictionary;

import static ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary.dictionary;

@Getter
@RequiredArgsConstructor
public enum AdministeredDictionaryType implements Serializable, SimpleDictionary {

	CLASSROOM("Аудитория"),
	PROFESSOR("Преподаватели"),
	DISCIPLINE("Дисциплина");

	private final String value;

	public static String lookupValue(final AdministeredDictionaryType type, final String key) {
		return dictionary().lookupValue(type, key);
	}

	public static String lookupKey(final AdministeredDictionaryType type, final String value) {
		return dictionary().lookupKey(type, value);
	}

	public static boolean containsValue(final AdministeredDictionaryType type, final String key) {
		return dictionary().containsValue(type, key);
	}

	public static boolean containsKey(final AdministeredDictionaryType type, final String value) {
		return dictionary().containsKey(type, value);
	}

	@Override
	public String getDictionaryValue() {
		return this.value;
	}

	@Override
	public String getDictionaryKey() {
		return this.name();
	}

}
