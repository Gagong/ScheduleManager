package ru.schedule.manager.infrastructure.base.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import ru.schedule.manager.infrastructure.base.listener.BaseEntityListener;

@Audited
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@DiscriminatorOptions(insert = false)
@EntityListeners(BaseEntityListener.class)
@EqualsAndHashCode(of = "id", callSuper = false)
public abstract class BaseEntity extends AbstractEntity implements Serializable {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	@GenericGenerator(
		name = "ext_hibernate_sequence",
		strategy = "increment",
		parameters = {
			@Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "ext_hibernate_sequence"),
			@Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1"),
			@Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "10000")
		}
	)
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@Builder.Default
	@CreationTimestamp
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "created_date")
	protected LocalDateTime createdDateTime = LocalDateTime.now();

	@Builder.Default
	@UpdateTimestamp
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "update_date")
	protected LocalDateTime updateDateTime = LocalDateTime.now();

	@Override
	public String toString() {
		return "BaseEntity{"
			+ "class="
			+ this.getClass().getSimpleName()
			+ "id="
			+ id
			+ '}';
	}

}
