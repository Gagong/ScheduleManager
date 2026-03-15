package ru.schedule.manager.infrastructure.base.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.entity.Role;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String middleName;

    private String fullName;

    private Set<String> roles;

    private boolean enabled;

    public static EmployeeResponse fromEmployee(final Employee user) {
        if (user == null) {
            return new EmployeeResponse();
        }
        return EmployeeResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .enabled(user.isEnabled())
                .build();
    }

}
