package ru.schedule.manager.it.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.SocketUtils;

public class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(final ConfigurableApplicationContext applicationContext) {
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
			applicationContext,
			"HTTP_SERVER_PORT=" + SocketUtils.findAvailableTcpPort(8000, 8900)
		);
	}

}
