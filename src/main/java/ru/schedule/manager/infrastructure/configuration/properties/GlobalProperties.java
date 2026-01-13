package ru.schedule.manager.infrastructure.configuration.properties;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GlobalProperties {

	public static final String DEFAULT_API_PATH = "api/";

	public static final Pattern DEFAULT_API_REQUEST_PATTERN = Pattern.compile(
		"(?!/actuator|/webjars|/swagger-ui|/api|/_nuxt|/static|/index\\.html|/200\\.html|/favicon\\.ico|/sw\\.js).*$"
	);

	public static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:8080", "https://localhost:8080");

	public static final List<String> ALLOWED_METHODS = List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH");

	public static final List<String> ALLOWED_HEADERS = List.of("Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origin");

	public static final DateTimeFormatter DTO_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

}
