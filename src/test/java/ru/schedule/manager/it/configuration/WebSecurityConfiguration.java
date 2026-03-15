package ru.schedule.manager.it.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Primary
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfiguration {

	/**
	 * Кодировщик паролей
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(8);
	}

	/**
	 * AuthenticationManager
	 * Получаем его через AuthenticationConfiguration
	 */
	@Bean
	public AuthenticationManager authenticationManager(final AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/**
	 * Цепочка фильтров безопасности
	 */
	@Bean
	public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll())
				.build();
	}

}
