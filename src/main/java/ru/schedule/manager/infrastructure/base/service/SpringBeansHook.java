package ru.schedule.manager.infrastructure.base.service;

import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import ru.schedule.manager.infrastructure.configuration.annotations.TestAvoidGenerated;

/**
 * Получение статического доступа к Spring Bean
 */
@Component
@TestAvoidGenerated
public final class SpringBeansHook implements ApplicationContextAware {

    @Override
    @SneakyThrows
    public void setApplicationContext(final ApplicationContext applicationContext) {

    }

}

