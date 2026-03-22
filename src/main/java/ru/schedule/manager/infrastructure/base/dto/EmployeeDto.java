package ru.schedule.manager.infrastructure.base.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.entity.Role;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class EmployeeDto extends BaseResponseDto {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String middleName;

    private String fullName;

    private Set<String> roles;

    private boolean enabled;

    public static EmployeeDto fromEntity(final Employee employee) {
        if (employee == null) {
            return new EmployeeDto();
        }
        return EmployeeDto.builder()
                .id(employee.getId())
                .createdDateTime(employee.getCreatedDateTime())
                .updateDateTime(employee.getUpdateDateTime())
                .createdBy(employee.getCreatedByEmployee().getFullName())
                .updatedBy(employee.getUpdatedByEmployee().getFullName())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .roles(employee.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .enabled(employee.isEnabled())
                .build();
    }

}
