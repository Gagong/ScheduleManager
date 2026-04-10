package ru.schedule.manager.infrastructure.base.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.schedule.manager.infrastructure.base.dictionary.Roles;
import ru.schedule.manager.infrastructure.base.dto.EmployeeDto;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.entity.Role;
import ru.schedule.manager.infrastructure.base.repository.EmployeeRepository;
import ru.schedule.manager.infrastructure.base.repository.RoleRepository;
import ru.schedule.manager.infrastructure.base.request.EmployeeDataRequest;

import java.util.List;
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
    public EmployeeDto registerUser(final EmployeeDataRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        if (userDetailsService.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Пользователь с логином: " + request.getUsername() + " уже существует.");
        }
        if (userDetailsService.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с E-Mail: " + request.getEmail() + " уже существует.");
        }

        final Role userRole = roleRepository.findByName(Roles.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));

        final Employee user = Employee.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .roles(Set.of(userRole))
                .build();

        final Employee savedUser = employeeRepository.save(user);
        log.info("User registered successfully: {}", savedUser);

        return EmployeeDto.fromEntity(savedUser);
    }

    public List<EmployeeDto> getEmployees() {
        return employeeRepository.findAll().stream().map(EmployeeDto::fromEntity).toList();
    }

    @Transactional
    public EmployeeDto toggleEmployee(final Long id) {
        return employeeRepository.findById(id).map(employee -> employee.setEnabled(!employee.isEnabled())).map(EmployeeDto::fromEntity).orElse(null);
    }

    @Transactional
    public EmployeeDto updateEmployee(final Long id, final EmployeeDataRequest request) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setFirstName(request.getFirstName());
            employee.setLastName(request.getLastName());
            employee.setMiddleName(request.getMiddleName());
            employee.setEmail(request.getEmail());
            employee.setUsername(request.getUsername());
            return employee;
        }).map(EmployeeDto::fromEntity).orElse(null);
    }

}
