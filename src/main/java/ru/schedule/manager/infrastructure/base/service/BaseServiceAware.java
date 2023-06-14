package ru.schedule.manager.infrastructure.base.service;

import java.util.List;

import ru.schedule.manager.infrastructure.base.dto.BaseResponseDto;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;

public interface BaseServiceAware<E extends BaseEntity, D extends BaseResponseDto> {

	D fromEntity(E entity);

	List<D> fromEntity(List<E> entities);

	D findById(Long id);

	D update(D dto);

	D create(D dto);

	void delete(D dto);

}
