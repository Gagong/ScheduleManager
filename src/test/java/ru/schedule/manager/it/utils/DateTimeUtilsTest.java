package ru.schedule.manager.it.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.schedule.manager.business.utils.DateTimeUtils;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilsTest {

    @ParameterizedTest
    @MethodSource("provideDatesForAfterOrEquals")
    void isAfterOrEquals_ShouldReturnCorrectResult(final LocalDate date, final LocalDate target, final boolean expected) {
        assertEquals(expected, DateTimeUtils.isAfterOrEquals(date, target));
    }

    @ParameterizedTest
    @MethodSource("provideDatesForBeforeOrEquals")
    void isBeforeOrEquals_ShouldReturnCorrectResult(final LocalDate date, final LocalDate target, final boolean expected) {
        assertEquals(expected, DateTimeUtils.isBeforeOrEquals(date, target));
    }

    private static Stream<Arguments> provideDatesForAfterOrEquals() {
        final LocalDate now = LocalDate.now();
        return Stream.of(
                Arguments.of(now.plusDays(1), now, true),
                Arguments.of(now, now, true),
                Arguments.of(now.minusDays(1), now, false)
        );
    }

    private static Stream<Arguments> provideDatesForBeforeOrEquals() {
        final LocalDate now = LocalDate.now();
        return Stream.of(
                Arguments.of(now.minusDays(1), now, true),
                Arguments.of(now, now, true),
                Arguments.of(now.plusDays(1), now, false)
        );
    }

}
