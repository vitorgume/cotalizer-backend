package com.gumeinteligenciacomercial.orcaja;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OrcajaApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
		System.setProperty("OPENIA_API_KEY", dotenv.get("OPENIA_API_KEY"));
		System.setProperty("SECRET_KEY", dotenv.get("SECRET_KEY"));

		SpringApplication.run(OrcajaApplication.class, args);
	}

}
