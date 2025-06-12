package com.gumeinteligenciacomercial.orcaja;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrcajaApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
		System.setProperty("URL_BD", dotenv.get("URL_BD"));
		System.setProperty("USER_BD", dotenv.get("USER_BD"));
		System.setProperty("PASSWORD_BD", dotenv.get("PASSWORD_BD"));
		System.setProperty("OPENI_API_KEY", dotenv.get("OPENI_API_KEY"));

		SpringApplication.run(OrcajaApplication.class, args);
	}

}
