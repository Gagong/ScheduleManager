package ru.schedule.manager.it.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.schedule.manager.business.controller.ProfileController;
import ru.schedule.manager.business.dictionary.ProfileNavigator;
import ru.schedule.manager.infrastructure.base.entity.Employee;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileControllerTest {

    @InjectMocks
    private ProfileController profileController;

    @Test
    void getNavigator_WhenUserNotAuthenticated_ShouldReturnUnsecured() {
        // Подготовка
        final Map<String, Map<String, Object>> expectedMap = Map.of(
                "item1", Map.of("label", "Public", "path", "/public", "secured", false)
        );

        try (MockedStatic<ProfileNavigator> mockedStatic = mockStatic(ProfileNavigator.class)) {
            mockedStatic.when(ProfileNavigator::getUnsecured).thenReturn(expectedMap);

            // Действие
            final Map<String, Map<String, Object>> result = profileController.getNavigator(null);

            // Проверка
            assertNotNull(result);
            assertEquals(expectedMap, result);
            mockedStatic.verify(ProfileNavigator::getUnsecured, times(1));
            mockedStatic.verify(ProfileNavigator::getAll, never());
        }
    }

    @Test
    void getNavigator_WhenUserAuthenticated_ShouldReturnAll() {
        // Подготовка
        final Employee userDetails = mock(Employee.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        final Map<String, Map<String, Object>> expectedMap = Map.of(
                "item1", Map.of("label", "Public", "path", "/public", "secured", false),
                "item2", Map.of("label", "Private", "path", "/private", "secured", true)
        );

        try (MockedStatic<ProfileNavigator> mockedStatic = mockStatic(ProfileNavigator.class)) {
            mockedStatic.when(ProfileNavigator::getAll).thenReturn(expectedMap);

            // Действие
            final Map<String, Map<String, Object>> result = profileController.getNavigator(userDetails);

            // Проверка
            assertNotNull(result);
            assertEquals(expectedMap, result);
            mockedStatic.verify(ProfileNavigator::getAll, times(1));
            mockedStatic.verify(ProfileNavigator::getUnsecured, never());
        }
    }

    @Test
    void getNavigator_WhenUserDetailsHasAuthorities_ShouldReturnAll() {
        // Подготовка
        final Employee userDetails = mock(Employee.class);
        when(userDetails.getAuthorities()).thenReturn(null);

        final Map<String, Map<String, Object>> expectedMap = Map.of();

        try (MockedStatic<ProfileNavigator> mockedStatic = mockStatic(ProfileNavigator.class)) {
            mockedStatic.when(ProfileNavigator::getAll).thenReturn(expectedMap);

            // Действие
            final Map<String, Map<String, Object>> result = profileController.getNavigator(userDetails);

            // Проверка
            assertNotNull(result);
            assertEquals(expectedMap, result);
        }
    }

    @Test
    void getNavigator_ShouldReturnNonEmptyMap() {
        try (MockedStatic<ProfileNavigator> mockedStatic = mockStatic(ProfileNavigator.class)) {
            mockedStatic.when(ProfileNavigator::getUnsecured).thenReturn(Map.of(
                    "_0_GENERAL_SCHEDULE", Map.of("label", "Расписание", "path", "/", "secured", false)
            ));
            mockedStatic.when(ProfileNavigator::getAll).thenReturn(Map.of(
                    "_0_GENERAL_SCHEDULE", Map.of("label", "Расписание", "path", "/", "secured", false),
                    "_1_CREATE_SCHEDULE", Map.of("label", "Составление расписания", "path", "/create-schedule", "secured", true)
            ));

            // Проверка неаутентифицированного
            final Map<String, Map<String, Object>> unsecuredResult = profileController.getNavigator(null);
            assertEquals(1, unsecuredResult.size());

            // Проверка аутентифицированного
            final Map<String, Map<String, Object>> allResult = profileController.getNavigator(mock(Employee.class));
            assertEquals(2, allResult.size());
        }
    }

}
