package ru.schedule.manager.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.schedule.manager.business.dictionary.ProfileNavigator;
import ru.schedule.manager.infrastructure.base.dictionary.Roles;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.entity.Role;

import java.util.HashMap;
import java.util.Map;

import static ru.schedule.manager.business.dictionary.ProfileNavigator._4_EMPLOYEE;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "profile")
public class ProfileController {

	@GetMapping("getNavigator")
	public Map<String, Map<String, Object>> getNavigator(@AuthenticationPrincipal final Employee employee) {
		final Map<String, Map<String, Object>> result;
		if (employee == null) {
			result = new HashMap<>(ProfileNavigator.getUnsecured());
		} else {
			result = new HashMap<>(ProfileNavigator.getAll());
			if (employee.getRoles().stream().map(Role::getName).noneMatch(role -> role.equals(Roles.ROLE_ADMIN.name()))) {
				result.remove(_4_EMPLOYEE.getDictionaryKey());
			}
		}
		return result;
	}

}
