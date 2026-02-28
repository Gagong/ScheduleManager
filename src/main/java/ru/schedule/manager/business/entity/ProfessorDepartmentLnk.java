package ru.schedule.manager.business.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.serializer.BaseEntitySerializer;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "professor_department_lnk")
@Table(name = "professor_department_lnk")
public class ProfessorDepartmentLnk extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "department", nullable = false)
	private Dictionary department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonSerialize(using = BaseEntitySerializer.class)
	@JoinColumn(name = "professor", nullable = false)
	private Dictionary professor;

}
