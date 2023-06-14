package ru.schedule.manager.infrastructure.base.listener;

import java.io.Serializable;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.stereotype.Component;

import ru.schedule.manager.infrastructure.base.entity.BaseEntity;

@Component
public class BaseEntityListener implements Serializable {

	@PostLoad
	public void onLoad(final BaseEntity baseEntity) {
		//Empty now
	}

	@PrePersist
	public void onCreate(final BaseEntity baseEntity) {
		//Empty now
	}

	@PreUpdate
	public void onUpdate(final BaseEntity baseEntity) {
		//Empty now
	}

}
