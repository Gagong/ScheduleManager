package ru.schedule.manager.infrastructure.base.dictionary.administered.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.schedule.manager.business.dictionary.AdministeredDictionaryType;
import ru.schedule.manager.infrastructure.base.dictionary.administered.dto.DictionaryDto;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static ru.schedule.manager.business.dictionary.SemesterType.AUTUMN;
import static ru.schedule.manager.business.dictionary.SemesterType.SPRING;

@Slf4j
@Service
@Profile("!it")
@RequiredArgsConstructor
public class UpsertSemesterService {

    private final AdministeredDictionaryService administeredDictionaryService;

    @PostConstruct
    public void fillSemesters() {
        for (int i = LocalDate.now().getYear() - 10; i < LocalDate.now().getYear() + 100; i++) {
            try {
                administeredDictionaryService.create(
                        DictionaryDto.builder()
                                .type(AdministeredDictionaryType.SEMESTER.name())
                                .key(AUTUMN.name() + "_" + i + "_" + (i + 1))
                                .value(AUTUMN.getValue() + " " + i + "/" + (i + 1))
                                .build()
                );
            } catch (final UnsupportedOperationException e) {
                //Skip AlreadyExistsException
            }
            try {
                administeredDictionaryService.create(
                        DictionaryDto.builder()
                                .type(AdministeredDictionaryType.SEMESTER.name())
                                .key(SPRING.name() + "_" + i + "_" + (i + 1))
                                .value(SPRING.getValue() + " " + i + "/" + (i + 1))
                                .build()
                );
            } catch (final UnsupportedOperationException e) {
                //Skip AlreadyExistsException
            }
        }
    }

}
