package ru.schedule.manager.infrastructure.base.dictionary.administered.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;
import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.repository.DictionaryRepository;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import static ru.schedule.manager.business.exception.ExceptionMessageUtils.DICTIONARY_KEY_NOT_FOUND_EXCEPTION_PATTERN;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN;

@Service
public class AdministeredDictionaryService implements BaseServiceAware<Dictionary, DictionaryDto>, IAdministeredDictionary {

	private static final String NULL_ARGUMENTS_ERROR = "Отсутствуют значения для поиска справочника";

	private static final String UNSUPPORTED_CREATE_ALREADY_EXISTS_DICTIONARY_VALUES_ERROR = "Данная операция не поддерживает обновление существующих записей";

	private static final String UNSUPPORTED_UPDATE_ALREADY_EXISTS_VALUES_ERROR = "Данные по указанным значениям уже существуют %s";

	private final DictionaryRepository dictionaryRepository;

	public AdministeredDictionaryService(final DictionaryRepository dictionaryRepository) {
		this.dictionaryRepository = dictionaryRepository;
		IAdministeredDictionary.INSTANCE.set(this);
	}

	@Override
	public DictionaryDto fromEntity(final Dictionary entity) {
		return DictionaryDto.builder()
			.type(entity.getDictionaryType().getDictionaryKey())
			.key(entity.getDictionaryKey())
			.value(entity.getDictionaryValue())
			.id(entity.getId())
			.createdDateTime(entity.getCreatedDateTime())
			.updateDateTime(entity.getUpdateDateTime())
			.build();
	}

	@Override
	public List<DictionaryDto> fromEntity(final List<Dictionary> entities) {
		return entities.stream().filter(Objects::nonNull).map(this::fromEntity).collect(Collectors.toList());
	}

	@Override
	public DictionaryDto findById(final Long id) {
		return this.fromEntity(
			dictionaryRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
					ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
					Dictionary.class.getSimpleName(),
					id
				)))
		);
	}

	@Override
	@Transactional
	public DictionaryDto update(final DictionaryDto dto) {
		final Dictionary entity = dictionaryRepository.findById(dto.getId())
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
				ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
				Dictionary.class.getSimpleName(),
				dto.getId()
			)));
		if (containsKey(entity.getDictionaryType(), dto.getKey()) && containsValue(entity.getDictionaryType(), dto.getValue())) {
			throw new UnsupportedOperationException(String.format(UNSUPPORTED_UPDATE_ALREADY_EXISTS_VALUES_ERROR, entity));
		}
		return this.fromEntity(
			dictionaryRepository.findById(dto.getId())
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
					ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
					Dictionary.class.getSimpleName(),
					dto.getId()
				)))
				.setDictionaryValue(dto.getValue())
				.setDictionaryKey(dto.getKey())
		);
	}

	@Override
	public DictionaryDto create(final DictionaryDto dto) {
		final AdministeredDictionaryType type = AdministeredDictionaryType.valueOf(dto.getType());
		if (containsKey(type, dto.getKey()) || containsValue(type, dto.getValue())) {
			throw new UnsupportedOperationException(UNSUPPORTED_CREATE_ALREADY_EXISTS_DICTIONARY_VALUES_ERROR);
		}
		return this.fromEntity(
			dictionaryRepository.save(
				Dictionary.builder()
					.dictionaryType(type)
					.dictionaryKey(dto.getKey())
					.dictionaryValue(dto.getValue())
					.build()
			)
		);
	}

	@Override
	public void delete(final DictionaryDto dto) {
		dictionaryRepository.findById(dto.getId()).ifPresentOrElse(dictionaryRepository::delete, () -> {
			throw new EntityNotFoundException(ExceptionMessageUtils.of(
				ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
				Dictionary.class.getSimpleName(),
				dto.getId()
			));
		});
	}

	@Override
	public String lookupValue(final AdministeredDictionaryType dictionaryType, final String dictionaryKey) {
		if (ObjectUtils.allNull(dictionaryType, dictionaryKey)) {
			throw new NullPointerException(NULL_ARGUMENTS_ERROR);
		}
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKey(dictionaryType, dictionaryKey).map(Dictionary::getDictionaryValue)
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
				DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN,
				dictionaryType,
				dictionaryKey
			)));
	}

	@Override
	public String lookupKey(final AdministeredDictionaryType dictionaryType, final String dictionaryValue) {
		if (ObjectUtils.allNull(dictionaryType, dictionaryValue)) {
			throw new NullPointerException(NULL_ARGUMENTS_ERROR);
		}
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValue(dictionaryType, dictionaryValue)
			.map(Dictionary::getDictionaryKey)
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
				DICTIONARY_KEY_NOT_FOUND_EXCEPTION_PATTERN,
				dictionaryType,
				dictionaryValue
			)));
	}

	@Override
	public boolean containsKey(final AdministeredDictionaryType dictionaryType, final String dictionaryKey) {
		if (ObjectUtils.allNull(dictionaryType, dictionaryKey)) {
			throw new NullPointerException(NULL_ARGUMENTS_ERROR);
		}
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKey(dictionaryType, dictionaryKey).isPresent();
	}

	@Override
	public boolean containsValue(final AdministeredDictionaryType dictionaryType, final String dictionaryValue) {
		if (ObjectUtils.allNull(dictionaryType, dictionaryValue)) {
			throw new NullPointerException(NULL_ARGUMENTS_ERROR);
		}
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValue(dictionaryType, dictionaryValue).isPresent();
	}

	public List<DictionaryDto> getAllByType(final AdministeredDictionaryType type) {
		return this.fromEntity(dictionaryRepository.findAllByDictionaryType(type));
	}

	public DictionaryDto getByTypeAndKey(final AdministeredDictionaryType type, final String key) {
		return this.fromEntity(
			dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKey(type, key)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN, type, key)))
		);
	}

}
