package ru.schedule.manager.business.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.SimpleDictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.service.AdministeredDictionaryService;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "dictionary")
public class DictionaryController {

	private final AdministeredDictionaryService administeredDictionaryService;

	@DeleteMapping("delete")
	public void delete(@RequestBody final DictionaryDto dto) {
		administeredDictionaryService.delete(dto);
	}

	@PostMapping("create")
	public DictionaryDto create(@RequestBody final DictionaryDto dto) {
		return administeredDictionaryService.create(dto);
	}

	@PatchMapping("update")
	public DictionaryDto update(@RequestBody final DictionaryDto dto) {
		return administeredDictionaryService.update(dto);
	}

	@GetMapping("getAllByType")
	public List<DictionaryDto> getAllByType(@RequestParam final AdministeredDictionaryType type) {
		return administeredDictionaryService.getAllByType(type);
	}

	@GetMapping("getByTypeAndKey")
	public DictionaryDto getByTypeAndKey(@RequestParam final AdministeredDictionaryType type, @RequestParam final String key) {
		return administeredDictionaryService.getByTypeAndKey(type, key);
	}

	@GetMapping("containsKey")
	public Boolean containsKey(@RequestParam final AdministeredDictionaryType type, @RequestParam final String key) {
		return administeredDictionaryService.containsKey(type, key);
	}

	@GetMapping("containsValue")
	public Boolean containsValue(@RequestParam final AdministeredDictionaryType type, @RequestParam final String value) {
		return administeredDictionaryService.containsValue(type, value);
	}

	@GetMapping("getAllTypes")
	public Set<String> getAllTypes() {
		return Arrays.stream(AdministeredDictionaryType.values()).map(AdministeredDictionaryType::getDictionaryKey).collect(Collectors.toSet());
	}

	@GetMapping("getAll")
	public List<SimpleDictionary> getAll() {
		return Arrays.stream(AdministeredDictionaryType.values())
			.map(type -> new SimpleDictionary(type.getDictionaryKey(), type.getDictionaryValue()))
			.collect(Collectors.toList());
	}

}
