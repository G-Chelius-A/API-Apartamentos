package com.blue.apartamentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Clase principal — punto de entrada de la aplicación Spring Boot
@SpringBootApplication
public class ApartamentosApplication {

	// Lanza el contexto de Spring y arranca el servidor embebido
	public static void main(String[] args) {
		SpringApplication.run(ApartamentosApplication.class, args);
	}

}
