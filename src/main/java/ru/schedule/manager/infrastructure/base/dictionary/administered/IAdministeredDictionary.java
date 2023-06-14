package ru.schedule.manager.infrastructure.base.dictionary.administered;

import java.util.concurrent.atomic.AtomicReference;

public interface IAdministeredDictionary {

	AtomicReference<IAdministeredDictionary> INSTANCE = new AtomicReference<>();

	static IAdministeredDictionary dictionary() {
		return INSTANCE.get();
	}

	String lookupValue(AdministeredDictionaryType dictionaryType, String dictionaryKey);

	String lookupKey(AdministeredDictionaryType dictionaryType, String dictionaryValue);

	boolean containsKey(AdministeredDictionaryType dictionaryType, String dictionaryKey);

	boolean containsValue(AdministeredDictionaryType dictionaryType, String dictionaryValue);

}
