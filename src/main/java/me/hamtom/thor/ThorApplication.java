package me.hamtom.thor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ThorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThorApplication.class, args);
		System.out.println("Hello! Thor!");
	}

}
