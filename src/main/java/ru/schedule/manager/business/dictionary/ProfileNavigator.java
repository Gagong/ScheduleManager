package ru.schedule.manager.business.dictionary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.schedule.manager.infrastructure.base.dictionary.SimpleDictionary;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * !!!IMPORTANT!!!
 * <br>
 * DO NOT CHANGE NAMES
 * <br>
 * !!!IMPORTANT!!!
 */
@Getter
@SuppressWarnings("all")
@RequiredArgsConstructor
public enum ProfileNavigator implements Serializable, SimpleDictionary {

	_0_GENERAL_SCHEDULE("Расписание", "/", false),
	_1_CREATE_SCHEDULE("Составление расписания", "/create-schedule", true),
	_2_DICTIONARY("Справочники", "/dictionary", true),
	_3_PROFESSOR("Преподаватели", "/professor", true),
	_4_EMPLOYEE("Менеджеры", "/employee", true);

	private final String label;

	private final String value;

	private final boolean secured;

	@Override
	public String getDictionaryValue() {
		return this.value;
	}

	@Override
	public String getDictionaryKey() {
		return this.name();
	}

	public static Map<String, Map<String, Object>> getAll() {
		return Arrays.stream(ProfileNavigator.values())
				.collect(Collectors.toMap(
						ProfileNavigator::getDictionaryKey,
                        ProfileNavigator::toMap
				));
	}

	public static Map<String, Map<String, Object>> getUnsecured() {
		return Arrays.stream(ProfileNavigator.values())
				.filter(nav -> !nav.isSecured())
				.collect(Collectors.toMap(
						ProfileNavigator::getDictionaryKey,
						ProfileNavigator::toMap
				));
	}

	public static Map<String, Object> toMap(final ProfileNavigator nav) {
		return Map.of(
				"label", nav.getLabel(),
				"path", nav.getValue(),
				"secured", nav.isSecured()
		);
	}

}
