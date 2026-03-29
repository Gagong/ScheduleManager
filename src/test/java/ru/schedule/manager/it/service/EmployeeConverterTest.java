package ru.schedule.manager.it.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.schedule.manager.infrastructure.base.dictionary.Roles;
import ru.schedule.manager.infrastructure.base.dto.EmployeeDto;
import ru.schedule.manager.infrastructure.base.entity.Employee;
import ru.schedule.manager.infrastructure.base.entity.Role;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.schedule.manager.infrastructure.base.dictionary.Roles.ROLE_ADMIN;
import static ru.schedule.manager.infrastructure.base.dictionary.Roles.ROLE_USER;

@ExtendWith(MockitoExtension.class)
class EmployeeConverterTest {

    @Test
    void fromEntity_WithValidEmployee_ShouldConvertToDto() {
        // Arrange
        final LocalDateTime now = LocalDateTime.now();
        final Employee createdBy = Employee.builder()
                .id(10L)
                .fullName("Creator Name")
                .build();

        final Employee updatedBy = Employee.builder()
                .id(20L)
                .fullName("Updater Name")
                .build();

        final Role adminRole = Role.builder()
                .id(1L)
                .name(ROLE_ADMIN)
                .build();

        final Role userRole = Role.builder()
                .id(2L)
                .name(ROLE_USER)
                .build();

        final Set<Role> roles = new HashSet<>(Arrays.asList(adminRole, userRole));

        final Employee employee = Employee.builder()
                .id(100L)
                .createdDateTime(now)
                .updateDateTime(now.plusHours(1))
                .createdByEmployee(createdBy)
                .updatedByEmployee(updatedBy)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .roles(roles)
                .enabled(true)
                .build();

        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getCreatedDateTime()).isEqualTo(now);
        assertThat(result.getUpdateDateTime()).isEqualTo(now.plusHours(1));
        assertThat(result.getCreatedBy()).isEqualTo("Creator Name");
        assertThat(result.getUpdatedBy()).isEqualTo("Updater Name");
        assertThat(result.getUsername()).isEqualTo("john.doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(result.isEnabled()).isTrue();
    }

