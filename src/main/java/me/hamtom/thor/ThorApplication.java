package me.hamtom.thor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThorApplication.class, args);
		System.out.println("Hello! Thor!");
	}

}
