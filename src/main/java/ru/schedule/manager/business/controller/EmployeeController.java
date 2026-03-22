package ru.schedule.manager.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.request.LoginRequest;
import ru.schedule.manager.infrastructure.base.request.RegisterRequest;
import ru.schedule.manager.infrastructure.base.response.EmployeeDto;
import ru.schedule.manager.infrastructure.base.service.CustomUserDetailsService;
import ru.schedule.manager.infrastructure.base.service.EmployeeService;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_API_PATH + "employee")
public class EmployeeController {

	private final AuthenticationManager authenticationManager;

	private final CustomUserDetailsService userDetailsService;

	private final EmployeeService userService;

	@PostMapping("/login")
	public ResponseEntity<EmployeeDto> login(@Validated @RequestBody final LoginRequest loginRequest) {
		log.info("Login attempt for user: {}", loginRequest.getUsername());

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(),
						loginRequest.getPassword()
				)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		final Employee user = userDetailsService.loadUserEntityByUsername(loginRequest.getUsername());

		return ResponseEntity.ok(EmployeeDto.fromEntity(user));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/register")
	public ResponseEntity<EmployeeDto> register(@Validated @RequestBody final RegisterRequest registerRequest) {
		return ResponseEntity.ok(userService.registerUser(registerRequest));
	}

	@GetMapping("/me")
	public ResponseEntity<EmployeeDto> getCurrentUser(final Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.ok(null);
		}

		final Employee user = (Employee) authentication.getPrincipal();
		return ResponseEntity.ok(EmployeeDto.fromEntity(user));
	}

}
