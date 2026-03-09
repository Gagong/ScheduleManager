package ru.schedule.manager.infrastructure.base.dictionary.administered;

import org.apache.commons.lang3.StringUtils;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public interface IAdministeredDictionary {

	AtomicReference<IAdministeredDictionary> INSTANCE = new AtomicReference<>();

	AtomicReference<Dictionary> DEFAULT_SUB_GROUP = new AtomicReference<>();

	String DEFAULT_KEY = "DEFAULT";

	Predicate<Dictionary> IS_NOT_DEFAULT = dictionary -> Objects.nonNull(dictionary) && !StringUtils.equals(dictionary.getDictionaryKey(), DEFAULT_KEY);

	static IAdministeredDictionary dictionary() {
		return INSTANCE.get();
	}

	static Dictionary defaultSubgroup() {
		return DEFAULT_SUB_GROUP.get();
	}

	static Predicate<Dictionary> isNotDefaultDictionary() {
		return IS_NOT_DEFAULT;
	}

	String lookupValue(AdministeredDictionaryType dictionaryType, String dictionaryKey);

	String lookupKey(AdministeredDictionaryType dictionaryType, String dictionaryValue);

	boolean containsKey(AdministeredDictionaryType dictionaryType, String dictionaryKey);

	boolean containsValue(AdministeredDictionaryType dictionaryType, String dictionaryValue);

}
