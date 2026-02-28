package ru.schedule.manager.business.dictionary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SemesterType {

    AUTUMN("Осенний семестр"),
    SPRING("Весенний семестр");

    private final String value;

}
