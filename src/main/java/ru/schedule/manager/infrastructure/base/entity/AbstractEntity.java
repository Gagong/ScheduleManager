package ru.schedule.manager.infrastructure.base.entity;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import ru.schedule.manager.infrastructure.base.listener.AbstractEntityListener;

@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AbstractEntityListener.class)
public abstract class AbstractEntity {

	@Transient
	@Builder.Default
	protected boolean isNew = true;

	@Override
	public String toString() {
		return "AbstractEntity {class=" + this.getClass().getSimpleName() + "}";
	}

}
