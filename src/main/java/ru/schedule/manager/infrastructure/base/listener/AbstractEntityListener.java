package ru.schedule.manager.infrastructure.base.listener;

import java.io.Serializable;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;

import ru.schedule.manager.infrastructure.base.entity.AbstractEntity;

public class AbstractEntityListener implements Serializable {

	@PostLoad
	public void onLoad(final AbstractEntity abstractEntity) {
		abstractEntity.setNew(false);
	}

	@PostPersist
	public void postPersist(final AbstractEntity abstractEntity) {
		abstractEntity.setNew(false);
	}

}
