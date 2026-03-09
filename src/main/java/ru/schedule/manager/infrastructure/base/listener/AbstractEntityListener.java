package ru.schedule.manager.infrastructure.base.listener;

import ru.schedule.manager.infrastructure.base.entity.AbstractEntity;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import java.io.Serializable;

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
