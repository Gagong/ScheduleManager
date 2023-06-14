package ru.schedule.manager.business.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessageUtils {

	public static final String ENTITY_NOT_FOUND_EXCEPTION_PATTERN = "Entity [%s] with ID [%s] not found";

	public static final String DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN = "DictionaryService with TYPE [%s] and KEY [%s] not found";

	public static final String DICTIONARY_KEY_NOT_FOUND_EXCEPTION_PATTERN = "DictionaryService with TYPE [%s] and VALUE [%s] not found";

	public static String of(final String pattern, final Object... args) {
		return String.format(pattern, args);
	}

}
