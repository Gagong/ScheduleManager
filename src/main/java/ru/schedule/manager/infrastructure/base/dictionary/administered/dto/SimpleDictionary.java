package ru.schedule.manager.infrastructure.base.dictionary.administered.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleDictionary implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String key;

	private final String value;

}
