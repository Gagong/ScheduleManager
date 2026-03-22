package ru.schedule.manager.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.schedule.manager.infrastructure.configuration.annotations.TestAvoidGenerated;

import static org.springframework.security.config.Customizer.withDefaults;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.ALLOWED_HEADERS;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.ALLOWED_METHODS;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.ALLOWED_ORIGINS;

@Profile("!it")
@Configuration
@EnableWebSecurity
@TestAvoidGenerated
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfiguration {

	/**
	 * Настройка CORS
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(ALLOWED_ORIGINS);
		configuration.setAllowedMethods(ALLOWED_METHODS);
		configuration.setAllowedHeaders(ALLOWED_HEADERS);
		configuration.setAllowCredentials(true);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * Настройка Basic Authentication Entry Point
	 */
	@Bean
	public BasicAuthenticationEntryPoint authenticationEntryPoint() {
		final BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		entryPoint.setRealmName("Schedule Manager API");
		return entryPoint;
	}

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
				// Отключаем CSRF для REST API
				.csrf().disable()

				// Настраиваем CORS
				.cors().configurationSource(corsConfigurationSource()).and()

				// Настраиваем обработку исключений
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint())
				.and()

				// Stateless сессии
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()

				// Настройка авторизации запросов
				.authorizeRequests()
				// Swagger
				.antMatchers(
						"/swagger-ui.html",
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/swagger-resources/**",
						"/webjars/**"
				).permitAll()

				// Публичные GET запросы
				.antMatchers(HttpMethod.GET,
						"/api/dictionary/**",
						"/api/schedule/**",
						"/api/professor/**",
						"/api/profile/**"
				).permitAll()

				// Публичные POST запросы
				.antMatchers(HttpMethod.POST,
						"/api/schedule/getSchedule",
						"/api/schedule/getFreeClassRoomsAndProfessors",
						"/api/professor/getProfessorDisciplines",
						"/api/professor/getProfessorDepartment",
						"/api/professor/getDepartmentProfessors"
				).permitAll()

				// Аутентификация
				.antMatchers("/api/employee/**").permitAll()

				// Регистрация только для админов
				.antMatchers("/api/profile/register").hasRole("ADMIN")

				//WebSocket
				.antMatchers("/ws").permitAll()
				.antMatchers("/wss").permitAll()
				.antMatchers("/ws/**").permitAll()
				.antMatchers("/wss/**").permitAll()

				// Все остальные запросы требуют аутентификации
				.anyRequest().authenticated()
				.and()

				// Базовая HTTP аутентификация
				.httpBasic(withDefaults())

				.build();
	}

}
