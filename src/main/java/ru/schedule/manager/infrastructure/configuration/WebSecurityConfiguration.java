package ru.schedule.manager.infrastructure.configuration;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.ALLOWED_HEADERS;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.ALLOWED_METHODS;
import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.ALLOWED_ORIGINS;

@Profile("!it")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

	@Bean
	public BasicAuthenticationEntryPoint apiAwareLoginUrlAuthenticationEntryPoint() {
		final BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		entryPoint.setRealmName("ScheduleManager");
		return entryPoint;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		final UserDetails user = User.builder()
				.username("voronenkov")
				.password(passwordEncoder().encode("admin"))
				.roles("ADMIN")
				.build();

		return new InMemoryUserDetailsManager(user);
	}

	@SneakyThrows
	@Bean
	public SecurityFilterChain filterChain(final HttpSecurity http) {
		return http
				.csrf()
				.disable()
				.cors()
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(apiAwareLoginUrlAuthenticationEntryPoint())
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers(
						"/swagger-ui.html",
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/swagger-resources/**",
						"/webjars/**"
				).permitAll()
				.anyRequest()
				.fullyAuthenticated()
				.and()
				.httpBasic(withDefaults())
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(8);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(ALLOWED_ORIGINS);
		configuration.setAllowedMethods(ALLOWED_METHODS);
		configuration.setAllowedHeaders(ALLOWED_HEADERS);
		configuration.setAllowCredentials(false);
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
