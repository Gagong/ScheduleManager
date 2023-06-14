package ru.schedule.manager.business.exception;

public class EntityNotFoundException extends RuntimeException {

	public EntityNotFoundException(final String message) {
		super(message);
	}

}
