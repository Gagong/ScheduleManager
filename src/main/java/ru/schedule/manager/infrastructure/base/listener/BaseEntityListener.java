package ru.schedule.manager.infrastructure.base.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.repository.EmployeeRepository;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Optional;

@Component
public class BaseEntityListener implements Serializable {

	@Autowired
	private ApplicationContext context;

	@PostLoad
	public void onLoad(final BaseEntity baseEntity) {
		baseEntity.setLoadVstamp(baseEntity.getVstamp());
	}

	@PrePersist
	public void onCreate(final BaseEntity baseEntity) {
		final Employee employee = getAuthEmployee();
		baseEntity.setCreatedByEmployee(employee);
		baseEntity.setUpdatedByEmployee(employee);
	}

	@PreUpdate
	public void onUpdate(final BaseEntity baseEntity) {
		final Employee employee = getAuthEmployee();
		baseEntity.setUpdatedByEmployee(employee);
	}

	private Employee getAuthEmployee() {
		return Optional.ofNullable(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getPrincipal)
				.map(Employee.class::cast)
				.orElse(context.getBean(EmployeeRepository.class).getReferenceById(1L));
	}

}
