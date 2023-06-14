package ru.schedule.manager.infrastructure.configuration;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import static ru.schedule.manager.infrastructure.configuration.properties.GlobalProperties.DEFAULT_API_REQUEST_PATTERN;

@Slf4j
@Configuration
public class RedirectFilterConfiguration {

	@SuppressWarnings("rawtypes")
	@Bean
	public FilterRegistrationBean redirectFiler() {
		final FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(createRedirectFilter());
		registration.addUrlPatterns("/*");
		registration.setName("frontendRedirectFiler");
		registration.setOrder(1);
		return registration;
	}

	private OncePerRequestFilter createRedirectFilter() {
		return new OncePerRequestFilter() {

			@SuppressWarnings("NullableProblems")
			@Override
			protected void doFilterInternal(final HttpServletRequest req,
											final HttpServletResponse res,
											final FilterChain chain) throws ServletException, IOException {
				if (DEFAULT_API_REQUEST_PATTERN.matcher(req.getRequestURI()).matches() && !req.getRequestURI().equals("/")) {
					log.info("URL {} entered directly into the Browser, redirecting...", req.getRequestURI());
					req.getRequestDispatcher("/").forward(req, res);
				} else {
					chain.doFilter(req, res);
				}
			}
		};

	}

}
