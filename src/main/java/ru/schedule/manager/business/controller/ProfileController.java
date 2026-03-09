package ru.schedule.manager.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.schedule.manager.business.dictionary.ProfileNavigator;

import java.util.Map;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "profile")
public class ProfileController {

	@GetMapping("getNavigator")
	public Map<String, Map<String, Object>> getNavigator(@AuthenticationPrincipal final UserDetails userDetails) {
		if (userDetails == null) {
			return ProfileNavigator.getUnsecured();
		} else {
			return ProfileNavigator.getAll();
		}
	}

}
