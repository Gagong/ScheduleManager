package ru.schedule.manager.business.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.serializer.BaseEntitySerializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary.defaultSubgroup;
import static ru.schedule.manager.infrastructure.base.dictionary.administered.IAdministeredDictionary.isNotDefaultDictionary;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "schedule_item")
@Table(name = "schedule_item")
public class ScheduleItem extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "discipline", nullable = false)
	private Dictionary discipline;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "professor", nullable = false)
	private Dictionary professor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "classroom", nullable = false)
	private Dictionary classroom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "discipline_type", nullable = false)
	private Dictionary disciplineType;


	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "times", nullable = false)
	private Dictionary times;


	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "faculty", nullable = false)
	private Dictionary faculty;


	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "student_group", nullable = false)
	private Dictionary group;

	@Builder.Default
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "subgroup", nullable = false)
	private Dictionary subgroup = defaultSubgroup();

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "semester", nullable = false)
	private Dictionary semester;

	@Column(name = "row", nullable = false)
	private Integer row;

	@Column(name = "col", nullable = false)
	private Integer col;

	@Transient
	@Builder.Default
	private boolean editable = false;

	@JsonAnyGetter
	public String getValue() {
		if (ObjectUtils.anyNull(classroom, professor, disciplineType, discipline)) {
			return editable ? "Нажмите для заполнения" : StringUtils.EMPTY;
		}
		return Stream.of(
			classroom,
			professor,
			disciplineType,
			discipline
		).map(Dictionary::getDictionaryValue).collect(Collectors.joining(", "))
				+ Optional.ofNullable(subgroup)
				.filter(isNotDefaultDictionary())
				.map(Dictionary::getDictionaryValue)
				.map(value -> " (" +  value + ")")
				.orElse(StringUtils.EMPTY);
	}

}
