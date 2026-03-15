package ru.schedule.manager.it.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import ru.schedule.manager.business.controller.LoginController;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.repository.EmployeeRepository;
import ru.schedule.manager.infrastructure.base.request.LoginRequest;
import ru.schedule.manager.infrastructure.base.response.EmployeeResponse;
import ru.schedule.manager.infrastructure.base.service.CustomUserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private final CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(employeeRepository);

    @Test
    void login_ShouldReturnOkResponse() {
        when(employeeRepository.findByUsername(any())).thenReturn(Optional.of(new Employee()));

        final ResponseEntity<EmployeeResponse> response = loginController.login(new LoginRequest("admin", "admin"));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
