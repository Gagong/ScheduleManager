package ru.schedule.manager.it;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ru.schedule.manager.it.configuration.RandomPortInitializer;

@Slf4j
@ActiveProfiles("it")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = RandomPortInitializer.class)
class ScheduleManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
