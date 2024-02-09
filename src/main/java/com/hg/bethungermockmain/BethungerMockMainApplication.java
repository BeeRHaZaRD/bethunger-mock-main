package com.hg.bethungermockmain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableScheduling
public class BethungerMockMainApplication {
	@Value("${jwt.token}")
	private String jwtToken;

	@Value("${base_url}")
	private String baseUrl;

	public static void main(String[] args) {
		SpringApplication.run(BethungerMockMainApplication.class, args);
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader("Authorization", "Bearer " + jwtToken)
			.build();
	}
}
