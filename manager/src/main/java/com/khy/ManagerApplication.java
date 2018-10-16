package com.khy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class ManagerApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ManagerApplication.class, args);
	}

}
