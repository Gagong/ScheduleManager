package ru.schedule.manager.infrastructure.base.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.entity.Role;
import ru.schedule.manager.infrastructure.base.repository.EmployeeRepository;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        final Employee user = employeeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("Пользователь с логином: " + username + " не найден");
                });

        log.info("User loaded successfully: {}, roles: {}", username, user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        return user;
    }

    @Transactional(readOnly = true)
    public Employee loadUserEntityByUsername(final String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с логином: " + username + " не найден"));
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(final String username) {
        return employeeRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(final String email) {
        return employeeRepository.existsByEmail(email);
    }

}
