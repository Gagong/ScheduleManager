package ru.schedule.manager.it.exception;

import org.junit.jupiter.api.Test;
import ru.schedule.manager.business.exception.ExceptionMessageUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionMessageUtilsTest {

    @Test
    void of_ShouldFormatMessageWithSingleArgument() {
        final String result = ExceptionMessageUtils.of(
                ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN,
                "User", 123L
        );

        assertEquals("Entity [User] with ID [123] not found", result);
    }

    @Test
    void of_ShouldFormatMessageWithMultipleArguments() {
        final String result = ExceptionMessageUtils.of(
                ExceptionMessageUtils.DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN,
                "PROFESSOR", "john.doe"
        );

        assertEquals("Dictionary with TYPE [PROFESSOR] and KEY [john.doe] not found", result);
    }

    @Test
    void constants_ShouldBeDefined() {
        assertNotNull(ExceptionMessageUtils.ENTITY_NOT_FOUND_EXCEPTION_PATTERN);
        assertNotNull(ExceptionMessageUtils.DICTIONARY_VALUE_NOT_FOUND_EXCEPTION_PATTERN);
        assertNotNull(ExceptionMessageUtils.DICTIONARY_KEY_NOT_FOUND_EXCEPTION_PATTERN);
    }

}
