package ru.schedule.manager.infrastructure.base.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Логин обязателен для заполнения")
    private String username;

    @NotBlank(message = "Пароль обязателен для заполнения")
    private String password;

}
