package ru.schedule.manager.infrastructure.base.dictionary.administered.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import ru.schedule.manager.infrastructure.base.dictionary.administered.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "dictionary")
@Table(name = "dictionary")
public class Dictionary extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "dictionary_type", nullable = false, updatable = false)
	private AdministeredDictionaryType dictionaryType;

	@Column(name = "dictionary_key", nullable = false)
	private String dictionaryKey;

	@Column(name = "dictionary_value", nullable = false)
	private String dictionaryValue;

	@Override
	public String toString() {
		return "DictionaryService{"
			+ "dictionaryType="
			+ dictionaryType
			+ ", dictionaryKey='"
			+ dictionaryKey
			+ '\''
			+ ", dictionaryValue='"
			+ dictionaryValue
			+ '\''
			+ ", id="
			+ id + '}';
	}

}
