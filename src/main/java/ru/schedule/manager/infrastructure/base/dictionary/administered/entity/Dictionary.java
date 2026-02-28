package ru.schedule.manager.infrastructure.base.dictionary.administered.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

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

	@Column(name = "dictionary_key", nullable = false, updatable = false)
	private String dictionaryKey;

	@Column(name = "dictionary_value", nullable = false)
	private String dictionaryValue;

	@Builder.Default
	@Column(name = "active", nullable = false)
	@ColumnDefault("true")
	private boolean active = true;

	@Builder.Default
	@Column(name = "display_order")
	@ColumnDefault("0")
	private int displayOrder = 0;

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
			+ ", active='"
			+ active
			+ '\''
			+ ", id="
			+ id + '}';
	}

}
