package ru.schedule.manager.it.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.schedule.manager.business.controller.LoginController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Test
    void login_ShouldReturnOkResponse() {
        final ResponseEntity<Void> response = loginController.login();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
