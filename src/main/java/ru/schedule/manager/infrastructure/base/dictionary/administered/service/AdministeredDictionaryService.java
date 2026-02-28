package ru.schedule.manager.infrastructure.base.dictionary.administered.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;
import ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.repository.DictionaryRepository;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.schedule.manager.business.dictionary.SemesterType.AUTUMN;
import static ru.schedule.manager.business.dictionary.SemesterType.SPRING;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.DICTIONARY_KEY_NOT_FOUND_EXCEPTION_PATTERN;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN;

@Slf4j
@Service
public class AdministeredDictionaryService implements BaseServiceAware<Dictionary, DictionaryDto>, IAdministeredDictionary {

	private static final String NULL_ARGUMENTS_ERROR = "Отсутствуют значения для поиска справочника";

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
			.active(entity.isActive())
			.displayOrder(entity.getDisplayOrder())
			.id(entity.getId())
			.createdDateTime(entity.getCreatedDateTime())
			.updateDateTime(entity.getUpdateDateTime())
			.build();
	}

	@Override
	public List<DictionaryDto> fromEntity(final List<Dictionary> entities) {
		return entities.stream()
			.filter(Objects::nonNull)
			.map(this::fromEntity)
			.sorted(Comparator.comparing(DictionaryDto::getDisplayOrder).thenComparing(DictionaryDto::getValue))
			.collect(Collectors.toList());
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
		if (!dto.isActive() && entity.isActive()) {
			log.info("Обнаружено перемещение справочника {} в архив", entity);
		} else {
			if (containsKey(entity.getDictionaryType(), dto.getKey()) && containsValue(entity.getDictionaryType(), dto.getValue())) {
				throw new UnsupportedOperationException(String.format(UNSUPPORTED_UPDATE_ALREADY_EXISTS_VALUES_ERROR, entity));
			}
		}
		return this.fromEntity(entity.setDictionaryValue(dto.getValue()).setDictionaryKey(dto.getKey()).setActive(dto.isActive()));
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
					.active(true)
					.displayOrder(dictionaryRepository.getNextOrder(type).map(val -> val + 1).orElse(0))
					.build()
			)
		);
	}

	@Override
	@Transactional
	public void delete(final DictionaryDto dto) {
		dictionaryRepository.findById(dto.getId()).ifPresentOrElse(entity -> entity.setActive(false), () -> {
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
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(dictionaryType, dictionaryKey).map(Dictionary::getDictionaryValue)
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
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(dictionaryType, dictionaryValue)
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
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(dictionaryType, dictionaryKey).isPresent();
	}

	@Override
	public boolean containsValue(final AdministeredDictionaryType dictionaryType, final String dictionaryValue) {
		if (ObjectUtils.allNull(dictionaryType, dictionaryValue)) {
			throw new NullPointerException(NULL_ARGUMENTS_ERROR);
		}
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(dictionaryType, dictionaryValue).isPresent();
	}

	public List<DictionaryDto> getAllByType(final AdministeredDictionaryType type, final boolean onlyActive) {
		return this.fromEntity(
				dictionaryRepository.findAllByDictionaryTypeOrderByDisplayOrderDesc(type).stream()
						.filter(dict -> dict.isActive() == onlyActive)
						.collect(Collectors.toList())
		);
	}

	public List<Dictionary> getAllEntitiesByType(final AdministeredDictionaryType type, final boolean onlyActive) {
		return dictionaryRepository.findAllByDictionaryTypeOrderByDisplayOrderDesc(type).stream()
				.filter(dict -> dict.isActive() == onlyActive)
				.collect(Collectors.toList());
	}

	public DictionaryDto getByTypeAndKey(final AdministeredDictionaryType type, final String key) {
		return this.fromEntity(
			dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(type, key)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN, type, key)))
		);
	}

	public Dictionary getEntityByTypeAndKey(final AdministeredDictionaryType type, final String key) {
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(type, key)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN, type, key)));
	}

	public void dictionaryEntityToDto(final Dictionary dictionary, final Consumer<DictionaryDto> consumer) {
		Optional.ofNullable(dictionary).map(this::fromEntity).ifPresent(consumer);
	}

	@Nullable
	public DictionaryDto dictionaryEntityToDto(final Dictionary dictionary) {
		return Optional.ofNullable(dictionary).map(this::fromEntity).orElse(null);
	}

	public Dictionary getOneAsEntity(@NonNull final DictionaryDto dto) {
		return Optional.ofNullable(dto.getId()).flatMap(dictionaryRepository::findById)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
						ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
						Dictionary.class.getSimpleName(),
						dto.getId()
				)));
	}

	public Dictionary getOneAsEntity(@NonNull final Long id) {
		return dictionaryRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
						ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
						Dictionary.class.getSimpleName(),
						id
				)));
	}

	@PostConstruct
	public void fillSemesters() {
		for (int i = LocalDate.now().getYear() - 10; i < LocalDate.now().getYear() + 100; i++) {
			try {
				this.create(
						DictionaryDto.builder()
								.type(AdministeredDictionaryType.SEMESTER.name())
								.key(AUTUMN.name() + "_" + i + "_" + (i + 1))
								.value(AUTUMN.getValue() + " " + i + "/" + (i + 1))
								.build()
				);
			} catch (final Exception e) {
				//Skip AlreadyExistsException
			}
			try {
				this.create(
						DictionaryDto.builder()
								.type(AdministeredDictionaryType.SEMESTER.name())
								.key(SPRING.name() + "_" + i + "_" + (i + 1))
								.value(SPRING.getValue() + " " + i + "/" + (i + 1))
								.build()
				);
			} catch (final Exception e) {
				//Skip AlreadyExistsException
			}
		}
	}

}
