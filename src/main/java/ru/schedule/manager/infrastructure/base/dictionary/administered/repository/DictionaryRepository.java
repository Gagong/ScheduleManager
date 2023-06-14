package ru.schedule.manager.infrastructure.base.dictionary.administered.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {

	Optional<Dictionary> findDictionaryByDictionaryTypeAndDictionaryKey(AdministeredDictionaryType dictionaryType, String dictionaryKey);

	Optional<Dictionary> findDictionaryByDictionaryTypeAndDictionaryValue(AdministeredDictionaryType dictionaryType, String dictionaryValue);

	List<Dictionary> findAllByDictionaryType(AdministeredDictionaryType dictionaryType);

}
