package ru.schedule.manager.infrastructure.base.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.entity.Role;
import ru.schedule.manager.infrastructure.base.repository.EmployeeRepository;
import ru.schedule.manager.infrastructure.base.repository.RoleRepository;
import ru.schedule.manager.infrastructure.base.request.RegisterRequest;
import ru.schedule.manager.infrastructure.base.response.EmployeeDto;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final RoleRepository roleRepository;

    private final CustomUserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeDto registerUser(final RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Проверка уникальности
        if (userDetailsService.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userDetailsService.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Получаем роль USER по умолчанию
        final Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));

        // Создаем пользователя
        final Employee user = Employee.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(userRole))
                .build();

        final Employee savedUser = employeeRepository.save(user);
        log.info("User registered successfully: {}", savedUser);

        return EmployeeDto.fromEntity(savedUser);
    }

}