    @ParameterizedTest
    @NullSource
    void fromEntity_WithNullEmployee_ShouldReturnEmptyDto(final NullEmployee nullEmployee) {
        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getUsername()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getFullName()).isNull();
        assertThat(result.getRoles()).isNull();
        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    void fromEntity_WithNullEmployee_ShouldReturnEmptyDto() {
        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getUsername()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getFullName()).isNull();
        assertThat(result.getRoles()).isNull();
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getUpdatedBy()).isNull();
    }

    @Test
    void fromEntity_WithEmployeeHavingNullCreatedByEmployee_ShouldHandleGracefully() {
        // Arrange
        final Employee updatedBy = Employee.builder()
                .id(20L)
                .fullName("Updater Name")
                .build();

        final Employee employee = Employee.builder()
                .id(100L)
                .createdByEmployee(null)
                .updatedByEmployee(updatedBy)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .roles(Collections.emptySet())
                .enabled(true)
                .build();
        employee.setCreatedByEmployee(employee);
        employee.setUpdatedByEmployee(employee);

        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCreatedBy()).isNotNull();
        assertThat(result.getUpdatedBy()).isEqualTo("John Doe");
    }

    @Test
    void fromEntity_WithEmployeeHavingNullUpdatedByEmployee_ShouldHandleGracefully() {
        // Arrange
        final Employee createdBy = Employee.builder()
                .id(10L)
                .fullName("Creator Name")
                .build();

        final Employee employee = Employee.builder()
                .id(100L)
                .createdByEmployee(createdBy)
                .updatedByEmployee(null)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .roles(Collections.emptySet())
                .enabled(true)
                .build();

        employee.setCreatedByEmployee(employee);
        employee.setUpdatedByEmployee(employee);

        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCreatedBy()).isEqualTo("John Doe");
        assertThat(result.getUpdatedBy()).isNotNull();
    }

    @Test
    void fromEntity_WithEmployeeHavingNullRoles_ShouldReturnNullRoles() {
        // Arrange
        final Employee employee = Employee.builder()
                .id(100L)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .roles(new HashSet<>())
                .enabled(true)
                .build();

        employee.setCreatedByEmployee(employee);
        employee.setUpdatedByEmployee(employee);

        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRoles()).isEmpty();
    }

    @Test
    void fromEntity_WithEmployeeHavingEmptyRoles_ShouldReturnEmptySet() {
        // Arrange
        final Employee employee = Employee.builder()
                .id(100L)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .roles(Collections.emptySet())
                .enabled(true)
                .build();
        employee.setCreatedByEmployee(employee);
        employee.setUpdatedByEmployee(employee);

        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRoles()).isEmpty();
    }

    @Test
    void fromEntity_WithEmployeeHavingSingleRole_ShouldConvertCorrectly() {
        // Arrange
        final Role role = Role.builder()
                .id(1L)
                .name(ROLE_ADMIN)
                .build();

        final Employee employee = Employee.builder()
                .id(100L)
                .username("admin.user")
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .fullName("Admin User")
                .roles(Collections.singleton(role))
                .enabled(true)
                .build();
        employee.setCreatedByEmployee(employee);
        employee.setUpdatedByEmployee(employee);
        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRoles()).hasSize(1);
        assertThat(result.getRoles()).containsExactly("ROLE_ADMIN");
    }

    @ParameterizedTest
    @MethodSource("provideEmployeeData")
    void fromEntity_WithVariousEmployeeData_ShouldConvertCorrectly(
            final Long id,
            final String username,
            final String email,
            final String firstName,
            final String lastName,
            final String fullName,
            final boolean enabled,
            final Set<Roles> expectedRoles) {

        // Arrange
        final Set<Role> roles = new HashSet<>();
        for (final Roles roleName : expectedRoles) {
            roles.add(Role.builder().id(1L).name(roleName).build());
        }

        final Employee employee = Employee.builder()
                .id(id)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(fullName)
                .roles(roles)
                .enabled(enabled)
                .build();

        employee.setCreatedByEmployee(employee);
        employee.setUpdatedByEmployee(employee);

        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getFirstName()).isEqualTo(firstName);
        assertThat(result.getLastName()).isEqualTo(lastName);
        assertThat(result.getFullName()).isEqualTo(fullName);
        assertThat(result.isEnabled()).isEqualTo(enabled);
    }

    private static Stream<Arguments> provideEmployeeData() {
        return Stream.of(
                Arguments.of(1L, "user1", "user1@test.com", "User", "One", "User One", true, Set.of(ROLE_USER)),
                Arguments.of(2L, "admin", "admin@test.com", "Admin", "User", "Admin User", true, Set.of(ROLE_ADMIN, ROLE_USER)),
                Arguments.of(3L, "guest", "guest@test.com", "Guest", "Account", "Guest Account", false, Set.of(ROLE_USER)),
                Arguments.of(4L, "inactive", "inactive@test.com", "Inactive", "User", "Inactive User", false, Collections.emptySet())
        );
    }

    @Test
    void fromEntity_ShouldNotModifyOriginalEmployee() {
        // Arrange
        final LocalDateTime now = LocalDateTime.now();
        final Employee employee = Employee.builder()
                .id(100L)
                .createdDateTime(now)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .roles(Collections.emptySet())
                .enabled(true)
                .build();

        final Employee originalCopy = Employee.builder()
                .id(employee.getId())
                .createdDateTime(employee.getCreatedDateTime())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .roles(employee.getRoles())
                .enabled(employee.isEnabled())
                .build();

        employee.setCreatedByEmployee(employee);
        employee.setUpdatedByEmployee(employee);

        // Act
        final EmployeeDto result = EmployeeDto.fromEntity(employee);

        // Assert
        assertThat(result.getId()).isEqualTo(originalCopy.getId());
        assertThat(result.getUsername()).isEqualTo(originalCopy.getUsername());
        assertThat(result.getEmail()).isEqualTo(originalCopy.getEmail());
        assertThat(result.getFirstName()).isEqualTo(originalCopy.getFirstName());
        assertThat(result.getLastName()).isEqualTo(originalCopy.getLastName());
        assertThat(result.getFullName()).isEqualTo(originalCopy.getFullName());
        assertThat(result.isEnabled()).isEqualTo(originalCopy.isEnabled());
    }

    // Helper class for NullSource
    private static class NullEmployee {
        // Empty class for NullSource parameterized test
    }

}
