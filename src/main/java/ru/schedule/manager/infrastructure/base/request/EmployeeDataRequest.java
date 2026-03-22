package ru.schedule.manager.infrastructure.base.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class EmployeeDataRequest {

    @NotBlank(message = "Логин обязателен для заполнения")
    @Size(min = 3, max = 50, message = "Длина логина должна быть в диапазоне 3-50 символов")
    private String username;

    @NotBlank(message = "Пароль обязателен для заполнения")
    @Size(min = 8, max = 50, message = "Длина пароля должна быть в диапазоне 8-50 символов")
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "E-Mail обязателен для заполнения")
    private String email;

    @NotBlank(message = "Имя обязательно для заполнения")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна для заполнения")
    private String lastName;

    private String middleName;

}
