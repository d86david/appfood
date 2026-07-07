package com.dsys.appfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Habilita o processamento assícrono com @Async
public class AppfoodApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppfoodApplication.class, args);
	}

}
