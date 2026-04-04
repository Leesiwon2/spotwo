package com.spotwo.spotwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpotwoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotwoApplication.class, args);
	}

}
