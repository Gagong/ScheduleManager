package ru.schedule.manager.infrastructure.base.service;

import java.util.List;

import ru.schedule.manager.infrastructure.base.dto.BaseResponseDto;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;

public interface BaseServiceAware<E extends BaseEntity, D extends BaseResponseDto> {

	String UNSUPPORTED_OPERATION = "Операция не поддерживается";

	String UNSUPPORTED_UPDATE_ALREADY_EXISTS_VALUES_ERROR = "Данные по указанным значениям уже существуют %s";

	String UNSUPPORTED_CREATE_ALREADY_EXISTS_DICTIONARY_VALUES_ERROR = "Данная операция не поддерживает обновление существующих записей";

	D fromEntity(E entity);

	List<D> fromEntity(List<E> entities);

	D findById(Long id);

	D update(D dto);

	D create(D dto);

	void delete(D dto);

}
