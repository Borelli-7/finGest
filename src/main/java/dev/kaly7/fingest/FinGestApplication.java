package dev.kaly7.fingest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "dev.kaly7.fingest.entities")
public class FinGestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinGestApplication.class, args);
	}

}
