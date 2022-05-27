package com.shenhui.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// --spring.profiles.active=dev
		SpringApplication.run(Application.class, args);
	}

}
