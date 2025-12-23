package com.marcosespeche.spring_batch_poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBatchPocApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBatchPocApplication.class, args);

		String port = context.getEnvironment().getProperty("server.port", "8080");
		System.out.println("Running application on port " + port);
	}
}
