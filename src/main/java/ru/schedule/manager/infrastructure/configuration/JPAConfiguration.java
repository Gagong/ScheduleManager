package ru.schedule.manager.infrastructure.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("ru.schedule.manager")
@EntityScan(basePackages = "ru.schedule.manager")
@EnableJpaRepositories(basePackages = "ru.schedule.manager")
public class JPAConfiguration {

}
