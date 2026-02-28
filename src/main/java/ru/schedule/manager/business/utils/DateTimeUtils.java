package ru.schedule.manager.business.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class DateTimeUtils {

    public static boolean isAfterOrEquals(final LocalDate date, final LocalDate target) {
        return date.isAfter(target) || date.isEqual(target);
    }

    public static boolean isBeforeOrEquals(final LocalDate date, final LocalDate target) {
        return date.isBefore(target) || date.isEqual(target);
    }

}
