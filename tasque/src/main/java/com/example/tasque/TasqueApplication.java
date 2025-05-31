package com.example.tasque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TasqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasqueApplication.class, args);
	}

}
