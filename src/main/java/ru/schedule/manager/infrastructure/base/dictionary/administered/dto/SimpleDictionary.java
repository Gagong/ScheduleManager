package ru.schedule.manager.infrastructure.base.dictionary.administered.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class SimpleDictionary implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String key;

	private final String value;

}
