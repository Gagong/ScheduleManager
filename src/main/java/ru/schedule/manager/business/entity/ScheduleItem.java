package ru.schedule.manager.business.entity;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ObjectUtils;
import ru.schedule.manager.business.dictionary.Times;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.serializer.BaseEntitySerializer;

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

	@Column(name = "row", nullable = false)
	private Integer row;

	@Column(name = "col", nullable = false)
	private Integer col;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "times", nullable = false)
	private Times times;

	@JsonAnyGetter
	public String getValue() {
		if (ObjectUtils.allNull(classroom, professor, disciplineType, discipline)) {
			return "Нажмите для заполнения";
		}
		return Stream.of(
			classroom,
			professor,
			disciplineType,
			discipline
		).map(Dictionary::getDictionaryValue).collect(Collectors.joining(", "));
	}

}
