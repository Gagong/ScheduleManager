package ru.schedule.manager.infrastructure.base.dictionary.administered.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {

	Optional<Dictionary> findDictionaryByDictionaryTypeAndDictionaryKeyAndActiveIsTrue(AdministeredDictionaryType dictionaryType, String dictionaryKey);

	Optional<Dictionary> findDictionaryByDictionaryTypeAndDictionaryValueAndActiveIsTrue(AdministeredDictionaryType dictionaryType, String dictionaryValue);

	List<Dictionary> findAllByDictionaryTypeOrderByDisplayOrderDesc(AdministeredDictionaryType dictionaryType);

	@Query("select max(displayOrder) from dictionary where dictionaryType = :type")
	Optional<Integer> getNextOrder(AdministeredDictionaryType type);

}
