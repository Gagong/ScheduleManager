package ru.schedule.manager.infrastructure.base.dictionary.administered.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.business.exception.EntityNotFoundException;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;
import ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.dictionary.administered.repository.DictionaryRepository;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.schedule.manager.business.dictionary.AdministeredDictionaryType.SUBGROUP;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.DICTIONARY_KEY_NOT_FOUND_EXCEPTION_PATTERN;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN;
import static ru.schedule.manager.business.exception.ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN;

@Slf4j
@Service
public class AdministeredDictionaryService implements BaseServiceAware<Dictionary, DictionaryDto>, IAdministeredDictionary {

	private static final String NULL_ARGUMENTS_ERROR = "Отсутствуют значения для поиска справочника";

	private final DictionaryRepository dictionaryRepository;

	private final Map<AdministeredDictionaryType, Set<Triple<Long, Dictionary, DictionaryDto>>> cache;

	private final Map<Long, Pair<Dictionary, DictionaryDto>> idCache;

	public AdministeredDictionaryService(final DictionaryRepository dictionaryRepository) {
		this.dictionaryRepository = dictionaryRepository;
		final Pair<Map<AdministeredDictionaryType, Set<Triple<Long, Dictionary, DictionaryDto>>>, Map<Long, Pair<Dictionary, DictionaryDto>>> pair = this.getCache();
		this.cache = pair.getFirst();
		this.idCache = pair.getSecond();
		IAdministeredDictionary.INSTANCE.set(this);
		IAdministeredDictionary.DEFAULT_SUB_GROUP.set(this.dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(SUBGROUP, DEFAULT_KEY)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
						DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN,
						SUBGROUP,
						DEFAULT_KEY
				))));
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
			.createdBy(entity.getCreatedByEmployee().getFullName())
			.updatedBy(entity.getUpdatedByEmployee().getFullName())
			.build();
	}

	@Override
	public List<DictionaryDto> fromEntity(final List<Dictionary> entities) {
		return entities.stream()
			.filter(Objects::nonNull)
			.map(this::fromEntity)
			.sorted(Comparator.comparing(DictionaryDto::getDisplayOrder).thenComparing(DictionaryDto::getValue))
			.toList();
	}

	@Override
	public DictionaryDto findById(final Long id) {
		if (idCache.containsKey(id)) {
			return idCache.get(id).getSecond();
		}
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
	public DictionaryDto update(final DictionaryDto dto) {
		final Dictionary entity = this.getOneAsEntity(dto.getId());
		if (!dto.isActive() && entity.isActive()) {
			log.info("Обнаружено перемещение справочника {} в архив", entity);
		} else {
			if (containsKey(entity.getDictionaryType(), dto.getKey()) && containsValue(entity.getDictionaryType(), dto.getValue())) {
				throw new UnsupportedOperationException(String.format(UNSUPPORTED_UPDATE_ALREADY_EXISTS_VALUES_ERROR, entity));
			}
		}
		final Dictionary saved = dictionaryRepository.save(entity.setDictionaryValue(dto.getValue()).setDictionaryKey(dto.getKey()).setActive(dto.isActive()));
		this.upsertCache();
		return this.fromEntity(saved);
	}

	@Override
	public DictionaryDto create(final DictionaryDto dto) {
		final AdministeredDictionaryType type = AdministeredDictionaryType.valueOf(dto.getType());
		if (containsKey(type, dto.getKey()) || containsValue(type, dto.getValue())) {
			throw new UnsupportedOperationException(UNSUPPORTED_CREATE_ALREADY_EXISTS_DICTIONARY_VALUES_ERROR);
		}

		final Dictionary saved = dictionaryRepository.save(
				Optional.ofNullable(this.cache.get(type))
						.flatMap(localCache -> localCache.stream()
								.map(Triple::getMiddle)
								.filter(dictionary -> !dictionary.isActive())
								.filter(dictionary -> Objects.equals(dictionary.getDictionaryKey(), dto.getKey()))
								.findFirst())
						.or(() -> dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsFalse(type, dto.getKey()))
						.map(dictionary -> {
							log.info("Обнаружено возвращение справочника из архива {}", dto);
							dictionary.setActive(true);
							dictionary.setDictionaryValue(dto.getValue());
							return dictionary;
						})
						.or(() -> Optional.of(Dictionary.builder()
								.dictionaryType(type)
								.dictionaryKey(dto.getKey())
								.dictionaryValue(dto.getValue())
								.active(true)
								.displayOrder(dictionaryRepository.getNextOrder(type).map(val -> val + 1).orElse(0))
								.build()))
						.orElseThrow()
		);
		this.upsertCache();
		return this.fromEntity(saved);
	}

	@Override
	public void delete(final DictionaryDto dto) {
		final Dictionary entity = this.getOneAsEntity(dto.getId());
		entity.setActive(false);
		dictionaryRepository.save(entity);
		this.upsertCache();
	}

	@Override
	public String lookupValue(final AdministeredDictionaryType dictionaryType, final String dictionaryKey) {
		if (ObjectUtils.allNull(dictionaryType, dictionaryKey)) {
			throw new NullPointerException(NULL_ARGUMENTS_ERROR);
		}
		return Optional.ofNullable(this.cache.get(dictionaryType))
				.flatMap(set -> set.stream().filter(triple -> Objects.equals(triple.getMiddle().getDictionaryKey(), dictionaryKey) && triple.getMiddle().isActive()).map(Triple::getMiddle).findFirst())
				.or(() -> dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(dictionaryType, dictionaryKey))
				.filter(IS_NOT_DEFAULT)
				.map(Dictionary::getDictionaryValue)
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
		return Optional.ofNullable(this.cache.get(dictionaryType))
				.flatMap(set -> set.stream().filter(triple -> Objects.equals(triple.getMiddle().getDictionaryValue(), dictionaryValue) && triple.getMiddle().isActive()).map(Triple::getMiddle).findFirst())
				.or(() -> dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(dictionaryType, dictionaryValue))
				.filter(IS_NOT_DEFAULT)
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
		return Optional.ofNullable(this.cache.get(dictionaryType))
				.flatMap(set -> set.stream().filter(triple -> Objects.equals(triple.getMiddle().getDictionaryKey(), dictionaryKey) && triple.getMiddle().isActive()).map(Triple::getMiddle).findFirst())
				.or(() -> dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(dictionaryType, dictionaryKey))
				.filter(IS_NOT_DEFAULT)
				.isPresent();
	}

	@Override
	public boolean containsValue(final AdministeredDictionaryType dictionaryType, final String dictionaryValue) {
		if (ObjectUtils.allNull(dictionaryType, dictionaryValue)) {
			throw new NullPointerException(NULL_ARGUMENTS_ERROR);
		}
		return Optional.ofNullable(this.cache.get(dictionaryType))
				.flatMap(set -> set.stream().filter(triple -> Objects.equals(triple.getMiddle().getDictionaryValue(), dictionaryValue) && triple.getMiddle().isActive()).map(Triple::getMiddle).findFirst())
				.or(() -> dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(dictionaryType, dictionaryValue))
				.filter(IS_NOT_DEFAULT)
				.isPresent();
	}

	public List<DictionaryDto> getAllByType(final AdministeredDictionaryType type, final boolean onlyActive) {
		if (this.cache.containsKey(type)) {
			return this.cache.get(type).stream()
					.filter(triple -> IS_NOT_DEFAULT.test(triple.getMiddle()))
					.filter(triple -> triple.getMiddle().isActive() == onlyActive)
					.map(Triple::getRight)
					.toList();
		}
		return this.fromEntity(
				dictionaryRepository.findAllByDictionaryTypeOrderByDisplayOrderDesc(type).stream()
						.filter(IS_NOT_DEFAULT)
						.filter(dict -> dict.isActive() == onlyActive)
						.toList()
		);
	}

	public List<Dictionary> getAllEntitiesByType(final AdministeredDictionaryType type, final boolean onlyActive) {
		if (this.cache.containsKey(type)) {
			return this.cache.get(type).stream()
					.map(Triple::getMiddle)
					.filter(IS_NOT_DEFAULT)
					.filter(dictionary -> dictionary.isActive() == onlyActive)
					.toList();
		}
		return dictionaryRepository.findAllByDictionaryTypeOrderByDisplayOrderDesc(type).stream()
				.filter(IS_NOT_DEFAULT)
				.filter(dict -> dict.isActive() == onlyActive)
				.toList();
	}

	public DictionaryDto getByTypeAndKey(final AdministeredDictionaryType type, final String key) {
		if (this.cache.containsKey(type)) {
			return this.cache.get(type).stream()
					.filter(triple -> IS_NOT_DEFAULT.test(triple.getMiddle()))
					.filter(triple -> triple.getMiddle().isActive())
					.filter(triple -> Objects.equals(triple.getMiddle().getDictionaryKey(), key))
					.map(Triple::getRight)
					.findFirst()
					.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN, type, key)));
		}
		return this.fromEntity(
				dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(type, key)
						.filter(IS_NOT_DEFAULT)
						.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN, type, key)))
		);
	}

	public Dictionary getEntityByTypeAndKey(final AdministeredDictionaryType type, final String key) {
		if (this.cache.containsKey(type)) {
			return this.cache.get(type).stream()
					.map(Triple::getMiddle)
					.filter(IS_NOT_DEFAULT)
					.filter(Dictionary::isActive)
					.filter(dictionary -> Objects.equals(dictionary.getDictionaryKey(), key))
					.findFirst()
					.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN, type, key)));
		}
		return dictionaryRepository.findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(type, key)
				.filter(IS_NOT_DEFAULT)
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
		if (this.idCache.containsKey(dto.getId())) {
			return this.idCache.get(dto.getId()).getFirst();
		}
		return this.getOneAsEntity(dto.getId());
	}

	public Dictionary getOneAsEntity(@NonNull final Long id) {
		if (this.idCache.containsKey(id)) {
			return this.idCache.get(id).getFirst();
		}
		return dictionaryRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessageUtils.of(
						ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
						Dictionary.class.getSimpleName(),
						id
				)));
	}

	private Pair<Map<AdministeredDictionaryType, Set<Triple<Long, Dictionary, DictionaryDto>>>, Map<Long, Pair<Dictionary, DictionaryDto>>> getCache() {
		log.info("Инициализация кеша");
		final Map<AdministeredDictionaryType, Set<Triple<Long, Dictionary, DictionaryDto>>> cacheLoader = new EnumMap<>(AdministeredDictionaryType.class);
		final Map<Long, Pair<Dictionary, DictionaryDto>> idCacheLoader = new LinkedHashMap<>();

		final List<Triple<Long, Dictionary, DictionaryDto>> allTriples = dictionaryRepository.findAll().stream()
				.map(dictionary -> Triple.of(dictionary.getId(), dictionary, this.fromEntity(dictionary)))
				.sorted(Comparator.comparing((Triple<Long, Dictionary, DictionaryDto> triple) -> triple.getMiddle().getDisplayOrder()).thenComparing(triple -> triple.getRight().getValue()))
				.toList();

		allTriples.forEach(triple -> {
			final AdministeredDictionaryType type = triple.getMiddle().getDictionaryType();
			if (cacheLoader.containsKey(type)) {
				cacheLoader.get(type).add(triple);
			} else {
				cacheLoader.put(type, new LinkedHashSet<>(List.of(triple)));
			}
			if (!idCacheLoader.containsKey(triple.getLeft())) {
				idCacheLoader.put(triple.getLeft(), Pair.of(triple.getMiddle(), triple.getRight()));
			}
		});

		for (final Map.Entry<AdministeredDictionaryType, Set<Triple<Long, Dictionary, DictionaryDto>>> entry : cacheLoader.entrySet()) {
			final Set<Triple<Long, Dictionary, DictionaryDto>> sortedSet = entry.getValue().stream()
					.sorted(Comparator.comparing((Triple<Long, Dictionary, DictionaryDto> triple) -> triple.getMiddle().getDisplayOrder()).thenComparing(triple -> triple.getRight().getValue()))
					.collect(Collectors.toCollection(LinkedHashSet::new));
			entry.setValue(sortedSet);
		}

		log.info("Кеш инициализирован [{}, {}]", cacheLoader.size(), idCacheLoader.size());
		return Pair.of(cacheLoader, idCacheLoader);
	}

	private void upsertCache() {
		log.info("Запущено обновление кеша");
		final Pair<Map<AdministeredDictionaryType, Set<Triple<Long, Dictionary, DictionaryDto>>>, Map<Long, Pair<Dictionary, DictionaryDto>>> pair = this.getCache();
		this.cache.clear();
		this.idCache.clear();
		this.cache.putAll(pair.getFirst());
		this.idCache.putAll(pair.getSecond());
		log.info("Завершено обновление кеша [{}, {}]", pair.getFirst().size(), pair.getSecond().size());
	}

}
