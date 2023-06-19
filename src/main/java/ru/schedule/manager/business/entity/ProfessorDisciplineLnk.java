package ru.schedule.manager.business.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.serializer.BaseEntitySerializer;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "professor_discipline_lnk")
@Table(name = "professor_discipline_lnk")
public class ProfessorDisciplineLnk extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "discipline", nullable = false)
	private Dictionary discipline;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "professor", nullable = false)
	private Dictionary professor;

}
